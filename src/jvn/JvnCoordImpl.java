/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:
 * Authors: 
 */

package jvn;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	

  /**
   * JvnCoordImpls attributes :
   *  - namingService is a couple of hashmaps used to associate a name to an object ID.
   *    Since a name, you can retrieve object ID and vice-versa
   *  - readersFromId is a hashmap used to associate an object ID with all readers (servers)
   *  - writersFromId is a hashmap used to associate an object ID with a reader (server)
   *  - sharedObjects is a hashmap used to associate an object ID with a shared object
   */
	@Serial
    private static final long serialVersionUID = 1L;
    private Integer idSeq;
    private JvnNamingService namingService;
    private HashMap<Integer, List<JvnRemoteServer>> readersFromId;
    private HashMap<Integer, JvnRemoteServer> writersFromId;
    private HashMap<Integer, Serializable> sharedObjects;

    private final String statesDir = "CoordStates/";

/**
  * Default constructor
 **/
	private JvnCoordImpl() throws Exception {
        try {
            // try to retrive last states if they exist
            restoreStates();
        } catch (JvnException e){
            // Cannot retrieve last states, so we create new objects
            idSeq = -1;
            namingService = new JvnNamingService();
            readersFromId = new HashMap<>();
            writersFromId = new HashMap<>();
            sharedObjects = new HashMap<>();
        }
	}

    public static void main(String[] argv) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("Coord", new JvnCoordImpl());
            System.out.println("Coordinator ready");
        } catch (Exception e) {
            System.err.println("Coordinator initialization problem :"+ e.getMessage());
        }

    }

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
   **/
  public synchronized int jvnGetObjectId()
  throws java.rmi.RemoteException,jvn.JvnException {
    idSeq++;
    return idSeq;
  }
  
  /**
  * Associate a symbolic name with a JVN object.
   * Use the JvnNamingService.
  * @param jon : the JVN object name
  * @param jo  : the JVN object
  * @param js  : the remote reference of the JVNServer
   **/
  public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    namingService.addName(jon, jo.jvnGetObjectId());
    readersFromId.put(jo.jvnGetObjectId(), new ArrayList<>());
    writersFromId.put(jo.jvnGetObjectId(), null);
    sharedObjects.put(jo.jvnGetObjectId(), jo.jvnGetSharedObject());
    saveStates();
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
   **/
  public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    if (!namingService.containsString(jon))
        return null;
    Integer id = namingService.getId(jon);
    return new JvnObjectImpl(id, sharedObjects.get(id));
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
       // Check if there is a writer
       Serializable obj = sharedObjects.get(joi);
        if (writersFromId.get(joi) != null) {
            // If the writer is connected
            try {
                // then we invalidate him and get the new object
                obj = writersFromId.get(joi).jvnInvalidateWriterForReader(joi);
                sharedObjects.put(joi, obj);
            } catch (RemoteException e){
                // else we return the unmodified object
                System.out.println("Client connexion lost");
            }
            // and we remove the writer from the list
            writersFromId.put(joi, null);
        }
        readersFromId.get(joi).add(js);
        saveStates();
        return obj;
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
       // Invalidate writer
       Serializable obj = sharedObjects.get(joi);
       if (writersFromId.get(joi) != null){
           try {
               obj = writersFromId.get(joi).jvnInvalidateWriter(joi);
               writersFromId.put(joi, null);
               sharedObjects.put(joi, obj);
           } catch (RemoteException e){
               System.out.println("Client connexion lost");
           }
           writersFromId.put(joi, null);
       }

       Iterator<JvnRemoteServer> serverIterator = readersFromId.get(joi).iterator();

       // Invalidate readers
       while (serverIterator.hasNext()){
           JvnRemoteServer server = serverIterator.next();
           try {
               server.jvnInvalidateReader(joi);
           } catch (RemoteException e){
               System.out.println("Client connexion lost");
           }
           serverIterator.remove();
       }
       writersFromId.put(joi, js);
       saveStates();
       return obj;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public synchronized void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
         for (Integer id: sharedObjects.keySet()){
             if (writersFromId.replace(id, js, null))
                 sharedObjects.put(id, (Serializable) js.jvnInvalidateWriter(id));
             readersFromId.get(id).removeIf(server -> server == js);
         }
         saveStates();
    }

    /**
     * Coordinator's crash management, used to frequently save the coordinator
     * overall state on disk.
     **/
    private void saveStates(){
        try (
                ObjectOutputStream osnaming = new ObjectOutputStream(new FileOutputStream(statesDir+"namingService.sta"));
                ObjectOutputStream osobjs = new ObjectOutputStream(new FileOutputStream(statesDir+"sharedObjects.sta"));
                ObjectOutputStream osreaders = new ObjectOutputStream(new FileOutputStream(statesDir+"readersFromId.sta"));
                ObjectOutputStream oswriters = new ObjectOutputStream(new FileOutputStream(statesDir+"writersFromId.sta"));
                ObjectOutputStream osids = new ObjectOutputStream(new FileOutputStream(statesDir+"lastIdSeq.sta"))
        ){
            osnaming.writeObject(namingService);
            osobjs.writeObject(sharedObjects);
            osreaders.writeObject(readersFromId);
            oswriters.writeObject(writersFromId);
            osids.writeObject(idSeq);
        }
        catch (IOException e){
            System.out.println("Not able to save states");
            e.printStackTrace();
        }
    }

    /**
     * Coordinator's crash management, used to retrieve last coordinator's states
     * since last crash using the disk memory.
     */
    private void restoreStates() throws JvnException {
        try (
                ObjectInputStream osnaming = new ObjectInputStream(new FileInputStream(statesDir+"namingService.sta"));
                ObjectInputStream osobjs = new ObjectInputStream(new FileInputStream(statesDir+"sharedObjects.sta"));
                ObjectInputStream osreaders = new ObjectInputStream(new FileInputStream(statesDir+"readersFromId.sta"));
                ObjectInputStream oswriters = new ObjectInputStream(new FileInputStream(statesDir+"writersFromId.sta"));
                ObjectInputStream osids = new ObjectInputStream(new FileInputStream(statesDir+"lastIdSeq.sta"))
        ) {
            namingService = (JvnNamingService) osnaming.readObject();
            sharedObjects = (HashMap<Integer, Serializable>) osobjs.readObject();
            readersFromId = (HashMap<Integer, List<JvnRemoteServer>>) osreaders.readObject();
            writersFromId = (HashMap<Integer, JvnRemoteServer>) oswriters.readObject();
            idSeq = (Integer) osids.readObject();
        } catch (IOException e) {
            System.out.println("Unable to restore states : files problem : " + e.getMessage());
            throw new JvnException();
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to restore states : reading files problem : " + e.getMessage());
            throw new JvnException();
        }
    }

}

 

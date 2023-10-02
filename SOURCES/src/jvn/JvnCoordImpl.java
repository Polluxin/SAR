/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:  
 *
 * Authors: 
 */

package jvn;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class JvnCoordImpl 	
              extends UnicastRemoteObject 
							implements JvnRemoteCoord{
	

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Integer idSeq;
    private JvnNamingService namingService;
    private HashMap<Integer, List<JvnRemoteServer>> readersFromId;
    private HashMap<Integer, JvnRemoteServer> writersFromId;
    private HashMap<Integer, JvnObject> sharedObjects;

/**
  * Default constructor
  * @throws JvnException
  **/
	private JvnCoordImpl() throws Exception {
        idSeq = -1;
		namingService = new JvnNamingService();
        readersFromId = new HashMap<>();
        writersFromId = new HashMap<>();
        sharedObjects = new HashMap<>();
	}

    public static void main(String argv[]) {
        try {
            Naming.rebind("rmi://localhost/Coord", new JvnCoordImpl());
            System.out.println("Coordinator ready");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

  /**
  *  Allocate a NEW JVN object id (usually allocated to a 
  *  newly created JVN object)
  * @throws java.rmi.RemoteException,JvnException
  **/
  public int jvnGetObjectId()
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
  * @throws java.rmi.RemoteException,JvnException
  **/
  public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    namingService.addName(jon, jo.jvnGetObjectId());
    readersFromId.put(jo.jvnGetObjectId(), new ArrayList<>());
    writersFromId.put(jo.jvnGetObjectId(), null);
    sharedObjects.put(jo.jvnGetObjectId(), jo);
  }
  
  /**
  * Get the reference of a JVN object managed by a given JVN server 
  * @param jon : the JVN object name
  * @param js : the remote reference of the JVNServer
  * @throws java.rmi.RemoteException,JvnException
  **/
  public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
  throws java.rmi.RemoteException,jvn.JvnException{
    if (!namingService.containsString(jon))
        throw new JvnException("Unknown object named "+jon);
    return sharedObjects.get(namingService.getId(jon));
  }
  
  /**
  * Get a Read lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockRead(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
    // to be completed
    return null;
   }

  /**
  * Get a Write lock on a JVN object managed by a given JVN server 
  * @param joi : the JVN object identification
  * @param js  : the remote reference of the server
  * @return the current JVN object state
  * @throws java.rmi.RemoteException, JvnException
  **/
   public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
   throws java.rmi.RemoteException, JvnException{
    // to be completed
    return null;
   }

	/**
	* A JVN server terminates
	* @param js  : the remote reference of the server
	* @throws java.rmi.RemoteException, JvnException
	**/
    public void jvnTerminate(JvnRemoteServer js)
	 throws java.rmi.RemoteException, JvnException {
	 for (Integer id: sharedObjects.keySet()){
         if (writersFromId.get(id) == js)
             writersFromId.put(id, null);
         readersFromId.get(id).removeIf(server -> server == js);
     }
    }
}

 

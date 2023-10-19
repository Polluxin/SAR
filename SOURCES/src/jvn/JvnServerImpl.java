/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact:
 * Authors: 
 */

package jvn;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.HashMap;


public class JvnServerImpl
              extends UnicastRemoteObject 
							implements JvnLocalServer, JvnRemoteServer{ 
	
  /**
	 *
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	private final JvnRemoteCoord jvnRemoteCoord;
	private final HashMap<Integer, JvnObject> jvnObjectHashMap;
	private final int CACHE_SIZE = 5;

  /**
  * Default constructor
  **/
	private JvnServerImpl() throws MalformedURLException, NotBoundException, RemoteException {
		super();
		// Find reference to jvnRemoteCoord
		jvnRemoteCoord = (JvnRemoteCoord) LocateRegistry.getRegistry().lookup("Coord");
		assert(jvnRemoteCoord != null);
//		jvnRemoteCoord = (JvnRemoteCoord) Naming.lookup("rmi://localhost/Coord");
		jvnObjectHashMap = new HashMap<>();
	}
	
  /**
    * Static method allowing an application to get a reference to 
    * a JVN server instance
    **/
	public static JvnServerImpl jvnGetServer() throws JvnException {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				throw new JvnException("JvnGetServer: Cannot get the server instance: "+ e.getMessage());
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	**/
	public  void jvnTerminate()
	throws jvn.JvnException {
		try {
			jvnRemoteCoord.jvnTerminate(this);
		} catch (RemoteException e){
			System.out.println("Coordinator connection problem : "+e.getMessage());
		}
	}

	@Override
	public void jvnClearObjectsCache() {

	}

	private void clearCache(){
		int n = jvnObjectHashMap.size();
		try {
			jvnTerminate();
		} catch (JvnException e) {
			System.out.println("Coordinator problem : "+e.getMessage());
		}
		jvnObjectHashMap.clear();
		System.out.println("LocalServer's cache cleared ("+ n +"objects cleared), coordinator notified");
	}

	/**
	* creation of a JVN object
	* @param o : the JVN object state
	**/
	public  JvnObject jvnCreateObject(Serializable o)
	throws jvn.JvnException { 
		try {
			return new JvnObjectImpl(jvnRemoteCoord.jvnGetObjectId(), o);
		} catch (RemoteException e){
			throw new JvnException("jvnCreateObject: "+e.getMessage());
		}
	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {
		// Object cache management
		if (jvnObjectHashMap.size() > CACHE_SIZE)
			clearCache();

		try {
			jvnRemoteCoord.jvnRegisterObject(jon, jo, this);
			// Save object reference
			jvnObjectHashMap.put(jo.jvnGetObjectId(), jo);
		} catch (RemoteException e){
			throw new JvnException("jvnRegisterObject: "+e.getMessage());
		}
	}
	
	/**
	* Provide the reference of a JVN object beeing given its symbolic name
	* @param jon : the JVN object name
	* @return the JVN object
	**/
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
		// Object cache management
		if (jvnObjectHashMap.size() > CACHE_SIZE)
			clearCache();

		try {
			JvnObject obj =  jvnRemoteCoord.jvnLookupObject(jon, this);
			// Save object reference
			if (obj != null) {
				jvnObjectHashMap.put(obj.jvnGetObjectId(), obj);
			}
			return obj;
		} catch (RemoteException e){
			throw new JvnException("jvnLookupObject: "+e.getMessage());
		}
	}	

	/**
	* Get a Read lock on a JVN object.
	 * Called by JvnObject.
	* @param joi : the JVN object identification
	* @return the current JVN object state
	**/
   public Serializable jvnLockRead(int joi)
	 throws JvnException {
	   try {
		   return jvnRemoteCoord.jvnLockRead(joi, this);
	   } catch (RemoteException e){
		   throw new JvnException("jvnLockRead: "+e.getMessage());
	   }
	}

	/**
	* Get a Write lock on a JVN object
	 * Called by JvnObject.
	* @param joi : the JVN object identification
	* @return the current JVN object state
	**/
   public Serializable jvnLockWrite(int joi)
	 throws JvnException {
	   try {
		   return jvnRemoteCoord.jvnLockWrite(joi, this);
	   } catch (RemoteException e){
		   throw new JvnException("jvnLockWrite: "+e.getMessage());
	   }
	}	

	
  /**
	* Invalidate the Read lock of the JVN object identified by id 
	* called by the JvnCoord
	* @param joi : the JVN object id
	**/
  public void jvnInvalidateReader(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
		JvnObject jvnObject = jvnObjectHashMap.get(joi);
		System.out.println("JvnObject jvnInvalidateReader");
		jvnObject.jvnInvalidateReader();
	}
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
	  JvnObject jvnObject = jvnObjectHashMap.get(joi);
	  System.out.println("JvnObject jvnInvalidateWriter");
	  return jvnObject.jvnInvalidateWriter();
	}
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException {
	   JvnObject jvnObject = jvnObjectHashMap.get(joi);
	   System.out.println("JvnObject jvnInvalidateWriterForReader");
	   return jvnObject.jvnInvalidateWriterForReader();
	 }

}

 

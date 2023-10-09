/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.net.MalformedURLException;
import java.rmi.Naming;
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
	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	private final JvnRemoteCoord jvnRemoteCoord;
	private final HashMap<Integer, JvnObject> jvnObjectHashMap;

  /**
  * Default constructor
  * @throws JvnException
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
    * @throws JvnException
    **/
	public static JvnServerImpl jvnGetServer() throws JvnException {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				throw new JvnException("Cannot get the server instance: "+ e.getMessage());
			}
		}
		return js;
	}
	
	/**
	* The JVN service is not used anymore
	* @throws JvnException
	**/
	public  void jvnTerminate()
	throws jvn.JvnException {
		try {
			jvnRemoteCoord.jvnTerminate(this);
		} catch (RemoteException e){
			e.printStackTrace();
		}
	}
	
	/**
	* creation of a JVN object
	* @param o : the JVN object state
	* @throws JvnException
	**/
	public  JvnObject jvnCreateObject(Serializable o)
	throws jvn.JvnException { 
		try {
			assert(jvnRemoteCoord != null);
			return new JvnObjectImpl(jvnRemoteCoord.jvnGetObjectId(), o, this);
		} catch (RemoteException e){
			throw new JvnException("jvnCreateObject: "+e.getMessage());
		}
	}
	
	/**
	*  Associate a symbolic name with a JVN object
	* @param jon : the JVN object name
	* @param jo : the JVN object 
	* @throws JvnException
	**/
	public  void jvnRegisterObject(String jon, JvnObject jo)
	throws jvn.JvnException {

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
	* @throws JvnException
	**/
	public  JvnObject jvnLookupObject(String jon)
	throws jvn.JvnException {
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
	* @throws  JvnException
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
	* @throws  JvnException
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
	* @return void
	* @throws java.rmi.RemoteException,JvnException
	**/
  public void jvnInvalidateReader(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
		JvnObject jvnObject = jvnObjectHashMap.get(joi);
		jvnObject.jvnInvalidateReader(); // /!\ May not be the good way
		jvnRemoteCoord.notify();
	};
	    
	/**
	* Invalidate the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
  public Serializable jvnInvalidateWriter(int joi)
	throws java.rmi.RemoteException,jvn.JvnException {
	  JvnObject jvnObject = jvnObjectHashMap.get(joi);
	  jvnObject.jvnInvalidateWriter(); // /!\ May not be the good way
		return null;
	};
	
	/**
	* Reduce the Write lock of the JVN object identified by id 
	* @param joi : the JVN object id
	* @return the current JVN object state
	* @throws java.rmi.RemoteException,JvnException
	**/
   public Serializable jvnInvalidateWriterForReader(int joi)
	 throws java.rmi.RemoteException,jvn.JvnException {
	   JvnObject jvnObject = jvnObjectHashMap.get(joi);
	   jvnObject.jvnInvalidateWriterForReader(); // /!\ May not be the good way
		return null;
	 };

}

 

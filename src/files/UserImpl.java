package files;

import java.io.File;
import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


import security.Key;
import security.KeyPair;

public class UserImpl extends UnicastRemoteObject implements Serializable, User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private String password;
	private MyDirectoryImpl path;
	private KeyPair keys;
	
	public UserImpl(String email, String password) throws RemoteException {
		super();
		this.email = email;
		this.password = password;
		keys=KeyPair.createKeyPair();
	}

	public String getEmail() throws RemoteException {
		return email;
	}

	public String getPassword()throws RemoteException {
		return password;
	}
	
	public MyDirectory getPath()  throws RemoteException{
		path.refresh();
		return path;
	}
	
	public Remote getPathRemote() throws RemoteException, NoSuchObjectException{
		return UnicastRemoteObject.toStub(path);
	}

	public void setPath(String string) throws RemoteException {
		this.path = new MyDirectoryImpl(new File(string), this,this);
		
	}

	public Key getPublicKey() {
		return keys.getPublic();
	}
	
	protected Key getPrivateKey(){
		return keys.getPrivate();
	}
}

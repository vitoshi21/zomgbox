package files;

import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;


import security.Key;

public interface User extends Remote,Serializable {

	public String getEmail()throws RemoteException;

	public String getPassword()throws RemoteException;
	
	public Key getPublicKey()throws RemoteException;
	
	//public Key getPrivateKey()throws RemoteException;
	
	public Remote getPathRemote() throws RemoteException, NoSuchObjectException;

	public MyDirectory getPath() throws RemoteException;

	public void setPath(String string) throws RemoteException;
}

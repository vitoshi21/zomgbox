package client;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;


import files.MyFile;
import files.User;


public interface Client extends Remote {
	public void sync()throws RemoteException, IOException;
	public MyFile getFile(String path,MyFile file)throws RemoteException,IOException;
	public User getUser() throws RemoteException;
	public boolean login(User user) throws RemoteException;
	public boolean register(User user) throws RemoteException;
}

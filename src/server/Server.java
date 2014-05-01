package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;


import files.MyFile;
import files.User;


public interface Server extends Remote{
	public Map<String, MyFile> getFileList(User remoteUser)throws RemoteException;
	public void addPeer(Server s)throws RemoteException, UnknownHostException;
	public void removePeer(Server s)throws RemoteException;
	public List<Server> getServers()throws RemoteException;
	public InetAddress getAddress() throws RemoteException,UnknownHostException;
	public boolean login(User user) throws RemoteException;
	public boolean register(User user) throws RemoteException;
	public User getUser(String user, int tries) throws RemoteException;
	public boolean addUser(User newUser) throws RemoteException;
	public User getOwner() throws RemoteException;
	public void share(User newUser, boolean read, boolean write, MyFile shareFile) throws RemoteException;
}

package files;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;


public interface MyFile extends Remote, Serializable {
	
	public String getName() throws RemoteException;
	public long getLastModified() throws RemoteException;
	public boolean isDirectory()throws RemoteException;
	public String getAbsolutePath() throws RemoteException;
	public long getLength() throws RemoteException;
	public byte[] downloadFile() throws RemoteException,FileNotFoundException;
	public InetAddress getAddress() throws RemoteException,UnknownHostException;
	public User getOwner() throws RemoteException;
	public Map<String, User> getCanRead() throws RemoteException;
	public Map<String, User> getCanWrite() throws RemoteException;
	public void updatePermissions(User user) throws RemoteException;
	public void setReadable(boolean flag, User newUser) throws RemoteException;
	public void setWritable(boolean flag, User newUser) throws RemoteException;
	public boolean canRead(User user) throws RemoteException;
	public boolean canWrite(User user) throws RemoteException;
	public void setLastModified(long lastModified) throws RemoteException;
}

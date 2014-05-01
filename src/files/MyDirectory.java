package files;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import server.Server;



public interface MyDirectory extends MyFile, Remote, Serializable {
	public Map<String, MyFile> getFiles() throws RemoteException;
	public Map<String, MyFile> getFilesToRequest(List<Server> peers, Server myserver)throws RemoteException;
	public MyFile find(MyFile searchFile) throws RemoteException;
	public void addFile(String path, MyFile myFile) throws RemoteException;
	public void refresh() throws RemoteException;
}

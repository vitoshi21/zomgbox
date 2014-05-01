package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import files.MyDirectory;
import files.MyFile;
import files.User;
import files.UserImpl;

import security.Key;
import security.SecClientSocketFactory;
import security.SecServerSocketFactory;

public class ServerImpl extends UnicastRemoteObject implements Server {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	private List<Server> peers;
	private Map<String, User> allUsers;
	//private Key serverkey;
	//private String path;
	
	public ServerImpl(User user,Key serverkey) throws RemoteException {
		super(0, new SecClientSocketFactory(serverkey), new SecServerSocketFactory(serverkey));
		//super();
		this.user=user;
		//System.out.println("SERVER + " + user.getPath());
		peers=new LinkedList<Server>();
		allUsers = new HashMap<String, User>();
		//this.serverkey=serverkey;

	}
	
	public ServerImpl(Key serverkey) throws RemoteException {
		super(0, new SecClientSocketFactory(serverkey), new SecServerSocketFactory(serverkey));
		//super();
		peers=new LinkedList<Server>();
		allUsers = new HashMap<String, User>();
		//this.serverkey=serverkey;
	}
	
	public boolean login(User user) throws RemoteException {
		User searchUser = allUsers.get(user.getEmail());
		if (searchUser == null)
			return false;
		if (searchUser.getEmail().equals(user.getEmail()) && searchUser.getPassword().equals(user.getPassword())) {
			this.user = user;
			return true;
		}
		return false;
	}
	
	public boolean register(User user) throws RemoteException {
		if (allUsers.containsKey(user.getEmail()))
			return false;
		allUsers.put(user.getEmail(), user);
		return true;
	}

	@Override
	public Map<String, MyFile> getFileList(User remoteUser) throws RemoteException {
		user.getPath().refresh();
		if (user.getPath().canRead(remoteUser))
			return user.getPath().getFiles();
		return new HashMap<String, MyFile>();
	}


	@Override
	public void addPeer(Server s) throws RemoteException, UnknownHostException {
		peers.add(s);
		System.err.println("added "+s.getAddress().getHostName());
	}

	@Override
	public List<Server> getServers() throws RemoteException {
		return peers;
	}
	
	public String toString(){
		return "super server here";
	}
	
	public void removePeer(Server s) throws RemoteException {
		peers.remove(s);
	}
	
	public InetAddress getAddress() throws RemoteException,UnknownHostException{
		return InetAddress.getLocalHost();
	}
	
	public boolean addUser(User newUser) throws RemoteException {
		User user = allUsers.get(newUser.getEmail());
		if (user != null)
			return false;
		allUsers.put(newUser.getEmail(), newUser);
		Iterator<Server> it = peers.iterator();
		while(it.hasNext()) {
			Server server = it.next();
			server.addUser(newUser);
		}
		return true;
	}
	
	public User getUser(String email, int tries) throws RemoteException {
		if(tries==0) return null;
		User user = allUsers.get(email);
		if (user == null) {
			Iterator<Server> it = peers.iterator();
			while(it.hasNext()) {
				Server server = it.next();
				user = server.getUser(email,tries-1);
				if (user != null) {
					user = new UserImpl(user.getEmail(), user.getPassword());
					addUser(user);
					return user;
				}
			}
		}
		return user;
	}
	
	public User getOwner() throws RemoteException {
		return user;
	}

	@Override
	public void share(User shareUser, boolean read, boolean write, MyFile shareFile) throws RemoteException {
		Iterator<Server> it = peers.iterator();
		while (it.hasNext()) {
			boolean propagate = false;
			Server peer = it.next();
			User ownz = peer.getOwner();
			MyDirectory root = ownz.getPath();
			MyFile targetFile = root.find(shareFile);
			if (targetFile != null) {
				if (targetFile.canRead(shareUser) != read) {
					targetFile.setReadable(read, shareUser);
					propagate = true;
				}
				if (targetFile.canWrite(shareUser) != write) {
					targetFile.setWritable(write, shareUser);
					propagate = true;
				}
			}
			if (propagate) {
				peer.share(shareUser, read, write, shareFile);
			}
		}
		
	}

}

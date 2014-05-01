package files;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


import security.SecClientSocketFactory;
import security.SecServerSocketFactory;

public class MyFileImpl extends UnicastRemoteObject implements MyFile, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	File file;
	long lastModified;
	User owner,localuser;
	Map<String, User> canRead;
	Map<String, User> canWrite;
	
	
	public MyFileImpl(File file, User owner, User localuser) throws RemoteException{
		super(0);
		this.file=file;
		this.owner = owner;
		this.localuser=localuser;
		this.lastModified=file.lastModified();
		canRead = new HashMap<String, User>();
		canWrite = new HashMap<String, User>();
		owner.getEmail();
		canRead.put(owner.getEmail(), owner);
		canWrite.put(owner.getEmail(), owner);
	}

	public MyFileImpl(File newfile, MyFile file2, User localuser) throws RemoteException {
		super(0);
		this.file = newfile;
		this.owner = file2.getOwner();
		this.localuser=localuser;
		canRead = file2.getCanRead();
		canWrite = file2.getCanWrite();
	}

	@Override
	public String getName() throws RemoteException {
		return file.getName();
	}


	@Override
	public long getLastModified() throws RemoteException {
		return file.lastModified();
	}

	public void setLastModified(long lastModified) throws RemoteException {
		file.setLastModified(lastModified);
	}
	

	@Override
	public boolean isDirectory() throws RemoteException{
		return file.isDirectory();
	}


	@Override
	public String getAbsolutePath() throws RemoteException{
		return file.getAbsolutePath();
	}

	@Override
	public long getLength() throws RemoteException {
		return file.length();
	}

	@Override
	public byte[] downloadFile() throws RemoteException, FileNotFoundException {
		 byte buffer[] = new byte[(int)file.length()];
        BufferedInputStream input = new
     BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
        try {
			input.read(buffer,0,buffer.length);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return(buffer);

	}

	@Override
	public InetAddress getAddress() throws RemoteException,UnknownHostException{
		return InetAddress.getLocalHost();
	}

	@Override
	public Map<String, User> getCanRead() {
		return canRead;
	}

	@Override
	public Map<String, User> getCanWrite() {
		return canWrite;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public void updatePermissions(User user) throws RemoteException {
		file.setWritable(true);
		file.setReadable(true);
	}

	@Override
	public void setReadable(boolean flag, User newUser) throws RemoteException {
		file.setReadable(flag);
		if(flag)
			canRead.put(newUser.getEmail(), newUser);
		else
			canRead.remove(newUser.getEmail());
	}

	@Override
	public void setWritable(boolean flag, User newUser) throws RemoteException {
		file.setWritable(flag);
		if (flag)
			canWrite.put(newUser.getEmail(), newUser);
		else
			canWrite.remove(newUser.getEmail());
	}

	@Override
	public boolean canRead(User user) throws RemoteException {
		return canRead.containsKey(user.getEmail());
	}

	@Override
	public boolean canWrite(User user) throws RemoteException {
		return canWrite.containsKey(user.getEmail());
	}
}

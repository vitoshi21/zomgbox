package files;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import security.SecClientSocketFactory;
import security.SecServerSocketFactory;
import server.Server;




public class MyDirectoryImpl extends MyFileImpl implements MyDirectory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String,MyFile> filelist;
	Map<String, User> sharedWith;



	public MyDirectoryImpl(File file, User owner,User localuser)throws RemoteException{
		super(file,owner,localuser);
		this.filelist=generateFileList();
	}

	public MyDirectoryImpl(File newFile, MyFile file2, User localuser) throws RemoteException {
		super(newFile, file2,localuser);
		canRead = file2.getCanRead();
		canWrite = file2.getCanWrite();
	}

	@Override
	public Map<String,MyFile> getFiles() throws RemoteException {
		return filelist;
	}

	@Override
	public long getLastModified() throws RemoteException {
		refresh();
		return file.lastModified();
	}


	private Map<String,MyFile> generateFileList() throws RemoteException{
		//refresh();
		Map<String,MyFile> filelist=new HashMap<String,MyFile>();
		File[] filearray=this.file.listFiles();
		if(filearray==null) return filelist;
		for(java.io.File file: filearray){
			
			MyFile searchFile = find(file);
			
			if (searchFile != null)
				filelist.put(file.getAbsolutePath(), searchFile);
			else if(file.isDirectory()) {
				filelist.put(file.getName(), new MyDirectoryImpl(file, owner, localuser));
			}
			else {
				filelist.put(file.getName(), new MyFileImpl(file, owner,localuser));
			}
		}
		return filelist;
	}

	private MyFile find(File searchFile) {
		if (filelist == null)
			return null;
		else {
			return filelist.get(searchFile.getAbsolutePath());
		}
	}
	
	public MyFile find(MyFile searchFile) throws RemoteException {
		if (filelist == null)
			return null;
		else {
			return filelist.get(searchFile);
		}
	}

	@Override
	public Map<String, MyFile> getFilesToRequest(List<Server> peers, Server server) throws RemoteException {
		refresh();
		List<Server> toRemove = new LinkedList<Server>();
		Map<String,MyFile> filestoreq=new HashMap<String,MyFile>();
		Iterator<Server> it=peers.iterator();
		while(it.hasNext()){
			Server peer=it.next();
			try {
				filestoreq.putAll(this.compare(peer.getFileList(owner)));
			}
			catch (RemoteException e) {
				toRemove.add(peer);
			}
		}
		it = toRemove.iterator();
		while(it.hasNext()) {
			server.removePeer(it.next());
		}
		return filestoreq;
	}

	public Map<String,MyFile> compare(Map<String,MyFile> otherdir) throws RemoteException{
		Map<String,MyFile> request=new HashMap<String,MyFile>();
		if (otherdir == null)
			return null;
		Iterator<Entry<String,MyFile>> it=otherdir.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,MyFile> e=it.next();
			String name=e.getKey();
			MyFile rfile=e.getValue();
			MyFile lfile=filelist.get(name);
			MyFile mapfile=request.get(this.file.getPath()+File.separator+name);
			
			if(lfile==null){
				//System.err.println(this.file.getPath()+File.separator+name+" does not exist");
				
				if(mapfile==null||mapfile.getLastModified()<rfile.getLastModified())request.put(this.file.getPath()+File.separator+name, rfile);
				else if(mapfile!=null&&mapfile.getLastModified()==rfile.getLastModified()) {double r=Math.random(); if(r<0.5) request.put(this.file.getPath()+File.separator+name, rfile);}
				if(rfile.isDirectory()) request=mergeMaps(request,new MyDirectoryImpl(new File(this.file.getPath()+File.separator+name), owner, localuser).compare(((MyDirectory)rfile).getFiles()));
			}
			else if(lfile.getLastModified()<rfile.getLastModified()){
				if(mapfile!=null&&mapfile.getLastModified()==rfile.getLastModified()) {double r=Math.random(); if(r<0.5) request.put(this.file.getPath()+File.separator+name, rfile);}
				else request.put(this.file.getPath()+File.separator+name, rfile);
			}
			
			else if(lfile.isDirectory()&&rfile.isDirectory()) request=mergeMaps(request,(((MyDirectoryImpl)lfile).compare(((MyDirectory)rfile).getFiles())));

		}
		return request;

	}

	private  Map<String,MyFile> mergeMaps( Map<String,MyFile> m1,  Map<String,MyFile> m2) throws RemoteException{
		Iterator<Entry<String,MyFile>> it=m2.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,MyFile> e=it.next();
			String path=e.getKey();
			MyFile f1=m1.get(e.getKey());
			MyFile f2=e.getValue();
			if(f1==null||f1.getLastModified()<f2.getLastModified()) m1.put(path, f2);
		}
		return m1;
	}
	
	public void refresh() throws RemoteException {
		this.filelist=generateFileList();
		this.lastModified = file.lastModified();
	}

	public void addFile(String path, MyFile myFile) {
		filelist.put(path, myFile);
	}
	
	@Override
	public void updatePermissions(User user) throws RemoteException {
		file.setWritable(canWrite.containsKey(user.getEmail()));
		file.setReadable(true);
		
		refresh();
		Iterator<Entry<String, MyFile>> it = filelist.entrySet().iterator();
		while (it.hasNext()) {
			it.next().getValue().updatePermissions(user);
		}
	}

	@Override
	public void setReadable(boolean flag, User newUser) throws RemoteException {
		file.setReadable(flag);
		if(flag)
			canRead.put(newUser.getEmail(), newUser);
		else
			canRead.remove(newUser.getEmail());
		
		refresh();
		Iterator<Entry<String, MyFile>> it = filelist.entrySet().iterator();
		while (it.hasNext()) {
			it.next().getValue().setReadable(flag, newUser);
		}
	}

	@Override
	public void setWritable(boolean flag, User newUser) throws RemoteException {
		file.setWritable(flag);
		if (flag)
			canWrite.put(newUser.getEmail(), newUser);
		else
			canWrite.remove(newUser.getEmail());
		Iterator<Entry<String, MyFile>> it = filelist.entrySet().iterator();
		while (it.hasNext()) {
			it.next().getValue().setWritable(flag, newUser);
		}
	}
}

package client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;



import files.MyDirectory;
import files.MyDirectoryImpl;
import files.MyFile;
import files.MyFileImpl;
import files.User;
import files.UserImpl;

import security.SymetricKey;
import server.Server;
import server.ServerImpl;

public class ClientImpl implements Client {
	private User user;
	private Server myserver;
	private int port;
	private SymetricKey serverkey;

	ClientImpl(User user, int port) throws Exception{
		serverkey = SymetricKey.createKey("Trabalho de SD 2010");
		this.myserver=new ServerImpl(serverkey);
		login(user);
		this.port=port;
		discoverPeers();
	}

	ClientImpl(int port) throws Exception{
		serverkey =SymetricKey.createKey("Trabalho de SD 2010");
		this.user=null;
		this.myserver=new ServerImpl(serverkey);
		this.port=port;
		discoverPeers();
	}

	private void discoverPeers() throws IOException, ClassNotFoundException {

		DatagramSocket rs=new DatagramSocket(port);
		rs.setSoTimeout(2000);
		MulticastSocket ms=new MulticastSocket(9090);
		ms.joinGroup(InetAddress.getByName("224.0.0.1"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos);
		oos.writeObject(port);
		oos.close();
		byte []arr = serverkey.encrypt(baos.toByteArray());
		System.out.println(arr.length);
		//System.out.println((Integer)(new ObjectInputStream(new ByteArrayInputStream(serverkey.decrypt(arr)))).readObject());
	//	byte[] arr = new byte[55555];
	//	DatagramPacket p=new DatagramPacket(arr,arr.length, InetAddress.getByName("224.0.0.1"),8080);
		
	//	byte []arr = baos.toByteArray();
		DatagramPacket p=new DatagramPacket(arr,arr.length, InetAddress.getByName("224.0.0.1"),8080);
		ms.send(p);
		ms.close();
		Attender a=new Attender(port, UnicastRemoteObject.toStub(myserver), serverkey);
		a.setDaemon(true);
		new Thread(a).start();
		try{
			while(true){
				DatagramPacket r=new DatagramPacket(new byte[624],624);
				rs.receive(r);
				ByteArrayInputStream bais = new ByteArrayInputStream( serverkey.decrypt(r.getData() ));
			//	ByteArrayInputStream bais = new ByteArrayInputStream( r.getData() );
				ObjectInputStream ois = new ObjectInputStream( bais);
				Server server= (Server) ois.readObject();
				myserver.addPeer(server);
				server.addPeer(myserver);
			}
		}catch(SocketTimeoutException e){}
		rs.close();

	}

	//Melhorar?!
	public void sync() throws IOException {
		///refreshRoot();
		List<Server> peers=myserver.getServers();
		List<MyFile> filesToUpdatePermissions = new LinkedList<MyFile>();
		Map<String,MyFile> files= user.getPath().getFilesToRequest(peers, myserver);
		Iterator<Entry<String,MyFile>> it=files.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,MyFile> e=it.next();
			if (e.getValue().isDirectory())
				filesToUpdatePermissions.add(getFile(e.getKey(),e.getValue()));
		}
		it = files.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,MyFile> e=it.next();
			if (!e.getValue().isDirectory())
				filesToUpdatePermissions.add(getFile(e.getKey(),e.getValue()));
		}
		
		Iterator<MyFile> it2 = filesToUpdatePermissions.iterator();
		while(it2.hasNext()) {
			MyFile f = it2.next();
			//long lastModified = f.getLastModified();
			
			f.updatePermissions(this.getUser());
			//f.setLastModified(lastModified);
		}
		this.getUser().getPath().refresh();

	}
	/*
	private void refreshRoot() throws RemoteException{
		this.root=new MyDirectoryImpl(new File(root.getAbsolutePath()));
	}
	 */
	@Override
	public MyFile getFile(String path,MyFile file) throws IOException {
		System.out.println("Getting "+file.getName()+" to "+path);
		MyFile myFile;
		File newfile=new File(path);
		newfile.setWritable(true);
		long pmodi=newfile.getParentFile().lastModified();
		long lpmodi=file.getLastModified();
		if(!file.isDirectory()){
			newfile.createNewFile();
			byte[] filedata = file.downloadFile();
			BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(path));
			output.write(filedata,0,filedata.length);
			output.flush();
			output.close();

			if(pmodi<lpmodi) 
				newfile.getParentFile().setLastModified(lpmodi); 
			else 
				newfile.getParentFile().setLastModified(pmodi);
			
			myFile = new MyFileImpl(newfile, file, user);
			
		}
		else {
			newfile.mkdirs();
			myFile = new MyDirectoryImpl(newfile, file, user);
			
		}
		this.getUser().getPath().addFile(myFile.getName(), myFile);
		newfile.setLastModified(file.getLastModified());
		return myFile;
	}


	public static void main(String args[]){
		try {
			Client c;
			if (args.length >= 4) {
				User user = new UserImpl(args[1], args[2]);
				user.setPath(args[3]);
				c = new ClientImpl(user,Integer.parseInt(args[0]));
			}
			else {
				c = new ClientImpl(Integer.parseInt(args[0]));
			}
			Scanner in=new Scanner(System.in);
			while (c.getUser() == null) {
				System.out.println("Comandos disponiveis:");
				System.out.println("0- Sair");
				System.out.println("1- Login");
				System.out.println("2- Registar");
				int com=in.nextInt();
				switch (com){
				case 0: {
					System.exit(0);
					break;
				}
				case 1: {
					System.out.println("Insira email, password e path");
					String email = in.next();
					String password = in.next();
					String path = in.next();
					User user = new UserImpl(email, password);
					user.setPath(path);
					if(!c.login(user));
					System.out.println("Credênciais Erradas");
					break;
				}
				case 2:{
					System.out.println("Insira email, password e path");
					String email = in.next();
					String password = in.next();
					String path = in.next();
					User user = new UserImpl(email, password);
					user.setPath(path);
					System.out.println("Passei aki1");
					if(!c.register(user))
						System.out.println("Utilizador já existe: " + c.getUser().getEmail());
					else
						c.login(user);
				}
				default: {break;}
				}
			}
			if(true) {

				while(true){
					System.out.println("Comandos disponiveis:");
					System.out.println("0- Sair");
					System.out.println("1- Listar pasta local");
					System.out.println("2- Listas pastas remotas");
					System.out.println("3- Listar ficheiros desatualizados");
					System.out.println("4- Sync");
					System.out.println("5- Partilhar Repositorio");
					int com=in.nextInt();
					switch (com){
					case 1: {
						c.getUser().getPath().refresh();
						printDir(c.getUser().getPath().getFiles());
						break;
					}
					case 2:{
						List<Server> toRemove = new LinkedList<Server>();
						Iterator<Server> it=((ClientImpl)c).myserver.getServers().iterator();
						while(it.hasNext()){
							Server next = it.next();
							try {
								System.out.println(next.getAddress().getHostName());
								printDir(next.getFileList(c.getUser()));
								System.out.println();
							}
							catch (RemoteException e) {
								//e.printStackTrace();
								toRemove.add(next);
							}
						}

						it = toRemove.iterator();
						while(it.hasNext()) {
							((ClientImpl)c).myserver.removePeer(it.next());
						}
						break;

					}
					case 4: {
						c.sync();
					}
					case 3:{
						Map<String,MyFile> files=c.getUser().getPath().getFilesToRequest(((ClientImpl)c).myserver.getServers(), ((ClientImpl)c).myserver);
						Iterator<Entry<String,MyFile>> it=files.entrySet().iterator();
						while(it.hasNext()){
							Entry<String,MyFile> next=it.next();
							System.out.println(next.getValue().getName()+" from "+next.getValue().getAddress().getHostName()+" to "+next.getKey());
						}
						break;
					}
					case 5: {
						System.out.println("Inserir email com quem partilhar repositorio");
						String shareUser = in.next();
						if(!((ClientImpl)c).share(shareUser, true, true, c.getUser().getPath()))
							System.out.println("Utilizador inexistente");
						break;
					}
					case 0: {
						System.exit(0);
						break;
					}
					default: {break;}
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


	}

	public static void printDir(Map<String,MyFile> map) throws RemoteException{
		if (map != null) {
			Iterator<Entry<String,MyFile>> it= map.entrySet().iterator();

			while(it.hasNext()){
				Entry<String,MyFile> e=it.next();
				//String name=e.getKey();
				MyFile f=e.getValue();
				if(f.isDirectory()){System.out.println("Pasta "+f.getName()+":");printDir(((MyDirectory)f).getFiles());}
				else System.out.println(f.getName()+" "+ f.getLastModified());
			}
		}

	}

	public Server getServer() {
		return myserver;
	}

	public User getUser() {
		return user;
	}

	public boolean login(User user) throws RemoteException {
		if (!myserver.login(user)) {
			user = null;
			return false;
		}
		this.user=user;
		return true;
	}

	public boolean register(User user) throws RemoteException {
		if(myserver.register(user)) {
			this.user = user;
			return true;
		}
		this.user = null;
		return false;
	}

	public boolean share(String shareUser, boolean read, boolean write, MyFile shareFile) throws RemoteException {
		User newUser = myserver.getUser(shareUser, 15);
		if (newUser == null)
			return false;
		shareFile.setReadable(read, newUser);
		shareFile.setWritable(write, newUser);
		myserver.share(newUser, read, write, shareFile);
		return true;
	}


}

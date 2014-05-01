package client;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Remote;

import security.Key;

class Attender extends Thread {
	
	int port ;
	Remote server;
	Key key;
	
	Attender(int port, Remote server , Key k) {
		this.port = port ; this.server=server; this.key=k;
		super.setDaemon(true) ;
	}
	
	public void run() {
		try {
			MulticastSocket ms = new MulticastSocket(8080) ;
			ms.joinGroup(InetAddress.getByName("224.0.0.1"));
			DatagramSocket ds= new DatagramSocket();
			System.err.println("waiting for reqs");
			for(;;) {
				DatagramPacket r = new DatagramPacket( new byte[96], 96 ) ;
				ms.receive( r ) ;
				System.err.println("resquest from "+r.getAddress().toString());
				ByteArrayInputStream bais = new ByteArrayInputStream(key.decrypt(r.getData()));
				
	//			ByteArrayInputStream bais = new ByteArrayInputStream(r.getData());
				
				ObjectInputStream ois = new ObjectInputStream( bais);
				int rport=(Integer)ois.readObject();
				ois.close();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream( baos);
				oos.writeObject(server);
				oos.close();
				byte []arr = key.encrypt(baos.toByteArray());
				System.out.println(arr.length);
				DatagramPacket s=new DatagramPacket( arr, arr.length,r.getAddress(),rport );
				ds.send(s);
				
			/*	DatagramPacket r = new DatagramPacket( new byte[65536], 65536 ) ;
				ms.receive( r ) ;
				System.err.println("resquest from "+r.getAddress().toString());
				ByteArrayInputStream bais = new ByteArrayInputStream(r.getData());
				ObjectInputStream ois = new ObjectInputStream( bais);
				int rport=(Integer)ois.readObject();
				ois.close();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream( baos);
				oos.writeObject(server);
				oos.close();
				byte []arr = baos.toByteArray();
				DatagramPacket s=new DatagramPacket( arr, arr.length,r.getAddress(),rport );
				ds.send(s);*/

			}
		} catch( Exception x ) {
			x.printStackTrace();}
	}
	
}

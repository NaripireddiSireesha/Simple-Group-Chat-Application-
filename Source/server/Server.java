//This class is implemented as a singleton class. This implements the server application for the chat application

import java.io.*;
import java.net.*;
import java.util.*;

class Server
{
	private ServerSocket myServerSocket;
	private InetAddress localInetAddress;
	private int localPort = 2000;
	public static HashMap<String, User> whoIsOnline = new HashMap<String, User>();
	
	private Server()
	{
		try{
			myServerSocket = new ServerSocket(localPort);
		}catch(IOException ioe){
			System.out.println("Error while opening ServerSocket");
			System.exit(1);
		}

		try{
			localInetAddress = InetAddress.getLocalHost();
		}catch(UnknownHostException ioe){
			System.out.println("Cannot determine the local InetAddress");
		}

	}

	private static final Server INSTANCE = new Server();

	public static Server getServer() 
	{
		return INSTANCE;
	}
	
	public void acceptIncomingConnection()
	{

		try{

			Socket mySocket = myServerSocket.accept();
			User myUser = new User(mySocket);
			whoIsOnline.put(myUser.name, myUser);
			new Thread(myUser).start();
			System.out.println( myUser.name + "@" + myUser.remoteInetAddress.getHostAddress() + " connected");

		} catch(IOException ioe){

			System.out.println("IOException Occured");
			System.exit(1);
		}

	}


	public static void main(String args[])
	{

		Server myServer = Server.getServer();
		String message  = null;

		while(true){
			System.out.println("Listening on "+ myServer.localInetAddress.toString() + " port " + myServer.localPort );
			myServer.acceptIncomingConnection();
		}
			
	}

}

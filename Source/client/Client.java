import java.io.*;
import java.net.*;

class Client{

	private Socket mySocket;
	private InetAddress ipAddress;
	private int port = 2000;
	private BufferedReader myBufferedReader;
	private PrintWriter myPrintWriter;
	private BufferedReader stdIn;
	

	public Client(String server)
	{
		try{
			ipAddress = InetAddress.getByName(server);
		}catch(UnknownHostException ioe) {
			System.out.println("cannot resolve the host " + server );
			System.exit(1);
		}

		try{
			mySocket = new Socket(ipAddress, port);
			System.out.println("connected to server at " + mySocket.getInetAddress() + " port: " + mySocket.getPort());
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to open the socket. " + "No server at "+ server.toLowerCase().concat(".cs.uchicago.edu"));
			System.exit(1);
		}

		try{
			myBufferedReader = new BufferedReader( new InputStreamReader( mySocket.getInputStream() ) );
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to get Input Stream");
			System.exit(1);
		}

		try{
			myPrintWriter = new PrintWriter( mySocket.getOutputStream( ), true );
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to open Print Writer");
			System.exit(1);
		}

		stdIn = new BufferedReader( new InputStreamReader( System.in ) );

	}

	public void closeConnection()
	{
		try{
			myBufferedReader.close();
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to close myBufferedReader");
			System.exit(1);
		}

		myPrintWriter.close();

		try{
			stdIn.close();
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to close stdIn");
			System.exit(1);
		}

		try{
			mySocket.close();
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to close mySocket");
			System.exit(1);
		}

	}

	public static void main(String args[])
	{
		if(args.length != 2)
		{
			System.out.println("Usage:\njava Client <server url>  <username>")	;
			System.exit(-1);
		}

		Client myClient = new Client(args[0]);
		myClient.myPrintWriter.println(args[1]);

		ClientReader myClientReader = new ClientReader(myClient.myBufferedReader);

		new Thread(myClientReader).start();

		String outGoingMessage = new String();
		while(true){
			try{
				outGoingMessage = myClient.stdIn.readLine();
			}catch(Exception e){
				System.out.println("Exception occurred while reading from stdIn");
			}

			myClient.myPrintWriter.println(outGoingMessage);

			if(outGoingMessage.compareToIgnoreCase("server exit") == 0) break;
		}

		try{
			myClientReader.join();
		}catch(InterruptedException ie)
		{
			System.out.println("Interrupted exception occurred while waiting for the thread to join");
		}

		myClient.closeConnection();
		System.out.println("Successfully disconnected from Server");
	}
}

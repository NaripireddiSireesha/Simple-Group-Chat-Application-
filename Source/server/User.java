import java.io.*;
import java.net.*;
import java.util.*;

public class User implements Runnable
{
	private Socket mySocket;
	public InetAddress remoteInetAddress;
	private int remotePort;
	private int threadId;
	public PrintWriter myPrintWriter;
	private BufferedReader myBufferedReader;
	public String name; 
	
	public User(Socket mySocket)
	{
		this.mySocket = mySocket;
		this.remoteInetAddress = this.mySocket.getInetAddress();
		this.remotePort = this.mySocket.getPort();

		try{
			this.myPrintWriter = new PrintWriter(mySocket.getOutputStream());
		}catch(Exception e){
			System.out.println("Exception occurred while creating myPrintWriter");
			System.exit(1);
		}

		try{
			this.myBufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream( ) ) );
		}catch(Exception e){
			System.out.println("Exception occurred while creating myBufferedReader");
			System.exit(1);
		}

		try{
			name = myBufferedReader.readLine();
		}catch(Exception e){
			System.out.println("Exception occured while trying to read from myBufferedReader");
		}
	}


	private String readMyBufferedReader()
	{
		String message = new String();
		try{
			message = myBufferedReader.readLine();
		}catch(Exception e){
			System.out.println("Exception occured while trying to read from myBufferedReader");
		}

		return message;
	}


	private void sendMessage(String destinationUserName, String message)
	{
		User destinationUser = Server.whoIsOnline.get(destinationUserName);
		Object lock = new Object();

		synchronized(lock){
			destinationUser.myPrintWriter.println(message); 
			destinationUser.myPrintWriter.flush();
		}
	}

	private void sendMessageBack(String message)
	{
		String formattedMessage = "server ".concat(message);
		Object lock = new Object();

		synchronized(lock){
			myPrintWriter.println(formattedMessage);
			myPrintWriter.flush();
		}
	}

	private void closeConnection()
	{
		try{
			myBufferedReader.close();
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to close myBufferedReader");
		}

		myPrintWriter.close();

		try{
			mySocket.close();
		}catch(IOException ioe) {
			System.out.println("IOException occured while trying to close mySocket");
		}

	}


	public void run()
	{
		while(true){

			String incomingMessage = new String();
			String outgoingMessage = new String();

			incomingMessage = readMyBufferedReader();

			if(incomingMessage == null) continue;
			
			//Process Message
			String tokens[] = incomingMessage.split(":| ", 2);

			if(tokens.length < 2)
			{
				outgoingMessage = "invalid string : ".concat(tokens[0]);  
				sendMessageBack(outgoingMessage) ;
				continue;
			}
			else if(tokens[0].equalsIgnoreCase("server")) //If the message is a control message
			{
				if(tokens[1].equalsIgnoreCase("exit")) 
				{
					Server.whoIsOnline.remove(name);      
					outgoingMessage = "bye"  ;
					sendMessageBack(outgoingMessage) ;
					closeConnection();
					break;
				}
				else if(tokens[1].equalsIgnoreCase("whoisonline"))
				{       
					Set<String> userSet = Server.whoIsOnline.keySet();
					Iterator<String> myIterator = userSet.iterator();

					while(myIterator.hasNext()) outgoingMessage = outgoingMessage.concat(" ").concat(myIterator.next());
					
					sendMessageBack(outgoingMessage) ;
				}
				else
				{
					outgoingMessage = "invalid string : ".concat(tokens[1]);  
					sendMessageBack(outgoingMessage) ;
				}
			} 
			else //If the message is traffic message
			{ 
				String destinationUserName = tokens[0];
				String sourceUser = name;
				outgoingMessage = sourceUser.concat(" ").concat(tokens[1]);

				if(Server.whoIsOnline.containsKey(destinationUserName))
				{
					sendMessage(destinationUserName, outgoingMessage);
				}
				else
				{
					sendMessageBack("User " + destinationUserName + " is not online. " + "Message " + tokens[1] + " not delivered.");
				}

			}

		} //End of While

		System.out.println( name + "@" + remoteInetAddress.getHostAddress() + " disconnected");

		return;
	}

}//Class Ends

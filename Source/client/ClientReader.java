//This class implements the thread that reads the incoming messages

import java.io.*;
import java.net.*;

public class ClientReader extends Thread{
	private BufferedReader myBufferedReader;

	public ClientReader(BufferedReader myBufferedReader){
		this.myBufferedReader = myBufferedReader;
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


	private void printMessage(String user, String message) 
	{
		System.out.println(user + " says: " + message )	;
	}


	public void run()
	{
		String message = new String();

		while(true){
			message = readMyBufferedReader();
			if(null == message)
			{
				System.out.println("Server Died"); 
				try{
					Thread.sleep(1000);
				}catch(InterruptedException ie){
					System.out.println("InterruptedException occured in sleep");
				}
				continue;
			}

			//Process Message
			String tokens[] = message.split(":| ", 2);

			if((tokens[0].compareTo("server") == 0) && (tokens[1].compareTo("bye") == 0)) break;

			printMessage(tokens[0], tokens[1]);

		} //End of While
	}
	
}//Class Ends

package nl.sogyo.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    private static final String CONTENT_LENGTH_HEADER_KEY = "content-length";

    public ConnectionHandler(Socket toHandle) {
        this.socket = toHandle;
    }

    /// Handle the incoming connection. This method is called by the JVM when passing an
    /// instance of the connection handler class to a Thread.
    public void run() {
        try {

            // Set up a reader that can conveniently read our incoming bytes as lines of text.
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Request request = parseRequest(reader);
            
            System.out.println("Parsed Request: " + request);
            
            // Set up a writer that can write text to our binary output stream.
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            // Write a simple hello world textual response to the client.
            writer.write("Thank you for connecting!\r\n");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // After handling the request, we can close our socket.
            try
			{
				socket.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    private Request parseRequest(BufferedReader reader) throws IOException
    {
    	String line = null;
    	String[] header = null;
    	int contentLength = -1;
    	ParsingPhase currentPhase = ParsingPhase.MethodCall;
    	
    	Request.Builder requestBuilder = new Request.Builder();
    	do
    	{
    		line = reader.readLine();
    		System.out.println(line);
    		
    		switch(currentPhase)
    		{
    		case MethodCall:
    			String[] methodCallParts = line.split(" ");
    			requestBuilder.setHttpMethod(methodCallParts[0]);
    			requestBuilder.setUrl(methodCallParts[1]);
    			currentPhase = ParsingPhase.Headers;
    			break;
    		case Headers:
    			header = getHeaderKV(line);
    			if(header != null)
    			{
    				requestBuilder.addHeader(header[0], header[1]);
    				if(header[0].equalsIgnoreCase(CONTENT_LENGTH_HEADER_KEY))
    					contentLength = Integer.parseInt(header[1]);
    			}
    			break;
    		}
    	} while (!line.isEmpty());
    	
    	if(contentLength > 0)
    	{
    		String content = "";
	    	for(int i = 0; i < contentLength; i++)
	    	{
	    		int readChar = reader.read();
	    		if(readChar == -1)
	    			break;
	    		else
	    			content += (char)readChar;
	    	}
	    	
	    	requestBuilder.setBody(content);
    	}
    	
    	return requestBuilder.build();
    }
    
    private String[] getHeaderKV(String line)
    {
    	String[] kv = line.split(":");
    	if(kv.length == 2)
    	{
    		kv[0] = kv[0].strip();
    		kv[1] = kv[1].strip();
    		return kv;
    	}
    	else
    		return null;
    }
    
    private static enum ParsingPhase
    {
    	MethodCall,
    	Headers,
    	Body,
    	
    	Unknown,
    }

    public static void main(String... args) {
        try {
            // A server socket opens a port on the local computer (in this program port 9090).
            // The computer now listens to connections that are made using the TCP/IP protocol.
            ServerSocket socket = new ServerSocket(9090);
            System.out.println("Application started. Listening at localhost:9090");

            // Start an infinite loop. This pattern is common for applications that run indefinitely
            // and react on system events (e.g. connection established). Inside the loop, we handle
            // the connection with our application logic. 
            while(true) {
                // Wait for someone to connect. This call is blocking; i.e. our program is halted
                // until someone connects to localhost:9090. A socker is a connection (a virtual
                // telephone line) between two endpoints - the client (browser) and the server (this).
                Socket newConnection = socket.accept();
                // We want to process our incoming call. Furthermore, we want to support multiple
                // connections. Therefore, we handle the processing on a background thread. Java
                // threads take a class that implements the Runnable interface as a constructor
                // parameter. Upon starting the thread, the run() method is called by the JVM.
                // As our handling is in a background thread, we can accept new connections on the
                // main thread (in the next iteration of the loop).
                // Starting the thread is so-called fire and forget. The main thread starts a second
                // thread and forgets about its existence. We recieve no feedback on whether the
                // connection was handled gracefully.
                Thread t = new Thread(new ConnectionHandler(newConnection));
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

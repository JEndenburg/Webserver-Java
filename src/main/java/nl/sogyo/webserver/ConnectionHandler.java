package nl.sogyo.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import nl.sogyo.webserver.input.Request;
import nl.sogyo.webserver.input.RequestReader;
import nl.sogyo.webserver.output.Response;
import nl.sogyo.webserver.output.ResponseSender;

public class ConnectionHandler implements Runnable 
{
	public static ServerProperties serverProperties = ServerProperties.loadProperties(ServerProperties.class.getClassLoader().getResource("server.properties").getFile().replace("%20", " "));
    private Socket socket;

    public ConnectionHandler(Socket toHandle) {
        this.socket = toHandle;
    }

    /// Handle the incoming connection. This method is called by the JVM when passing an
    /// instance of the connection handler class to a Thread.
    public void run() {
        try {
        	
        	Request request = RequestReader.parseRequestFromStream(socket.getInputStream());
            System.out.println("Parsed Request: " + request);
            Response response = new Response(HttpStatusCode.OK, ContentType.HTML, "<HTML><body><p>Hello World You requested " + request.getResourcePath() + "!</p></body></HTML>");
            ResponseSender.sendResponse(response, socket.getOutputStream());

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

    public static void main(String... args) {
        try {
            // A server socket opens a port on the local computer (in this program port 9090).
            // The computer now listens to connections that are made using the TCP/IP protocol.
            ServerSocket socket = new ServerSocket(serverProperties.port);
            System.out.println("Application started. Listening at localhost:" + serverProperties.port);

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

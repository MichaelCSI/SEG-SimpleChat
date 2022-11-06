// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
/**
 * Michael O'Sullivan, 300228801
 */

import ocsf.server.*;

import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    try{
      String[] messages = ((String)msg).split(" ");
      // Command messages
      // Login: only allow this command at client connection
      if(messages[0].startsWith("#")){
        switch(messages[0]){
          case "#login":
            client.setInfo("id", messages[1]);
            sendToAllClients(client.getInfo("id") + " has logged on.");
            break;
          case "#quit":
          case "#logoff":
            sendToAllClients(client.getInfo("id") + " has logged off.");
            clientDisconnected(client);
            break;
        }
      }
      // Regular message
      else{
        this.sendToAllClients(client.getInfo("id") + "> " + (String)msg);
      }
      System.out.println("Message received: " + msg + " from " + client.getInfo("id") + " (from " + client + ")");

    } catch(Exception e){
      System.out.println("Error handling client input");
    }
  }

  /**
   * This method handles any messages received from the client.
   *
   *
   */
  public void handleMessageFromServerConsole(String message){
    String[] messages = message.split(" ");
    if(messages[0].startsWith("#")) {
      switch (messages[0]) {
        case "#quit":
          try {
            close();
          } catch (IOException e) {}
          System.exit(0);
          break;
        case "#close":
          try {
            close();
          } catch (IOException e) {}
          break;
        case "#stop":
          stopListening();
          break;
        case "#setport":
          try{
            if(isListening()){
              System.out.println("Cannot set port while server is open");
              return;
            }
            int p = Integer.parseInt(messages[1]);
            setPort(p);
          } catch(Exception e){ System.out.println("Error setting port from server");}
          break;
        case "#getport":
          System.out.println(getPort());
          break;
        case "#start":
          if(!isListening()) {
            try {
              listen();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
          else System.out.println("Server must be stopped to start listening.");
          break;
        default:
          System.out.println(message + " is not a valid command");
          break;
      }
    }
    else sendToAllClients("SERVER MSG> " + message);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  /**
   * Implements hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("Client connected: " + client.getInfo("id") + " (from " + client + ")");
  }

  /**
   * Implements hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println("Client disconnected: " + client.getInfo("id") + " (from " + client + ")");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class

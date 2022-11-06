// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
/**
 * Michael O'Sullivan, 300228801
 */

package client;

import ocsf.client.*;
import common.*;
import java.io.*;


/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */

public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  String id;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String id, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.id = id;
    openConnection();
  }

  //Setters/Getters *************************************************
  public String getID(){
    return this.id;
  }
  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      // User has specified a command
      if(message.startsWith("#")){
        String[] arguments = message.split(" ");
        switch(arguments[0]){
          case "#quit":
            sendToServer("#quit");
            quit();
          break;
          case "#logoff":
            sendToServer("#logoff");
            closeConnection();
          break;
          // set host/port have similar constraints, and thus follow a similar case
          case "#sethost":
          case "#setport":
            if(isConnected()) {
              System.out.println("You must be disconnected to edit host or port");
              return;
            }
            try{
              if(arguments[0].compareTo("#sethost") == 0) {
                setHost(arguments[1]);
                System.out.println("Host name set to '" + arguments[1] +"'");
              }
              else {
                setPort(Integer.parseInt(arguments[1]));
                System.out.println("Port number set to '" + arguments[1] +"'");
              }
            }
            catch(Exception e){
              System.out.println(message + " error: ");
              e.printStackTrace();
            }
            break;
          case "#login":
            if(isConnected()) {
              System.out.println("Error: Already logged in");
              closeConnection();
            }
            else {
              openConnection();
              System.out.println("Successful login");
            }
            break;
          case "#gethost": System.out.println(getHost());
          break;
          case "#getport": System.out.println(getPort());
          break;
          default: System.out.println(message + " is not a valid command");
          break;
        }
      }
      // Input is a message, not a command
      else sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  /**
   * Implements hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
    clientUI.display("Connection closed");
  }

  /**
   * Implements hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   *
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("Server has shut down");
    quit();
  }

  /**
   * Implements hook method called after a connection has been established. The default
   * implementation does nothing. It may be overridden by subclasses to do
   * anything they wish.
   */
  @Override
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + id);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
//End of ChatClient class

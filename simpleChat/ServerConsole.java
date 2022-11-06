/**
 * Michael O'Sullivan, 300228801
 */

import client.ChatClient;
import common.ChatIF;

import java.io.IOException;
import java.util.Scanner;


public class ServerConsole implements ChatIF {

    Scanner fromConsole;
    EchoServer server;

    /**
     * Constructs an instance of the ServerConsole UI.
     */
    public ServerConsole(int port) {
        server = new EchoServer(port);
        try
        {
            server.listen(); //Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }

        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);
    }


    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the server's server message handler.
     */
    public void accept()
    {
        try
        {
            String message;

            while (true)
            {
                message = fromConsole.nextLine();
                server.handleMessageFromServerConsole(message);
            }
        }
        catch (Exception ex)
        {
            System.out.println
                    ("Unexpected error while reading from server console!");
        }
    }
    @Override
    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message)
    {
        System.out.println("SERVER MSG> " + message);
    }


    /**
     * This method is responsible for the creation of the Client UI.
     *
     * @param args[0] The server port
     */
    public static void main(String[] args)
    {
        int port = 0;

        try
        {
            port = Integer.parseInt(args[0]);
        }
        catch(Exception e)
        {
            port = 5555;
        }
        ServerConsole serverConsole = new ServerConsole(port);
        serverConsole.accept();  //Wait for console data
    }
}


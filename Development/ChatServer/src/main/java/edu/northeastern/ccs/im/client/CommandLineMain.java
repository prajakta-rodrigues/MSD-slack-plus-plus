package edu.northeastern.ccs.im.client;

import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Class which can be used as a command-line IM client.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class CommandLineMain { 
 
  /**
   * This main method will perform all of the necessary actions for this phase of
   * the course project.
   *
   * @param args Command-line arguments which we ignore
   */
  public static void main(String[] args) {
    CommandLineMain commandLineMain = new CommandLineMain();
    IMConnection connect = commandLineMain.getUserNameAndConnect(args, new InputStreamReader(System.in));
    // Create the objects needed to read & write IM messages.
    commandLineMain.startMessaging(connect, connect.getKeyboardScanner() , connect.getMessageScanner());
    System.exit(0);
  }

private static final String BOUNCER = "Bouncer"; 
 
  public void startMessaging(IMConnection connect, KeyboardScanner scan,
      MessageScanner mess) {
    // Repeat the following loop
    while (connect.connectionActive()) {
      // Check if the user has typed in a line of text to broadcast to the IM server.
      // If there is a line of text to be
      // broadcast:
      if (scan.hasNext()) {
        // Read in the text they typed
        String line = scan.nextLine();

        // If the line equals "/quit", close the connection to the IM server.
        if (line.equals("/quit")) {
          connect.disconnect();
          break;
        } else {
          // Else, send the text so that it is broadcast to all users logged in to the IM
          // server.
          connect.sendMessage(line);
        }
      }
      // Get any recent messages received from the IM server.
      if (mess.hasNext()) { 
        Message message = mess.next();
        handleMessage(message, connect);
      }
    }
  } 

	public void handleMessage(Message message, IMConnection connect) {
	  if (message.getText().equals("Wrong password for given username. Try again.") 
	            && message.getSender().equals(BOUNCER)) {
	          System.out.println(message.getText());
	          System.out.println("Enter :authenticate your_password.");
	        }
	        else if (message.getText().equals("Enter Password for user") 
	            && message.getSender().equals(BOUNCER )) {
	          System.out.println("Enter :authenticate your_password.");
	        }
	        else if(message.getText().equals("User is not registered with system. Enter Password for user")
	            && message.getSender().equals(BOUNCER)) {
	          System.out.println("Enter :register your_password.");
	        }
	        else if (!message.getSender().equals(connect.getUserName())) {
	          System.out.println(message.getSender() + ": " + message.getText());
	        }

	
}

public IMConnection getConnection(String[] args, Readable input) {
	@SuppressWarnings("all")
    Scanner in = new Scanner(input); 
    // Prompt the user to type in a username.
    System.out.println("What username would you like?");
    String username = in.nextLine();
    return new IMConnection(args[0], Integer.parseInt(args[1]), username);
    
  }
  
  
  public IMConnection getUserNameAndConnect(String[] args, Readable input) {
    IMConnection connect;
    do {
      // Create a Connection to the IM server.
      connect = getConnection(args, input);
    } while (!checkIfConnected(connect));
    return connect;
    
  }  

  protected boolean checkIfConnected(IMConnection connect) {
    return connect.connect();
  }
}

package chatterTests;

import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import java.io.StringReader;
import java.util.NoSuchElementException;
import edu.northeastern.ccs.im.client.CommandLineMain;
import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.client.KeyboardScanner;
import edu.northeastern.ccs.im.client.Message;
import edu.northeastern.ccs.im.client.MessageScanner;



public class CommandLineMainTest {
  
  private CommandLineMain commandLineMain;
  
  @Test
  public void testGetConnection() {
    commandLineMain = new CommandLineMain();
    String[] args = new String[2];
    args[0] = "localhost";
    args[1] = "4545";    
    StringReader reader = new StringReader("testUser1");
    IMConnection imConnection = commandLineMain.getConnection(args, reader);
    assertEquals("testUser1" , imConnection.getUserName());
    
  }
  
  @Test
  public void testGetUserNameAndConnect() {

    String[] args = new String[2];
    args[0] = "localhost";
    args[1] = "4545";    
    StringReader reader = new StringReader("testUser1");
    commandLineMain = new CommandLineMain() {
      @Override
      protected boolean checkIfConnected(IMConnection connect) {
        return true;        
      }
    };
    commandLineMain.getUserNameAndConnect(args, reader);
    
  }
  
  @Test(expected = NoSuchElementException.class)
  public void testGetUserNameAndNotConnected() {

    String[] args = new String[2];
    args[0] = "localhost";
    args[1] = "4545";    
    StringReader reader = new StringReader("testUser1");
    commandLineMain = new CommandLineMain();
    commandLineMain.getUserNameAndConnect(args, reader);
  }
  
  @Test
  public void testStartMessagingNotActiveConnection() {
    commandLineMain = new CommandLineMain();
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = Mockito.mock(KeyboardScanner.class);
    MessageScanner mess = Mockito.mock(MessageScanner.class);
    Mockito.when(connect.connectionActive()).thenReturn(false);
    
    try {
    commandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  } 
  
  @Test
  public void testStartMessagingActiveConnectionMessageToScan() {
    commandLineMain = new CommandLineMain();
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = Mockito.mock(KeyboardScanner.class);
    MessageScanner mess = Mockito.mock(MessageScanner.class);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(connect.connectionActive()).thenReturn(true).thenReturn(false);
    Mockito.when(scan.hasNext()).thenReturn(true);
    Mockito.when(scan.nextLine()).thenReturn("testing");
    doNothing().when(connect).sendMessage(Mockito.anyString());
    Mockito.when(mess.hasNext()).thenReturn(true);
    Mockito.when(mess.next()).thenReturn(msg);
    Mockito.when(msg.getSender()).thenReturn("testUser");
    Mockito.when(connect.getUserName()).thenReturn("testUser");
    Mockito.when(msg.getText()).thenReturn("Testing message");
    try {
    commandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  } 
  
  @Test
  public void testStartMessagingActiveConnectionNoMessageToScan() {
    commandLineMain = new CommandLineMain();
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = Mockito.mock(KeyboardScanner.class);
    MessageScanner mess = Mockito.mock(MessageScanner.class);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(connect.connectionActive()).thenReturn(true).thenReturn(false);
    Mockito.when(scan.hasNext()).thenReturn(false);
    Mockito.when(mess.hasNext()).thenReturn(true);
    Mockito.when(mess.next()).thenReturn(msg);
    Mockito.when(msg.getSender()).thenReturn("testUser");
    Mockito.when(connect.getUserName()).thenReturn("testUser");
    Mockito.when(msg.getText()).thenReturn("Testing message");
    try {
    commandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  }
  
  @Test
  public void testStartMessagingActiveConnectionMessageRecieved() {
    commandLineMain = new CommandLineMain();
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = Mockito.mock(KeyboardScanner.class);
    MessageScanner mess = Mockito.mock(MessageScanner.class);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(connect.connectionActive()).thenReturn(true).thenReturn(false);
    Mockito.when(scan.hasNext()).thenReturn(false);
    Mockito.when(mess.hasNext()).thenReturn(true);
    Mockito.when(mess.next()).thenReturn(msg);
    Mockito.when(msg.getSender()).thenReturn("testUser");
    Mockito.when(connect.getUserName()).thenReturn("testUser2");
    Mockito.when(msg.getText()).thenReturn("Testing message");
    try {
    commandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  }
  
  @Test
  public void testStartMessagingActiveConnectionNoMessageRecieved() {
    commandLineMain = new CommandLineMain();
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = Mockito.mock(KeyboardScanner.class);
    MessageScanner mess = Mockito.mock(MessageScanner.class);
    Mockito.when(connect.connectionActive()).thenReturn(true).thenReturn(false);
    Mockito.when(scan.hasNext()).thenReturn(false);
    Mockito.when(mess.hasNext()).thenReturn(false);
    try {
    commandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  }
  
  @Test
  public void testStartMessagingActiveConnectionQuitMessage() {
    commandLineMain = new CommandLineMain();
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = Mockito.mock(KeyboardScanner.class);
    MessageScanner mess = Mockito.mock(MessageScanner.class);
    Mockito.when(connect.connectionActive()).thenReturn(true).thenReturn(false);
    Mockito.when(scan.hasNext()).thenReturn(true);
    Mockito.when(scan.nextLine()).thenReturn("/quit");
    Mockito.when(mess.hasNext()).thenReturn(false);
  
    try {
    commandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  }
  
  
  

}

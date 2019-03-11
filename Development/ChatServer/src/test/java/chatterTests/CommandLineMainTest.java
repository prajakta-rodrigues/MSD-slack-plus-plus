package chatterTests;

import java.io.StringReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import edu.northeastern.ccs.im.client.CommandLineMain;
import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.client.KeyboardScanner;
import edu.northeastern.ccs.im.client.Message;
import edu.northeastern.ccs.im.client.MessageScanner;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class) 
@PrepareForTest({CommandLineMain.class, KeyboardScanner.class, MessageScanner.class})
public class CommandLineMainTest {
  
  @Test
  public void testGetUserNameAndConnectSuccess() {
    String[] args = new String[2];
    args[0] = "localhost";
    args[1] = "4545";    
    StringReader reader = new StringReader("praj");
    IMConnection value = Mockito.mock(IMConnection.class);
    PowerMockito.mockStatic(CommandLineMain.class);
    PowerMockito.when(CommandLineMain.getConnection("praj" , "localhost", "4545"))
    .thenReturn(value);      
    Mockito.when(value.connect()).thenReturn(true);
    Mockito.when(value.connectionActive()).thenReturn(true);
    try {
    CommandLineMain.getUserNameAndConnect(args, reader);
    }
    catch(Exception e) {
      assert false;
    }
  }
  
  @Test
  public void testStartMessagingNotActiveConnection() {
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = PowerMockito.mock(KeyboardScanner.class);
    MessageScanner mess = Mockito.mock(MessageScanner.class);
    Mockito.when(connect.connectionActive()).thenReturn(false);
    
    try {
    CommandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  } 
  
  @Test
  public void testStartMessagingActiveConnectionMessageToScan() {
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = PowerMockito.mock(KeyboardScanner.class);
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
    CommandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  } 
  
  @Test
  public void testStartMessagingActiveConnectionNoMessageToScan() {
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = PowerMockito.mock(KeyboardScanner.class);
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
    CommandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  }
  
  @Test
  public void testStartMessagingActiveConnectionMessageRecieved() {
    IMConnection connect = Mockito.mock(IMConnection.class);
    KeyboardScanner scan = PowerMockito.mock(KeyboardScanner.class);
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
    CommandLineMain.startMessaging(connect, scan, mess);
    }
    catch(Exception e) {
      assert false;
    }
  }
  
  
  

}

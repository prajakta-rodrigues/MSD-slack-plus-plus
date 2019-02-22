package prattleTests;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.server.ClientRunnable;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * The Class ClientRunnableTest.
 */
public class ClientRunnableTest {
  
  private ClientRunnable client;
  
  private Iterator<Message> mockIterator;

  /**
   * Setup for tests.
   */
  @Before
  public void initData() {
    NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
    client = new ClientRunnable(mockNetwork);
    List<Message> messageQueue = new ArrayList<>();
    Message msg1 = Message.makeSimpleLoginMessage("Prajakta");
    Message msg2 = Message.makeSimpleLoginMessage(null);
    messageQueue.add(msg1);
    messageQueue.add(msg2);
    mockIterator = messageQueue.iterator();
    when(mockNetwork.iterator()).thenReturn(mockIterator);
  }

  /**
   * Test check for initialization.
   */
  @Test
  public void testCheckForInitialization() {
    client.run();
    assertTrue(client.isInitialized());
  }

  /**
   * Test check for initialization null.
   */
  @Test
  public void testCheckForInitializationNull() {
    mockIterator.next();
    client.run();
    assertFalse(client.isInitialized());
  }

  /**
   * Test name.
   */
  @Test
  public void testName() {
    client.setName("Franklin");
    assertEquals("Franklin", client.getName());
  }

}

package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Test;
import org.mockito.Mockito;
import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.MessageType;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.server.Prattle;

/**
 * The Class NetworkConnectionTest.
 */
public class NetworkConnectionTest {

  private NetworkConnection networkConnection;

  private String prattle = "edu.northeastern.ccs.im.server.Prattle";

  private String ct = "createClientThread";

  /**
   * Test network connection socket channel.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testNetworkConnectionSocketChannel() throws IOException {
    try (SocketChannel socketChannel = SocketChannel.open()) {
      socketChannel.configureBlocking(false);
      socketChannel.connect(new InetSocketAddress("localhost", 4514));
      networkConnection = new NetworkConnection(socketChannel);
    }
    assert true;

  }

  /**
   * Test network connection exception.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test(expected = AssertionError.class)
  public void testNetworkConnectionException() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.close();
    networkConnection = new NetworkConnection(socketChannel);

  }

  /**
   * Test network connection null exception.
   */
  @Test(expected = NullPointerException.class)
  public void testNetworkConnectionNullException() {
    networkConnection = new NetworkConnection(null);

  }

  /**
   * Test send message.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test(expected = NotYetConnectedException.class)
  public void testSendMessageException() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Message msg = Message.makeBroadcastMessage("john", "test this");
    networkConnection.sendMessage(msg);

  }

  /**
   * Test send empty message.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testSendEmptyMessage() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(msg.toString()).thenReturn("");
    assertTrue(networkConnection.sendMessage(msg));

  }

  @Test
  public void testSendMessageWriteFail() throws IOException, NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(msg.toString()).thenReturn("Heyya");
    Field fieldChannel = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("channel");
    fieldChannel.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(fieldChannel, fieldChannel.getModifiers() & ~Modifier.FINAL);
    SocketChannel mockChannel = Mockito.mock(SocketChannel.class);
    Mockito.when(mockChannel.write(Mockito.any(ByteBuffer.class))).thenReturn(1);
    fieldChannel.set(networkConnection, mockChannel);
    assertFalse(networkConnection.sendMessage(msg));

  }

  @Test
  public void testSendMessageWriteException() throws IOException, NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(msg.toString()).thenReturn("Heyya");
    Field fieldChannel = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("channel");
    fieldChannel.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(fieldChannel, fieldChannel.getModifiers() & ~Modifier.FINAL);
    SocketChannel mockChannel = Mockito.mock(SocketChannel.class);
    Mockito.when(mockChannel.write(Mockito.any(ByteBuffer.class))).thenThrow(new IOException());
    fieldChannel.set(networkConnection, mockChannel);
    assertFalse(networkConnection.sendMessage(msg));

  }


  /**
   * Creates the client thread assertion error exception.
   *
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void createClientThreadAssertionErrorException() throws IllegalAccessException,
      InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException {
    ServerSocketChannel serverSocket = Mockito.mock(ServerSocketChannel.class);
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
    Method createClientThreadMethod = Class.forName(prattle).getDeclaredMethod(ct,
        ServerSocketChannel.class, ScheduledExecutorService.class);
    createClientThreadMethod.setAccessible(true);
    Mockito.when(serverSocket.accept()).thenThrow(new AssertionError());
    createClientThreadMethod.invoke(null, serverSocket, threadPool);
  }

  /**
   * Creates the client thread IO exception.
   *
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void createClientThreadIOException() throws IllegalAccessException,
      InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException {

    ServerSocketChannel serverSocket = Mockito.mock(ServerSocketChannel.class);
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
    Method createClientThreadMethod = Class.forName(prattle).getDeclaredMethod(ct,
        ServerSocketChannel.class, ScheduledExecutorService.class);
    createClientThreadMethod.setAccessible(true);
    Mockito.when(serverSocket.accept()).thenThrow(new IOException());
    createClientThreadMethod.invoke(null, serverSocket, threadPool);
  }

  /**
   * Creates the client thread null socket.
   *
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void createClientThreadNullSocket() throws IllegalAccessException,
      InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException {

    ServerSocketChannel serverSocket = Mockito.mock(ServerSocketChannel.class);
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
    Method createClientThreadMethod = Class.forName(prattle).getDeclaredMethod(ct,
        ServerSocketChannel.class, ScheduledExecutorService.class);
    createClientThreadMethod.setAccessible(true);
    Mockito.when(serverSocket.accept()).thenReturn(null);
    createClientThreadMethod.invoke(null, serverSocket, threadPool);
    assert true;
  }

  /**
   * Test remove client.
   */
  @Test
  public void testRemoveClient() {
    NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
    Prattle.removeClient(new ClientRunnable(networkConnection));
  }

  /**
   * Test client runnable name null.
   */
  @Test
  public void testClientRunnableNameNull() {
    NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
    ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
    @SuppressWarnings("unchecked")
    Iterator<Message> value = Mockito.mock(Iterator.class);
    Mockito.when(networkConnection.iterator()).thenReturn(value);
    Mockito.when(value.hasNext()).thenReturn(true);
    Message message = Message.makeSimpleLoginMessage(null);
    Mockito.when(value.next()).thenReturn(message);
    clientRunnable.run();
  }

  /**
   * Test no message network connection exception.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test(expected = NoSuchElementException.class)
  public void testNoMessageNetworkConnectionException() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Iterator<Message> iterator = networkConnection.iterator();
    iterator.next();

  }

  /**
   * Test close network connection.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testCloseNetworkConnection() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    networkConnection.close();
    networkConnection.close();
  }

  /**
   * Test message is broadcast.
   */
  @Test
  public void testMessageIsBroadcast() {
    Message message = Message.makeBroadcastMessage("myName", "hey");
    assertTrue(message.isBroadcastMessage());
  }

  /**
   * Test message is quit
   */
  @Test
  public void testMessageIsQuit() {
    Message message = Message.makeMessage(MessageType.QUIT.toString(), "ih", -1,"tet");
    assertTrue(message.terminate());
  }

  /**
   * Test message is hello
   */
  @Test
  public void testMessageIsHello() {
    Message message = Message.makeMessage(MessageType.HELLO.toString(), "nam", -1, "tet");
    assertTrue(message.isInitialization());
  }

  /**
   * Test message is login.
   */
  @Test
  public void testMessageIsLogin() {
    Message message = Message.makeSimpleLoginMessage("myName");
    assertTrue(message.isInitialization());
  }

  @Test
  public void testAuthMessage() {
    Message message = Message.makeAuthenticateMessage("test", "text");
    assertEquals("text", message.getText());
    assertTrue(message.isAuthenticate());
  }

  @Test
  public void testRegisterMessage() {
    Message message = Message.makeRegisterMessage("test", "text");
    assertEquals("text", message.getText());
    assertTrue(message.isRegister());
  }

  @Test
  public void makeAuthMessage() {
    Message message = Message.makeMessage(MessageType.AUTHENTICATE.toString(), "b", -1, "txt");
    assertTrue(message.isAuthenticate());
  }

  @Test
  public void makeRegisterMessage() {
    Message message = Message.makeMessage(MessageType.REGISTER.toString(), "b", -1, "txt");
    assertTrue(message.isRegister());
  }

  @Test
  public void makeErrorMessage() {
    Message message = Message.makeMessage("t", "b", -1, "txt");
    assertNull(message);
  }

  @Test
  public void hasNextMessageNotEmpty() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IOException, IllegalArgumentException, IllegalAccessException {
    SocketChannel socketChannel = SocketChannel.open();
    NetworkConnection networkConnection = new NetworkConnection(socketChannel);
    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message message = Message.makeMessage(MessageType.REGISTER.toString(), "b", -1, "txt");
    messages.add(message);
    Field messageQueue = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("messages");
    messageQueue.setAccessible(true);
    messageQueue.set(networkConnection, messages);
    Iterator<Message> iterator = networkConnection.iterator();
    assertTrue(iterator.hasNext());
  }

  @Test
  public void getNextMessages() throws IOException, NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    SocketChannel socketChannel = SocketChannel.open();
    NetworkConnection networkConnection = new NetworkConnection(socketChannel);
    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message message = Message.makeMessage(MessageType.REGISTER.toString(), "b", -1,"txt");
    messages.add(message);
    Field messageQueue = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("messages");
    messageQueue.setAccessible(true);
    messageQueue.set(networkConnection, messages);
    Iterator<Message> iterator = networkConnection.iterator();
    assertEquals(message, iterator.next());
  }

  @Test(expected = AssertionError.class)
  public void hasNextGetMessageFromClientKeyReadableFalse()
      throws IOException, NoSuchFieldException, SecurityException, ClassNotFoundException,
      IllegalArgumentException, IllegalAccessException {
    SocketChannel socketChannel = SocketChannel.open();
    NetworkConnection networkConnection = new NetworkConnection(socketChannel);
    Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.selectNow()).thenReturn(1);
    Field selectorField = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("selector");
    selectorField.setAccessible(true);
    selectorField.set(networkConnection, selector);
    Iterator<Message> iterator = networkConnection.iterator();
    iterator.hasNext();
  }

  @Test(expected = AssertionError.class)
  public void closeException() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IOException, IllegalArgumentException, IllegalAccessException {
    SocketChannel socketChannel = SocketChannel.open();
    NetworkConnection networkConnection = new NetworkConnection(socketChannel);
    Selector selector = Mockito.mock(Selector.class);
    Mockito.doThrow(new IOException()).when(selector).close();
    Field selectorField = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("selector");
    selectorField.setAccessible(true);
    selectorField.set(networkConnection, selector);
    networkConnection.close();
  }

  @Test
  public void testHasNext() throws IOException, NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    SocketChannel socketChannel = SocketChannel.open();
    NetworkConnection networkConnection = new NetworkConnection(socketChannel);
    Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.selectNow()).thenReturn(1);
    Field selectorField = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("selector");
    selectorField.setAccessible(true);
    selectorField.set(networkConnection, selector);

    SelectionKey key = Mockito.mock(SelectionKey.class);
    Field selectorKey =
        Class.forName("edu.northeastern.ccs.im.server.NetworkConnection").getDeclaredField("key");
    selectorKey.setAccessible(true);
    selectorKey.set(networkConnection, key);
    Mockito.when(key.readyOps()).thenReturn(1);


    Field fieldBuffer =
        Class.forName("edu.northeastern.ccs.im.server.NetworkConnection").getDeclaredField("buff");
    fieldBuffer.setAccessible(true);
    ByteBuffer buffer = ByteBuffer.allocate(5);
    buffer.putChar(0, 'c');
    fieldBuffer.set(networkConnection, buffer);
    Field fieldChannel = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("channel");
    fieldChannel.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(fieldChannel, fieldChannel.getModifiers() & ~Modifier.FINAL);
    SocketChannel mockChannel = Mockito.mock(SocketChannel.class);
    Mockito.when(mockChannel.read(Mockito.any(ByteBuffer.class))).thenReturn(1);
    fieldChannel.set(networkConnection, mockChannel);
    Set<SelectionKey> set = Mockito.mock(Set.class);
    Mockito.when(set.remove(Mockito.anyObject())).thenReturn(true);
    Mockito.when(selector.selectedKeys()).thenReturn(set);
    Iterator<Message> iterator = networkConnection.iterator();
    iterator.hasNext();
  }

  @Test(expected = AssertionError.class)
  public void hasNextException() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {
    SocketChannel socketChannel = SocketChannel.open();
    NetworkConnection networkConnection = new NetworkConnection(socketChannel);
    Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.selectNow()).thenThrow(new IOException());
    Field selectorField = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("selector");
    selectorField.setAccessible(true);
    selectorField.set(networkConnection, selector);
    Iterator<Message> iterator = networkConnection.iterator();
    iterator.hasNext();
  }

  @Test
  public void hasNextSuccess() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {
    SocketChannel socketChannel = SocketChannel.open();
    NetworkConnection networkConnection = new NetworkConnection(socketChannel);
    Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.selectNow()).thenReturn(1);
    Field selectorField = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("selector");
    selectorField.setAccessible(true);
    selectorField.set(networkConnection, selector);
    
    Field keyField =
        Class.forName("edu.northeastern.ccs.im.server.NetworkConnection").getDeclaredField("key");
    keyField.setAccessible(true);
    SelectionKey key = Mockito.mock(SelectionKey.class);
    keyField.set(networkConnection, key);
    Mockito.when(key.readyOps()).thenReturn(1);
    
    Field fieldChannel =
        Class.forName("edu.northeastern.ccs.im.server.NetworkConnection").getDeclaredField("channel");
    fieldChannel.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(fieldChannel, fieldChannel.getModifiers() & ~Modifier.FINAL);
    SocketChannel mockChannel = Mockito.mock(SocketChannel.class);
    Mockito.when(mockChannel.read(Mockito.any(ByteBuffer.class))).thenReturn(1);
    fieldChannel.set(networkConnection, mockChannel);
    
    Field buffField = Class.forName("edu.northeastern.ccs.im.server.NetworkConnection")
        .getDeclaredField("buff");
    buffField.setAccessible(true);
    ByteBuffer buff = ByteBuffer.allocate(100);
    buff.put("HLO 6 test12 2 -1 2 --".getBytes());
    buffField.set(networkConnection, buff);
    Iterator<Message> iterator = networkConnection.iterator();
    iterator.hasNext();
  }
}

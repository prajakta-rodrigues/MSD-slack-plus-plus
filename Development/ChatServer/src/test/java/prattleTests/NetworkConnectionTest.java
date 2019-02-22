package prattleTests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Test;
import org.mockito.Mockito;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;

public class NetworkConnectionTest {

  private NetworkConnection networkConnection;
  private String prattle = "edu.northeastern.ccs.im.server.Prattle";
  private String ct = "createClientThread";

  @Test
  public void testNetworkConnectionSocketChannel() throws IOException {
    try (SocketChannel socketChannel = SocketChannel.open()) {
      socketChannel.configureBlocking(false);
      socketChannel.connect(new InetSocketAddress("localhost", 4514));
      networkConnection = new NetworkConnection(socketChannel);
    }
    assert true;

  }

  @Test(expected = AssertionError.class)
  public void testNetworkConnectionException() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.close();
    networkConnection = new NetworkConnection(socketChannel);

  }
  
  @Test(expected = NullPointerException.class)
  public void testNetworkConnectionNullException() {
    networkConnection = new NetworkConnection(null);

  }

  @Test(expected = NotYetConnectedException.class)
  public void testSendMessage() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Message msg = Message.makeBroadcastMessage("john", "test this");
    networkConnection.sendMessage(msg);

  }
  
  @Test
  public void testSendEmptyMessage() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(msg.toString()).thenReturn("");
    assertTrue(networkConnection.sendMessage(msg));

  }

  @Test
  public void createClientThreadAssertionErrorException()
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException {
    ServerSocketChannel serverSocket = Mockito.mock(ServerSocketChannel.class);
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
    Method createClientThreadMethod = Class.forName(prattle)
        .getDeclaredMethod(ct, ServerSocketChannel.class,
            ScheduledExecutorService.class);
    createClientThreadMethod.setAccessible(true);
    Mockito.when(serverSocket.accept()).thenThrow(new AssertionError());
    createClientThreadMethod.invoke(null, serverSocket, threadPool);
  }

  @Test
  public void createClientThreadIOException()
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException {

    ServerSocketChannel serverSocket = Mockito.mock(ServerSocketChannel.class);
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
    Method createClientThreadMethod = Class.forName(prattle)
        .getDeclaredMethod(ct, ServerSocketChannel.class,
            ScheduledExecutorService.class);
    createClientThreadMethod.setAccessible(true);
    Mockito.when(serverSocket.accept()).thenThrow(new IOException());
    createClientThreadMethod.invoke(null, serverSocket, threadPool);
  }

  @Test
  public void createClientThreadNullSocket()
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException {

    ServerSocketChannel serverSocket = Mockito.mock(ServerSocketChannel.class);
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
    Method createClientThreadMethod = Class.forName(prattle)
        .getDeclaredMethod(ct, ServerSocketChannel.class,
            ScheduledExecutorService.class);
    createClientThreadMethod.setAccessible(true);
    Mockito.when(serverSocket.accept()).thenReturn(null);
    createClientThreadMethod.invoke(null, serverSocket, threadPool);
    assert true;
  }


  @Test(expected = NoSuchElementException.class)
  public void testNoMessageNetworkConnectionException() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    Iterator<Message> iterator = networkConnection.iterator();
    iterator.next();

  }


  @Test
  public void testCloseNetworkConnection() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    networkConnection = new NetworkConnection(socketChannel);
    networkConnection.close();
    networkConnection.close();
  }


  @Test
  public void testMessageIsBroadcast() {
    Message message = Message.makeBroadcastMessage("myName", "hey");
    assertTrue(message.isBroadcastMessage());
  }

  @Test
  public void testMessageIsLogin() {
    Message message = Message.makeSimpleLoginMessage("myName");
    assertTrue(message.isInitialization());
  }


}
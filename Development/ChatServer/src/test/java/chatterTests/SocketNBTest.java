package chatterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import edu.northeastern.ccs.im.client.Message;
import edu.northeastern.ccs.im.client.SocketNB;

public class SocketNBTest {

  private String clientSocketNB = "edu.northeastern.ccs.im.client.SocketNB";

  private String localhost = "localhost";

  /**
   * Test socket NB.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testSocketNB() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    SocketNB socketNB = new SocketNB("", 0);
    Method connectedMethod = Class.forName(clientSocketNB).getDeclaredMethod("isConnected");
    connectedMethod.setAccessible(true);
    assertEquals(false, connectedMethod.invoke(socketNB));
  }

  /**
   * Test socket NB read argument empty buffer.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test
  public void testSocketNBReadArgumentEmptyBuffer()
      throws NoSuchMethodException, ClassNotFoundException {
    SocketNB socketNB = new SocketNB(localhost, 4545);
    Method readMethod =
        Class.forName(clientSocketNB).getDeclaredMethod("readArgument", CharBuffer.class);
    readMethod.setAccessible(true);
    CharBuffer charBuf = CharBuffer.allocate(1024);
    charBuf.put("1hey test this");
    charBuf.position(12);
    try {
      readMethod.invoke(socketNB, charBuf);
      assert false;
    } catch (Exception e) {
      assert true;
    }
  }

  /**
   * Test socket NB read argument.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testSocketNBReadArgument() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    SocketNB socketNB = new SocketNB(localhost, 4545);
    Method readMethod =
        Class.forName(clientSocketNB).getDeclaredMethod("readArgument", CharBuffer.class);
    readMethod.setAccessible(true);
    CharBuffer charBuf = CharBuffer.allocate(1024);
    charBuf.put("1hey test this00");
    charBuf.position(14);
    assertNull(readMethod.invoke(socketNB, charBuf));
  }

  /**
   * Test socket N bprint.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test
  public void testSocketNBprint() throws NoSuchMethodException, ClassNotFoundException {
    SocketNB socketNB = new SocketNB(localhost, 4545);
    Method readMethod = Class.forName(clientSocketNB).getDeclaredMethod("print",
        edu.northeastern.ccs.im.client.Message.class);
    readMethod.setAccessible(true);
    try {
      readMethod.invoke(socketNB,
          edu.northeastern.ccs.im.client.Message.makeAcknowledgeMessage("rita"));
      assert false;
    } catch (Exception e) {
      assert true;
    }
  }

  @Test(expected = InvocationTargetException.class)
  public void testEnqueueMessageKeyNotReadable() throws NoSuchMethodException, SecurityException,
      ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchFieldException, IOException {
    SocketNB socketNB = new SocketNB("localhost", 2421);
    Method enqueueMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB")
        .getDeclaredMethod("enqueueMessages", List.class);
    enqueueMethod.setAccessible(true);
    List<Message> listMessages = new ArrayList<Message>();
    Field selectorField =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("selector");
    selectorField.setAccessible(true);
    Selector selector = Mockito.mock(Selector.class);
    selectorField.set(socketNB, selector);

    Field keyField =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("key");
    keyField.setAccessible(true);
    SelectionKey key = Mockito.mock(SelectionKey.class);
    keyField.set(socketNB, key);
    Mockito.when(key.readyOps()).thenReturn(0);
    Mockito.when(selector.select(Mockito.anyLong())).thenReturn(2);
    enqueueMethod.invoke(socketNB, listMessages);
  }

  @Test
  public void testEnqueueMessageKeyReadable() throws NoSuchMethodException, SecurityException,
      ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchFieldException, IOException {
    SocketNB socketNB = new SocketNB("localhost", 2421);
    Method enqueueMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB")
        .getDeclaredMethod("enqueueMessages", List.class);
    enqueueMethod.setAccessible(true);
    List<Message> listMessages = new ArrayList<Message>();
    Field selectorField =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("selector");
    selectorField.setAccessible(true);
    Selector selector = Mockito.mock(Selector.class);
    selectorField.set(socketNB, selector);

    Field keyField =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("key");
    keyField.setAccessible(true);
    SelectionKey key = Mockito.mock(SelectionKey.class);
    keyField.set(socketNB, key);
    Mockito.when(key.readyOps()).thenReturn(1);

    Field fieldChannel =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("channel");
    fieldChannel.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(fieldChannel, fieldChannel.getModifiers() & ~Modifier.FINAL);
    SocketChannel mockChannel = Mockito.mock(SocketChannel.class);
    Mockito.when(mockChannel.read(Mockito.any(ByteBuffer.class))).thenReturn(1);
    fieldChannel.set(socketNB, mockChannel);
    Mockito.when(selector.select(Mockito.anyLong())).thenReturn(2);
    Set<SelectionKey> set = Mockito.mock(Set.class);
    Mockito.when(selector.selectedKeys()).thenReturn(set);
    Mockito.when(set.remove(Mockito.anyObject())).thenReturn(true);
    enqueueMethod.invoke(socketNB, listMessages);
  }

  @Test
  public void testClose() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchFieldException, IOException {
    SocketNB socketNB = new SocketNB("localhost", 2421);
    Method closeMethod =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredMethod("close");
    closeMethod.setAccessible(true);
    Field selectorField =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("selector");
    selectorField.setAccessible(true);
    Selector selector = Mockito.mock(Selector.class);
    Mockito.doNothing().when(selector).close();
    selectorField.set(socketNB, selector);
    Field fieldChannel =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("channel");
    fieldChannel.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(fieldChannel, fieldChannel.getModifiers() & ~Modifier.FINAL);
    SocketChannel mockChannel = SocketChannel.open();
    fieldChannel.set(socketNB, mockChannel);
    closeMethod.invoke(socketNB);
  }

  @Test
  public void testPrint() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchFieldException, IOException {
    SocketNB socketNB = new SocketNB("localhost", 2421) {
      @Override
      protected boolean isConnected() {
        return true;

      }
    };
    Method closeMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB")
        .getDeclaredMethod("print", Message.class);
    closeMethod.setAccessible(true);
    Message msg = Mockito.mock(Message.class);
    Mockito.when(msg.toString()).thenReturn("");
    Field fieldChannel =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("channel");
    fieldChannel.setAccessible(true);
    Field modifiersField = Field.class.getDeclaredField("modifiers");
    modifiersField.setAccessible(true);
    modifiersField.setInt(fieldChannel, fieldChannel.getModifiers() & ~Modifier.FINAL);
    SocketChannel mockChannel = Mockito.mock(SocketChannel.class);
    fieldChannel.set(socketNB, mockChannel);
    closeMethod.invoke(socketNB, msg);
  }

  @Test
  public void enqueueMessageDelayZero() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
      NoSuchMethodException, InvocationTargetException, IOException {
    SocketNB socketNB = new SocketNB("localhost", 2421);
    Field selectorField =
        Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredField("selector");
    selectorField.setAccessible(true);
    Selector selector = Mockito.mock(Selector.class);
    selectorField.set(socketNB, selector);
    Method enqueueMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB")
        .getDeclaredMethod("enqueueMessages", List.class);
    enqueueMethod.setAccessible(true);
    List<Message> listMessages = new ArrayList<Message>();
    Mockito.when(selector.select(Mockito.anyLong())).thenReturn(0);
    enqueueMethod.invoke(socketNB, listMessages);
  }


}

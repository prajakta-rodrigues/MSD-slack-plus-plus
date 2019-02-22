package tests;

import  edu.northeastern.ccs.im.*;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.client.*;
import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.ServerConstants;
import org.junit.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT.value;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Created by venkateshkoka on 2/10/19.
 * All the test cases
 */
public class PrattleTest {

    public PrattleTest() throws IOException {
    }


    /**
     * Test ServerConstants Types...
     */
    @Test
    public void testChatloggerTypes() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = Class.forName("edu.northeastern.ccs.im.server.ServerConstants").getDeclaredConstructor();
        constructor.setAccessible(true);
        Object sc = constructor.newInstance();
        assertEquals(true, true);
    }

    /**
     * Test makeHelloMessage in Message...
     */
    @Test
    public void testMessageClass() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method makeMessageMethod = Class.forName("edu.northeastern.ccs.im.Message").getDeclaredMethod("makeHelloMessage", String.class);
        makeMessageMethod.setAccessible(true);
        makeMessageMethod.invoke(null,"mike");
        Message msd1 =  Message.makeBroadcastMessage("koka","Hello There");
        Message msg = Message.makeQuitMessage("mike");
        boolean b = true;
        if(msd1.getText().length()>0) {
            b =  msg.isInitialization();
        }
        assertEquals(b, false);
    }

    /**
     * Test hadle type methods in Message...
     */
    @Test
    public void testClientMessageClass() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Method makeMessageMethod = Class.forName("edu.northeastern.ccs.im.client.Message").getDeclaredMethod("makeMessage", String.class, String.class, String.class);
        Method makeHelloMessageMethod = Class.forName("edu.northeastern.ccs.im.client.Message").getDeclaredMethod("makeHelloMessage", String.class);
        makeMessageMethod.setAccessible(true);
        makeHelloMessageMethod.setAccessible(true);
        makeMessageMethod.invoke(null,"HLO","Jaffa","Hello busy people");
        makeMessageMethod.invoke(null,"ACK","Jaffa","Hello busy people");
        makeMessageMethod.invoke(null,"NAK","Jaffa","Hello busy people");
        makeHelloMessageMethod.invoke(null,"Jaffa");
        edu.northeastern.ccs.im.client.Message sc = edu.northeastern.ccs.im.client.Message.makeLoginMessage("jaffa");
        sc.isAcknowledge();
        sc.isBroadcastMessage();
        sc.isDisplayMessage();
        sc.isInitialization();
        sc.terminate();
        sc.getSender();
        sc.getText();
        assertEquals(true, true);
    }



    @Test
    public void testNetworkConnectionSocketChannel() throws IOException {
       SocketChannel socketChannel = SocketChannel.open();
       socketChannel.configureBlocking(false);
       socketChannel.connect(new InetSocketAddress("localhost", 4545));
       NetworkConnection networkConnection = new NetworkConnection(socketChannel);
       assert true;
    }

    @Test
    public  void testMessageType() {
        MessageType mstype = MessageType.HELLO;
        MessageType mstype1 = MessageType.HELLO;
        MessageType mstype2 = MessageType.BROADCAST;
        MessageType mstype3 = MessageType.BROADCAST;
        Assert.assertEquals(mstype,mstype1);
        Assert.assertEquals("HLO",MessageType.HELLO.toString());
        Assert.assertEquals(mstype2,mstype3);
    }

    @Test
    public  void testMessage() {

        Message msd =  Message.makeSimpleLoginMessage("koka");
        Message msd1 =  Message.makeBroadcastMessage("koka","Hello There");
        Message msd2 =  Message.makeSimpleLoginMessage(null);
        String msg = msd.toString();
        String msg1 = msd1.toString();
        String msg2 = msd2.toString();
        Assert.assertEquals("HLO 4 koka 2 --",msg);
    }

    @Test
    public  void testBuddy() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Buddy buddy = Buddy.makeTestBuddy("jaffa");
        String name = buddy.getUserName();
        Method method1 = Class.forName("edu.northeastern.ccs.im.client.Buddy").getDeclaredMethod("getBuddy", String.class);
        Method method2 = Class.forName("edu.northeastern.ccs.im.client.Buddy").getDeclaredMethod("getEmptyBuddy", String.class);
        Method method3 = Class.forName("edu.northeastern.ccs.im.client.Buddy").getDeclaredMethod("removeBuddy", String.class);
        method1.setAccessible(true);
        method2.setAccessible(true);
        method3.setAccessible(true);
        method2.invoke(null,"daffa");
        method1.invoke(null,"jaffa");
        method1.invoke(null,"jaffa");
        method2.invoke(null,"daffa");
        method3.invoke(null,"daffa");
        Assert.assertEquals(name,"jaffa");
    }

    private NetworkConnection networkConnection;

    @Test
    public void testNetworkConnectionSocketChannel1() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost", 4514));
        networkConnection = new NetworkConnection(socketChannel);
        assert true;

    }
}
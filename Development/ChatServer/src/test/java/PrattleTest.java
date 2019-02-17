
import  edu.northeastern.ccs.im.*;
import edu.northeastern.ccs.im.client.Buddy;
import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


/**
 * Created by venkateshkoka on 2/10/19.
 * All the test cases
 */
public class PrattleTest {

    public PrattleTest() throws IOException {
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
        method1.invoke(null,"jaffa");
        method2.invoke(null,"daffa");
        method3.invoke(null,"daffa");
        Assert.assertEquals(name,"jaffa");
    }

//    @BeforeClass
//    public static void testPrattleSocket() throws IOException {
//        String[] port = {};
//        Prattle.main(port);
//        Prattle.broadcastMessage(Message.makeSimpleLoginMessage("JohnWick"));
//        Prattle.stopServer();
//    }

//    @Test
//    public void testClientRunnable() throws IOException {
//        ServerSocketChannel serverSocket = ServerSocketChannel.open();
//        SocketChannel socketChannel = SocketChannel.open();
//        NetworkConnection nc = new NetworkConnection(socketChannel);
//        nc.sendMessage(Message.makeSimpleLoginMessage("JohnWick"));
//        ClientRunnable cr = new ClientRunnable(nc);
//    }



}
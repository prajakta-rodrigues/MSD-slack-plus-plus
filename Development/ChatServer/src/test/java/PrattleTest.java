import  edu.northeastern.ccs.im.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


/**
 * Created by venkateshkoka on 2/10/19.
 * All the test cases
 */
public class PrattleTest {

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
        Assert.assertEquals(msg, "HLO 4 koka 2 --");
    }
}
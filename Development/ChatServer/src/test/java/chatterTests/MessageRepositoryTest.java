package chatterTests;

import edu.northeastern.ccs.im.client.CommandLineMain;
import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.MessageType;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * test the message respository
 */
public class MessageRepositoryTest {
    @Test
    public void testAddMessage() {
        MessageRepository messageRepository = new MessageRepository(DatabaseConnection.getDataSource());
        Message msg = Message.makeBroadcastMessage( "atti1", 1, "hello",1);
        boolean result = messageRepository.saveMessage(msg);
        Assert.assertTrue("true",result);

    }
}

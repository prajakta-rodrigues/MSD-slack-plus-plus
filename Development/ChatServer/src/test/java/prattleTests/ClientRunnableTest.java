package prattleTests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.UserRepository;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
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

	@Mock
	private DataSource ds;
	
	@Mock
	private UserRepository userRepository;
	
	/**
	 * Setup for tests.
	 */
	@Before
	public void initData() {
	    MockitoAnnotations.initMocks(this);
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

	@Test
	public void testSetClientRunnable() {
		ClientRunnable cr = Mockito.mock(ClientRunnable.class);
		cr.setActiveChannelId(-1);
		cr.setActiveChannelId(10);
	}

	@Test
	public void testAuthenticationMessage() throws SQLException, NoSuchFieldException, 
	SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		SocketChannel socketChannel = Mockito.mock(SocketChannel.class);
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork);
		List<Message> messageQueue = new ArrayList<>();
		Message msg1 = Message.makeSimpleLoginMessage("pra");
		messageQueue.add(msg1);
		Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
	    		.getDeclaredField("initialized");
	    initialized.setAccessible(true);
	    initialized.set(client, true);
		messageQueue.add(Message.makeAuthenticateMessage("pra", "test"));
		mockIterator = messageQueue.iterator();
		when(mockNetwork.iterator()).thenReturn(mockIterator);
		when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(new User(1, "pra", "pwd"));
		client.setName("pra");
		client.run();
		
	}

}

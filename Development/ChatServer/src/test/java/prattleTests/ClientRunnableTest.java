package prattleTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.ClientRunnable;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * The Class ClientRunnableTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientRunnableTest {

	private ClientRunnable client;

	private Iterator<Message> mockIterator;

	@Mock
	private DataSource ds;
	
		
	@Mock
    private Connection c;
	
    @Mock
    private PreparedStatement stmt;
	
    @Mock
    private ResultSet rs;
    
	/**
	 * Setup for tests.
	 * @throws SQLException 
	 */
	@Before
	public void initData() throws SQLException {
        assertNotNull(ds);
        when(c.prepareStatement(Mockito.any(String.class))).thenReturn(stmt);
        when(ds.getConnection()).thenReturn(c);
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
	SecurityException, ClassNotFoundException, IllegalArgumentException, 
	IllegalAccessException, NoSuchMethodException, InvocationTargetException {
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
		
		client.setName("pra");
		client.run();
		Field userRepo = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("userRepository");
		userRepo.setAccessible(true);
		UserRepository u = (UserRepository)userRepo.get(client);
		
		Field dataSrc = Class.forName("edu.northeastern.ccs.im.server.repositories.UserRepository")
				.getDeclaredField("dataSource");
		dataSrc.setAccessible(true);
		dataSrc.set(u, ds);
		ResultSetMetaData ResultSetMetaData = Mockito.mock(ResultSetMetaData.class);
		 when(rs.getMetaData()).thenReturn(ResultSetMetaData);
        when(stmt.executeQuery()).thenReturn(rs);
       
        
		Method authenticate = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
	    		.getDeclaredMethod("authenticateUser", Message.class);
		authenticate.setAccessible(true);
		authenticate.invoke(client, Message.makeAuthenticateMessage("pra", "pwd"));
		
	}
	
	@Test
	public void testAuthenticationFail() throws NoSuchFieldException, SecurityException, 
	ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork) {
			@Override
			protected boolean sendMessage(Message message) {
				return true;
			}
		};
		Field userRepo = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("userRepository");
		userRepo.setAccessible(true);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		userRepo.set(client, userRepository);
		when(userRepository.getUserByUserName("primt")).thenReturn(new User(1 , "primt" , 
				BCrypt.hashpw("test", BCrypt.gensalt(8))));
		List<Message> messageQueue = new ArrayList<>();
		Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
	    		.getDeclaredField("initialized");
	    initialized.setAccessible(true);
	    initialized.set(client, true);
		

		messageQueue.add(Message.makeAuthenticateMessage("primt", BCrypt.hashpw("test", BCrypt.gensalt(8))));
		mockIterator = messageQueue.iterator();
		when(mockNetwork.iterator()).thenReturn(mockIterator);
		client.setName("primt");
		client.run();
		
		messageQueue.add(Message.makeBroadcastMessage("pra", "testd"));
		
	}

	
	@Test
	public void testAuthenticationSuccess() throws NoSuchFieldException, SecurityException, 
	ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork) {
			@Override
			protected boolean sendMessage(Message message) {
				return true;
			}
		}; 
		String hash =  BCrypt.hashpw("test", BCrypt.gensalt(8));
		Field userRepo = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("userRepository");
		userRepo.setAccessible(true);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		userRepo.set(client, userRepository);
		when(userRepository.getUserByUserName("pri")).thenReturn(new User(1 , "pri" , 
				hash));
		List<Message> messageQueue = new ArrayList<>();
		Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
	    		.getDeclaredField("initialized");
	    initialized.setAccessible(true);
	    initialized.set(client, true);
		

		messageQueue.add(Message.makeAuthenticateMessage("pri", "test"));
		mockIterator = messageQueue.iterator();
		when(mockNetwork.iterator()).thenReturn(mockIterator);
		client.setName("pri");
		client.run();
		
		messageQueue.add(Message.makeBroadcastMessage("pra", "testd"));
		
	}

	
}

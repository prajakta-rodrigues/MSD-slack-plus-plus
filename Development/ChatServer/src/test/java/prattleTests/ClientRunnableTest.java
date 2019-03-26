package prattleTests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.server.Notification;
import edu.northeastern.ccs.im.server.NotificationType;
import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.ChatLogger;
import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.ClientTimer;

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

	/** The client. */
	private ClientRunnable client;

	/** The mock iterator. */
	private Iterator<Message> mockIterator;

	/** The ds. */
	@Mock
	private DataSource ds;
	
		
	/** The c. */
	@Mock
    private Connection c;
	
    /** The stmt. */
    @Mock
    private PreparedStatement stmt;
	
    /** The rs. */
    @Mock
    private ResultSet rs;
    
	/**
	 * Setup for tests.
	 *
	 * @throws SQLException the SQL exception
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

	/**
	 * Test set client runnable.
	 */
	@Test
	public void testSetClientRunnable() {
		ClientRunnable cr = Mockito.mock(ClientRunnable.class);
		cr.setActiveChannelId(-1);
		cr.setActiveChannelId(10);
	}
 
	  /**
	   * Test client runnable broadcast message name null.
	   */
	  @Test
	  public void testClientRunnableBroadcastMessageNameNull() {
	    NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
	    ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
	    @SuppressWarnings("unchecked")
	    Iterator<Message> value = Mockito.mock(Iterator.class);
	    Mockito.when(networkConnection.iterator()).thenReturn(value);
	    Mockito.when(value.hasNext()).thenReturn(true);
	    Message message = Message.makeBroadcastMessage(null, "test51");
	    Mockito.when(value.next()).thenReturn(message);
	    clientRunnable.run();
	  }

	  /**
	   * Test client runnable broadcast message different name.
	   */
	  @Test
	  public void testClientRunnableBroadcastMessageDifferentName() {
	    NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
	    ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
	    clientRunnable.setName("test61");
	    @SuppressWarnings("unchecked")
	    Iterator<Message> value = Mockito.mock(Iterator.class);
	    Mockito.when(networkConnection.iterator()).thenReturn(value);
	    Mockito.when(value.hasNext()).thenReturn(true);
	    Message message = Message.makeBroadcastMessage(null, "test51");
	    Mockito.when(value.next()).thenReturn(message);
	    clientRunnable.run();
	  }

	  /**
  	 * Test client runnable broadcast message different name.
  	 *
  	 * @throws IllegalAccessException the illegal access exception
  	 * @throws ClassNotFoundException the class not found exception
  	 * @throws NoSuchFieldException the no such field exception
  	 */
	  @Test
	  public void testClientRunnableBroadcastMessageDifferentName3()
	      throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
	    NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
	    ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
	    clientRunnable.setName("tuffaha");
	    @SuppressWarnings("unchecked")
	    Iterator<Message> value = Mockito.mock(Iterator.class);
	    Mockito.when(networkConnection.iterator()).thenReturn(value);
	    Mockito.when(value.hasNext()).thenReturn(true);
	    Message message = Message.makeSimpleLoginMessage("tuffaha");
	    Mockito.when(value.next()).thenReturn(message);
	    Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField("initialized");
	    initialized.setAccessible(true);
	    initialized.set(clientRunnable, true);
	    Mockito.when(value.hasNext()).thenReturn(true);
	    Mockito.when(value.next()).thenReturn(message);
	    clientRunnable.run();
	  }

	  /**
  	 * Test message null checks.
  	 *
  	 * @throws NoSuchMethodException the no such method exception
  	 * @throws ClassNotFoundException the class not found exception
  	 * @throws IllegalAccessException the illegal access exception
  	 * @throws InvocationTargetException the invocation target exception
  	 * @throws SecurityException the security exception
  	 * @throws IllegalArgumentException the illegal argument exception
  	 */
	  @Test
	  public void testMessageNullChecks()
	      throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
	    ClientRunnable clientRunnable = new ClientRunnable(null);
	    clientRunnable.setName("usr3");
	    ChatLogger.setMode(ChatLogger.HandlerType.FILE);
		ChatLogger.setMode(ChatLogger.HandlerType.CONSOLE);
	    Method msgChecksMethod = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredMethod("messageChecks",
	        Message.class);
	    msgChecksMethod.setAccessible(true);
	    msgChecksMethod.invoke(clientRunnable, Message.makeBroadcastMessage(null, "hey"));
	  }

	  /**
  	 * Test message checks.
  	 *
  	 * @throws NoSuchMethodException the no such method exception
  	 * @throws ClassNotFoundException the class not found exception
  	 * @throws IllegalAccessException the illegal access exception
  	 * @throws InvocationTargetException the invocation target exception
  	 * @throws SecurityException the security exception
  	 * @throws IllegalArgumentException the illegal argument exception
  	 */
	  @Test
	  public void testMessageChecks()
	      throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
	    ClientRunnable clientRunnable = new ClientRunnable(null);
	    clientRunnable.setName("usr1");
	    Method msgChecksMethod = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredMethod("messageChecks",
	        edu.northeastern.ccs.im.server.Message.class);
	    msgChecksMethod.setAccessible(true);
	    msgChecksMethod.invoke(clientRunnable, Message.makeBroadcastMessage("usr", "hey"));
	  }
	
	/**
	 * Test authentication message.
	 *
	 * @throws SQLException the SQL exception
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws InvocationTargetException the invocation target exception
	 */
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
		
		Field dataSrc = Class.forName("edu.northeastern.ccs.im.server.repositories.Repository")
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
	
	/**
	 * Test authentication fail.
	 *
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@Test
	public void testAuthenticationFail() throws NoSuchFieldException, SecurityException, 
	ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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

	
	/**
	 * Test authentication success.
	 *
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws InvocationTargetException the invocation target exception
	 */
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
	
	/**
	 * Test check for initialization no message.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	@Test
	public void testCheckForInitializationNoMessage() throws NoSuchMethodException, SecurityException,
	ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork) {
			@Override
			protected boolean sendMessage(Message message) {
				return true;
			}
		}; 
		Iterator<Message> iterator = Mockito.mock(Iterator.class);
		Mockito.when(mockNetwork.iterator()).thenReturn(iterator);
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("checkForInitialization");
		init.setAccessible(true);
		init.invoke(client);
		
	}
	
	/**
	 * Test check for initialization user exists.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchFieldException the no such field exception
	 */
	@Test
	public void testCheckForInitializationUserExists() throws NoSuchMethodException, SecurityException,
	ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork) {
			@Override
			protected boolean sendMessage(Message message) {
				return true;
			}
		}; 
		Iterator<Message> iterator = Mockito.mock(Iterator.class);
		Mockito.when(mockNetwork.iterator()).thenReturn(iterator);
		Mockito.when(iterator.hasNext()).thenReturn(true);
		Message message = Message.makeSimpleLoginMessage("prili");
		Mockito.when(iterator.next()).thenReturn(message);
		Field userRepo = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("userRepository");
		userRepo.setAccessible(true);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		userRepo.set(client, userRepository);
		when(userRepository.getUserByUserName("prili")).thenReturn(new User(1 , "prili" , 
				"test"));
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("checkForInitialization");
		init.setAccessible(true);
		init.invoke(client);
	} 
	
	/**
	 * Test respond to broadcast message.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchFieldException the no such field exception
	 */
	@Test
	public void testRespondToBroadcastMessage() throws NoSuchMethodException, 
	SecurityException, ClassNotFoundException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork) {
			@Override
			protected boolean sendMessage(Message message) {
				return true;
			}
		};
		
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("respondToMessage", Message.class);
		init.setAccessible(true);
		Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
	    		.getDeclaredField("authenticated");
	    initialized.setAccessible(true);
	    initialized.set(client, true);
		Message msg = Message.makeBroadcastMessage("testUser", "hey");
		init.invoke(client, msg);
	}

	/**
	 * Test respond to command message.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchFieldException the no such field exception
	 */
	@Test
	public void testRespondToCommandMessage() throws NoSuchMethodException, 
	SecurityException, ClassNotFoundException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork) {
			@Override
			protected boolean sendMessage(Message message) {
				return true;
			}
		};
		
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("respondToMessage", Message.class);
		init.setAccessible(true);
		Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
	    		.getDeclaredField("authenticated");
	    initialized.setAccessible(true);
	    initialized.set(client, true);
		Message msg = Message.makeCommandMessage("testUser", -1,"hey");
		init.invoke(client, msg);
	}
	
	/**
	 * Test respond to authenticate message.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchFieldException the no such field exception
	 */
	@Test
	public void testRespondToAuthenticateMessage() throws NoSuchMethodException, 
	SecurityException, ClassNotFoundException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
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
		when(userRepository.getUserByUserName("testUser")).thenReturn(new User(1 , "testUser" , 
				BCrypt.hashpw("test", BCrypt.gensalt(8))));
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("respondToMessage", Message.class);
		init.setAccessible(true);
		Message msg = Message.makeAuthenticateMessage("testUser", "test");
		init.invoke(client, msg);
	}
	
	/**
	 * Test respond to register message registration failed.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchFieldException the no such field exception
	 */
	@Test
	public void testRespondToRegisterMessageRegistrationFailed() throws NoSuchMethodException, 
	SecurityException, ClassNotFoundException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
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
		when(userRepository.addUser(Mockito.any(User.class))).thenReturn(false);
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("respondToMessage", Message.class);
		init.setAccessible(true);
		Message msg = Message.makeRegisterMessage("testUser", "hey");
		init.invoke(client, msg);
	}
	
	/**
	 * Test respond to register message registration success.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchFieldException the no such field exception
	 */
	@Test
	public void testRespondToRegisterMessageRegistrationSuccess() throws NoSuchMethodException, 
	SecurityException, ClassNotFoundException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
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
		when(userRepository.addUser(Mockito.any(User.class))).thenReturn(true);
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("respondToMessage", Message.class);
		init.setAccessible(true);
		Message msg = Message.makeRegisterMessage("testUser", "hey");
		init.invoke(client, msg);
		assertEquals((client.getName().hashCode() & 0xfffffff) , client.getUserId());
	}
	
	/**
	 * Test send message.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	@Test
	public void testSendMessage() throws NoSuchMethodException, 
	SecurityException, ClassNotFoundException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork);
		when(mockNetwork.sendMessage(Mockito.any(Message.class))).thenReturn(true);
		Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("sendMessage", Message.class);
		init.setAccessible(true);
		Message msg = Message.makeRegisterMessage("testUser", "hey");
		init.invoke(client, msg);
		
	}
	
	/**
	 * Test terminate client.
	 *
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@Test
	public void testTerminateClient() throws NoSuchFieldException, SecurityException, 
	ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork);
		Iterator<Message> iterator = Mockito.mock(Iterator.class);
		Mockito.when(mockNetwork.iterator()).thenReturn(iterator);
		ClientTimer clientTimer = Mockito.mock(ClientTimer.class);
		when(clientTimer.isBehind()).thenReturn(true);
		Field timerMock = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("timer");
		timerMock.setAccessible(true);
		timerMock.set(client, clientTimer);
		ScheduledFuture<?> runnableMe = Mockito.mock(ScheduledFuture.class);
		when(runnableMe.cancel(false)).thenReturn(true);
		Field runnableMeField = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("runnableMe");
		runnableMeField.setAccessible(true);
		runnableMeField.set(client, runnableMe);
		client.run();
	}
	
	/**
	 * Test handle incoming quit message.
	 *
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@Test
	public void testHandleIncomingQuitMessage() throws NoSuchFieldException, SecurityException,
	ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork);
		Iterator<Message> iterator = Mockito.mock(Iterator.class);
		Mockito.when(mockNetwork.iterator()).thenReturn(iterator);
		Mockito.when(iterator.hasNext()).thenReturn(true);
		Message mess = Message.makeQuitMessage("prim");
		Mockito.when(iterator.next()).thenReturn(mess);
		Field initField = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("initialized");
		initField.setAccessible(true);
		initField.set(client, true);
		ScheduledFuture<?> runnableMe = Mockito.mock(ScheduledFuture.class);
		when(runnableMe.cancel(false)).thenReturn(true);
		Field runnableMeField = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("runnableMe");
		runnableMeField.setAccessible(true);
		runnableMeField.set(client, runnableMe);
		client.run();
	}
	
	/**
	 * Test handle incoming ill formatted message.
	 *
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@Test
	public void testHandleIncomingIllFormattedMessage() throws NoSuchFieldException, SecurityException,
	ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
		client = new ClientRunnable(mockNetwork) {
			@Override
			public String getName() {
				return "another";
			}
		};
		Iterator<Message> iterator = Mockito.mock(Iterator.class);
		Mockito.when(mockNetwork.iterator()).thenReturn(iterator);
		Mockito.when(iterator.hasNext()).thenReturn(true);
		Message mess = Message.makeBroadcastMessage("testt", "play");
		Mockito.when(iterator.next()).thenReturn(mess);
		Field initField = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("initialized");
		initField.setAccessible(true);
		initField.set(client, true);
		ScheduledFuture<?> runnableMe = Mockito.mock(ScheduledFuture.class);
		when(runnableMe.cancel(false)).thenReturn(true);
		Field runnableMeField = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredField("runnableMe");
		runnableMeField.setAccessible(true);
		runnableMeField.set(client, runnableMe);
		client.run();
	}
	
	
  /**
   * Test handle notifications no notifications.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws InvocationTargetException the invocation target exception
   * @throws NoSuchFieldException the no such field exception
   */
  @Test
  public void testHandleNotificationsNoNotifications()
      throws NoSuchMethodException, SecurityException, ClassNotFoundException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
    NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
    client = new ClientRunnable(mockNetwork) {
      @Override
      public String getName() {
        return "another";
      }
    };
    NotificationRepository notificationRepository = Mockito.mock(NotificationRepository.class);
    when(notificationRepository.getAllNewNotificationsByReceiverId(Mockito.anyInt())).thenReturn(null);
    Field notificationRepositoryField = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
        .getDeclaredField("notificationRepository");
    notificationRepositoryField.setAccessible(true);
    notificationRepositoryField.set(client, notificationRepository);
    when(mockNetwork.sendMessage(Mockito.any(Message.class))).thenReturn(true);
    Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
        .getDeclaredMethod("handleNotifications");
    init.setAccessible(true);
    init.invoke(client);

  }
  
  /**
   * Test handle notifications.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws InvocationTargetException the invocation target exception
   * @throws NoSuchFieldException the no such field exception
   */
  @Test
  public void testHandleNotifications()
      throws NoSuchMethodException, SecurityException, ClassNotFoundException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
    NetworkConnection mockNetwork = Mockito.mock(NetworkConnection.class);
    client = new ClientRunnable(mockNetwork);
    NotificationRepository notificationRepository = Mockito.mock(NotificationRepository.class);
    List<Notification> listNotifications = new ArrayList<>();
    Field notificationRepositoryField = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
        .getDeclaredField("notificationRepository");
    notificationRepositoryField.setAccessible(true);
    notificationRepositoryField.set(client, notificationRepository);
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.FRIEND_REQUEST_APPROVED);
    n.setAssociatedUserId(2);
    listNotifications.add(n);
    when(notificationRepository.getAllNewNotificationsByReceiverId(Mockito.anyInt()))
    .thenReturn(listNotifications);
    when(mockNetwork.sendMessage(Mockito.any(Message.class))).thenReturn(true);
    Method init = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
        .getDeclaredMethod("handleNotifications");
    init.setAccessible(true);
    init.invoke(client);

  }
	
	
}

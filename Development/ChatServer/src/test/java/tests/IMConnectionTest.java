package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.client.IllegalNameException;
import edu.northeastern.ccs.im.client.InvalidListenerException;
import edu.northeastern.ccs.im.client.MessageScanner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IMConnection.class)
public class IMConnectionTest {

	private IMConnection iMConnection;
	

	@Test
	public void testCreateIMConnectionNullUsername() {
	
		iMConnection = new IMConnection("localhost", 4122, null);
		iMConnection.addMessageListener(MessageScanner.getInstance());	
		assert true;
	}
	
	@Test
	public void testCreateIMConnectionEmptyUsername() {
	
		iMConnection = new IMConnection("localhost", 4122, "");
		iMConnection.addMessageListener(MessageScanner.getInstance());	
		assert true;
	}
	
	
	@Test
	public void testCreateIMConnectionNonEmptyUsername() {
	
		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		assert true;
	}
	
	public void testAddMessageListenerValidListener() {
		
		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		iMConnection.addMessageListener(MessageScanner.getInstance());	
		assert true;
	}
	
	
	@Test(expected = InvalidListenerException.class)
	public void testAddMessageListenerNull() {
		
		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		iMConnection.addMessageListener(null);		
	}
	
	@Test
	public void testConnectValidUsername() throws Exception {
		IMConnection iMConnectionSpy = PowerMockito.spy(new IMConnection("localhost", 4123, "prajakta"));
		PowerMockito.doReturn(true).when(iMConnectionSpy, "login");
		assertEquals(true , iMConnectionSpy.connect());
	}
	
	@Test(expected = IllegalNameException.class)
	public void testConnectInvalidUsername() throws Exception {
		IMConnection iMConnectionSpy = PowerMockito.spy(new IMConnection("localhost", 4123, "prajakta_12"));
		PowerMockito.doReturn(true).when(iMConnectionSpy, "login");  
		assertEquals(true , iMConnectionSpy.connect());
	}
	 
	@Test
	public void isConnectionActive() {
		iMConnection = new IMConnection("localhost", 4127, "pra");
		assertEquals(false , iMConnection.connectionActive());
	}
	
	
}

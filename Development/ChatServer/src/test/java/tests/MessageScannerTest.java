package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.client.Message;
import edu.northeastern.ccs.im.client.MessageScanner;

public class MessageScannerTest {

	
	@Test(expected = NoSuchElementException.class)
	public void testNextMessages() {
		MessageScanner messageScanner = MessageScanner.getInstance();
		while(messageScanner.hasNext()) {
			messageScanner.next();
		}
		messageScanner.next();
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveMessages() {
		MessageScanner messageScanner = MessageScanner.getInstance();
		messageScanner.remove();
	}
	
	@Test
	public void testIsAcknowlegeMessage() {
		Message messsage = Message.makeAcknowledgeMessage("testing");
		assertEquals(true ,messsage.isAcknowledge());
		
	}
	
	@Test
	public void testIsNotAcknowlegeMessage() {
		Message messsage = Message.makeQuitMessage("testing");
		assertEquals(false ,messsage.isAcknowledge());
		
	}
	
	@Test
	public void testIsBroadcastMessage() {
		Message messsage = Message.makeBroadcastMessage("testuser", "u");
		assertEquals(true ,messsage.isBroadcastMessage());
		
	}
	
	@Test
	public void testIsNotBroadcastMessage() {
		Message messsage = Message.makeQuitMessage("testing");
		assertEquals(false ,messsage.isBroadcastMessage());
		
	}
	
	@Test
	public void testNotBroadcastMessage() {
		edu.northeastern.ccs.im.Message messsage = edu.northeastern.ccs.im.Message.makeQuitMessage("q");
		assertEquals(false ,messsage.isBroadcastMessage());
		
	}
	
	@Test
	public void testIsDisplayMessage() {
		Message messsage = Message.makeBroadcastMessage("testuser", "u");
		assertEquals(true ,messsage.isDisplayMessage());
		
	}
	
	@Test
	public void testIsNotDisplayMessage() {
		Message messsage = Message.makeQuitMessage("testuser");
		assertEquals(false ,messsage.isDisplayMessage());
		
	}
	
	@Test
	public void testIsInitializationMessage() {
		Message messsage = Message.makeLoginMessage("us");
		assertEquals(true ,messsage.isInitialization());
		
	}
	
	@Test
	public void testIsNotInitializationMessage() {
		Message messsage = Message.makeQuitMessage("testuser");
		assertEquals(false ,messsage.isInitialization());
		
	}
	
	@Test
	public void testIsNotTerminationMessage() {
		Message messsage = Message.makeLoginMessage("us");
		assertEquals(false ,messsage.terminate());
		
	}
	
	@Test
	public void testIsTerminationMessage() {
		Message messsage = Message.makeQuitMessage("testuser");
		assertEquals(true ,messsage.terminate());
		
	}
	
	@Test
	public void testToStringMessageSenderNull() {
		Message messsage = Message.makeLoginMessage(null);
		assertEquals("HLO 2 -- 2 --" , messsage.toString());
	}
	
	
	@Test
	public void testMakeTypeBroadcastMessage() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method makeMethod = Class.forName("edu.northeastern.ccs.im.Message").getDeclaredMethod("makeMessage", String.class , String.class , String.class);
		makeMethod.setAccessible(true);
		makeMethod.invoke(null, MessageType.BROADCAST.toString(), "test1" , "testText");
	}
	
	@Test
	public void testMakeTypeNoneMessage() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method makeMethod = Class.forName("edu.northeastern.ccs.im.Message").getDeclaredMethod("makeMessage", String.class , String.class , String.class);
		makeMethod.setAccessible(true);
		assertEquals(null, makeMethod.invoke(null, "zzz", "test1" , "testText"));
	}
	
	
}

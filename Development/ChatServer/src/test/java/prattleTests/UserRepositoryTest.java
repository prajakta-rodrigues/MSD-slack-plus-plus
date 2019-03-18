package prattleTests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.mockito.Mockito;

import com.mysql.jdbc.Connection;

import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.repositories.UserRepository;

/**
 * The Class UserRepositoryTest.
 */
public class UserRepositoryTest {

	
	/** The user repository. */
	private UserRepository userRepository;
	
	/**
	 * Test add user success.
	 *
	 * @throws SQLException the SQL exception
	 */
	@Test
	public void testAddUserSuccess() throws SQLException {
		DataSource ds = Mockito.mock(DataSource.class);
		userRepository = new UserRepository(ds);
		Connection connection = Mockito.mock(Connection.class);
		Mockito.when(ds.getConnection()).thenReturn(connection);
		PreparedStatement value = Mockito.mock(PreparedStatement.class); 
		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
		Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
		Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
		Mockito.doNothing().when(value).setTimestamp(Mockito.anyInt(), Mockito.anyObject());
		Mockito.when(value.executeUpdate()).thenReturn(1);
		Mockito.doNothing().when(connection).close();
		assertEquals(true, userRepository.addUser(new User(0 , "test" , "pwd")));
	}
	
	/**
	 * Test add user fail.
	 *
	 * @throws SQLException the SQL exception
	 */
	@Test
	public void testAddUserFail() throws SQLException {
		DataSource ds = Mockito.mock(DataSource.class);
		userRepository = new UserRepository(ds);
		Connection connection = Mockito.mock(Connection.class);
		Mockito.when(ds.getConnection()).thenReturn(connection);
		PreparedStatement value = Mockito.mock(PreparedStatement.class); 
		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
		Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
		Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
		Mockito.doNothing().when(value).setTimestamp(Mockito.anyInt(), Mockito.anyObject());
		Mockito.when(value.executeUpdate()).thenReturn(0);
		Mockito.doNothing().when(connection).close();
		assertEquals(false, userRepository.addUser(new User(0 , "test" , "pwd")));
	}
	
	
	/**
	 * Test add user exception.
	 *
	 * @throws SQLException the SQL exception
	 */
	@Test
	public void testAddUserException() throws SQLException {
		DataSource ds = Mockito.mock(DataSource.class);
		userRepository = new UserRepository(ds);
		Connection connection = Mockito.mock(Connection.class);
		Mockito.when(ds.getConnection()).thenReturn(connection);
		PreparedStatement value = Mockito.mock(PreparedStatement.class); 
		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
		assertEquals(false, userRepository.addUser(new User(0 , "test" , "pwd")));
	}
	
	
	/**
	 * Test get user by id exception.
	 *
	 * @throws SQLException the SQL exception
	 */
	@Test
	public void testGetUserByIdException() throws SQLException {
		DataSource ds = Mockito.mock(DataSource.class);
		userRepository = new UserRepository(ds);
		Connection connection = Mockito.mock(Connection.class);
		Mockito.when(ds.getConnection()).thenReturn(connection);
		PreparedStatement value = Mockito.mock(PreparedStatement.class); 
		Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
		assertNull(userRepository.getUserByUserName("tets"));
	}
	
	
}

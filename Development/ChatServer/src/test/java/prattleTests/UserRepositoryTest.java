package prattleTests;


import static org.junit.Assert.assertEquals;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.mockito.Mockito;

import com.mysql.jdbc.Connection;

import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.UserRepository;

public class UserRepositoryTest {

	
	private UserRepository userRepository;
	
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
	
	
	
}

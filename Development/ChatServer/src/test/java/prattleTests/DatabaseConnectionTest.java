package prattleTests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.mockito.Mockito;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

public class DatabaseConnectionTest {

	@Test
	public void testGetDatasource() {
		DataSource ds = DatabaseConnection.getDataSource();
		assertNotNull(ds);
	}
	
	@Test
	public void testResultsList() throws SQLException {
		ResultSet resultSet = Mockito.mock(ResultSet.class);
		Mockito.when(resultSet.getMetaData()).thenThrow(new SQLException());
		List<Map<String, Object>> result = DatabaseConnection.resultsList(resultSet);
		assertTrue(result.isEmpty());
	}
}

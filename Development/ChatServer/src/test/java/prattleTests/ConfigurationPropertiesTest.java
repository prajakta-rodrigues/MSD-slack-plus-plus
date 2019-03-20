package prattleTests;

import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;

import org.junit.Test;

import edu.northeastern.ccs.im.server.utility.ConfigurationProperties;

public class ConfigurationPropertiesTest {

	@Test
	public void testNullProperties() throws NoSuchFieldException, SecurityException, 
	ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		Field prop = Class.forName("edu.northeastern.ccs.im.server.utility.ConfigurationProperties")
				.getDeclaredField("prop");
		prop.setAccessible(true);
		prop.set(ConfigurationProperties.getInstance(), null);
		assertNull(ConfigurationProperties.getInstance().getProperty("tp"));
	}
	
}

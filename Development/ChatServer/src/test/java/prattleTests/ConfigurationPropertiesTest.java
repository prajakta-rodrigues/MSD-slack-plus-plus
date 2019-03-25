package prattleTests;

import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;

import org.junit.Test;

import edu.northeastern.ccs.im.server.utility.ConfigurationProperties;

/**
 * The Class ConfigurationPropertiesTest.
 */
public class ConfigurationPropertiesTest {

	/**
	 * Test null properties.
	 *
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
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

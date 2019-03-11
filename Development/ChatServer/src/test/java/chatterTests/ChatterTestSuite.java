package chatterTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
		IMConnectionTest.class,
		MessageScannerTest.class
})
public class ChatterTestSuite {

}

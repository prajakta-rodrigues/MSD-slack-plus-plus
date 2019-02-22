package prattleTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        PrattleTest.class,
        ClientRunnableTest.class,
        NetworkConnectionTest.class
})
public class PrattleTestSuite {

}

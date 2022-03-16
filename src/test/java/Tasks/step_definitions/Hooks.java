package Tasks.step_definitions;

import Tasks.utilities.ConfigurationReader;
import Tasks.utilities.Driver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Hooks {

    Logger logger = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void setUp(){
        String browser= ConfigurationReader.get("browser");
        if (!browser.contains("mobile")) { Driver.get().manage().window().maximize(); }
        Driver.get().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        logger.info("===============================================================");
        logger.info("|          Environment.........: " + ConfigurationReader.get("env"));
        logger.info("|          Url.................: " + ConfigurationReader.get("url"));
        logger.info("|          Browser.............: " + ConfigurationReader.get("browser"));
        logger.info("|          Operating System....: " + System.getProperty("os.name"));
        logger.info("|          Test................: " + ConfigurationReader.get("testName"));
        logger.info("|          Tester..............: " + ConfigurationReader.get("tester"));
        logger.info("===============================================================\n");
    }

    @After
    public void tearDown(Scenario scenario){
        if (scenario.isFailed()){
            logger.error("!!!!Test Failed! check the screenshot!!!!");
            final byte[] screenshot = ((TakesScreenshot) Driver.get()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot,"image/png","screenshot");
        }
        Driver.closeDriver();
    }

}

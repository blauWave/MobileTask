package Tasks.utilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Driver {
    private Driver() {
    }
    // InheritableThreadLocal  --> this is like a container, bag, pool.
    // in this pool we can have separate objects for each thread
    // for each thread, in InheritableThreadLocal we can have separate object for that thread
    // driver class will provide separate webdriver object per thread

    public static final String browserStackUserName = "cihanguler1";
    public static final String browserStackAutomateKey = "2JGRZGzGLf9wfwCjT4MG";
    public static final String browserStackURL = "https://" + browserStackUserName + ":" + browserStackAutomateKey + "@hub-cloud.browserstack.com/wd/hub";
    public static final String sauceUserName = "cihanguler.de";
    public static final String sauceAccessKey = "8b84ac1a-be3b-4457-b76e-14eee5c790fa";
    public static final String sauceURL = "https://" + sauceUserName + ":" + sauceAccessKey + "@ondemand.eu-central-1.saucelabs.com:443/wd/hub";


    private static InheritableThreadLocal<WebDriver> driverPool = new InheritableThreadLocal<>(); //in order to run parallel test with singleton web driver

    public static WebDriver get() {
        if (driverPool.get() == null) {
            String browserParamFromEnv = System.getProperty("browser");
            String browser = browserParamFromEnv == null ? ConfigurationReader.get("browser") : browserParamFromEnv;
            DesiredCapabilities caps = new DesiredCapabilities();
            ChromeOptions chromeOptions = new ChromeOptions();

            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    caps = new DesiredCapabilities();
                    /**
                     *We have disabled the cookies in below ChromeOptions
                     * You need to add this feature to your configuration.properties
                     * Add cookiesEnableDisable=2  (disable the cookies)
                     * Add cookiesEnableDisable =0  (enable the cookies)
                     */
                    chromeOptions = new ChromeOptions();
                    Map<String, Object> prefs = new HashMap<String, Object>();
                    Map<String, Object> profile = new HashMap<String, Object>();
                    Map<String, Object> contentSettings = new HashMap<String, Object>();

                    contentSettings.put("cookies", ConfigurationReader.get("cookiesEnableDisable"));
                    profile.put("managed_default_content_settings", contentSettings);
                    prefs.put("profile", profile);
                    chromeOptions.setExperimentalOption("prefs", prefs);
                    caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                    driverPool.set(new ChromeDriver(chromeOptions));
                    break;
                case "chrome-headless":
                    WebDriverManager.chromedriver().setup();
                    driverPool.set(new ChromeDriver(new ChromeOptions().setHeadless(true)));
                    break;
                case "mobile_chrome_appium":
                    caps = new DesiredCapabilities();

                    caps.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.ANDROID);
                    caps.setCapability(MobileCapabilityType.VERSION,"10.0+");
                    caps.setCapability(MobileCapabilityType.DEVICE_NAME,"Pixel 2 API 30");
                    //we are telling we want to open mobile chrome browser on the phone
                    caps.setCapability(MobileCapabilityType.BROWSER_NAME, BrowserType.CHROME);
                    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"UiAutomator2");
                    try {
                        //driverPool.set(new RemoteWebDriver(new URL("http://localhost:4723/wd/hub"),caps)); //it works also without appium
                        driverPool.set(new AppiumDriver<>(new URL("http://localhost:4723/wd/hub"),caps));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;

                case "mobile_chrome_saucelabs":
                    caps = DesiredCapabilities.android();
                    caps.setCapability("appiumVersion", "1.17.1");
                    caps.setCapability("deviceName","Google Pixel 3 GoogleAPI Emulator");
                    caps.setCapability("deviceOrientation", "portrait");
                    caps.setCapability("browserName", "Chrome");
                    caps.setCapability("platformVersion", "10.0");
                    caps.setCapability("platformName","Android");

                    try {
                        driverPool.set(new AppiumDriver<>(new URL(sauceURL), caps));
                        //driverPool.set(new RemoteWebDriver(new URL(sauceURL), caps)); //it works also without appium
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;

                case "mobile_chrome_browserstack":

                    caps = new DesiredCapabilities();
                    caps.setCapability("os_version", "10.0");
                    caps.setCapability("device", "Google Pixel 3");
                    caps.setCapability("real_mobile", "false");
                    caps.setCapability("project", "Task");
                    caps.setCapability("name", "Login");
                    caps.setCapability("browserstack.local", "false");
                    caps.setCapability("browserstack.timezone", "Europe/Berlin");

                    try {
                        //driverPool.set(new AppiumDriver<>(new URL(browserStackURL),caps));
                        driverPool.set(new RemoteWebDriver(new URL(browserStackURL),caps));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return driverPool.get();
    }
    public static void closeDriver() {
        driverPool.get().quit();
        driverPool.remove();
    }
}
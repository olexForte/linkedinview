package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
//import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Driver provider
 */
public class DriverProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverProvider.class);

    static String OS_EXTENTION = (System.getProperty("os.name").toLowerCase().contains("win")) ? ".exe" : "_mac";
    public static String FIREFOX_PATH = "drivers/geckodriver" + OS_EXTENTION;
    public static String CHROME_PATH = "drivers/chromedriver" + OS_EXTENTION;

    //private static WebDriver instance;
    public static ThreadLocal<WebDriver> instance = new ThreadLocal<WebDriver>();

    static String BROWSER_TYPE;

    static public FirefoxDriver getFirefox() {

        System.setProperty("webdriver.gecko.driver", FIREFOX_PATH);

        DesiredCapabilities caps = DesiredCapabilities.firefox();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

        return new FirefoxDriver(caps);

    }

    static public ChromeDriver getChrome(boolean runHeadless){

        LOGGER.info("Creation of WebDriver started ...");

        try {

            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);

            chromePrefs.put("download.default_directory", FileIO.OUTPUT_DIR);
            chromePrefs.put("download.prompt_for_download", false);
            // chromePrefs.put("profile.default_content_setting_values.automatic_downloads",2);
            // chromePrefs.put("download.directory_upgrade", true);

            chromePrefs.put("credentials_enable_service", false);
            chromePrefs.put("profile.password_manager_enabled", false);
            chromePrefs.put("profile.default_content_settings.popups", 0);

            // disable flash and the PDF viewer
            chromePrefs.put("plugins.plugins_disabled", new String[]{
                    "Adobe Flash Player",
                    "Chrome PDF Viewer"
            });

            chromePrefs.put("plugins.always_open_pdf_externally", true);
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePrefs);
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--start-maximized");
            //options.addArguments("--kiosk");

            //if ((PropertiesList.getConfigProperty("Headless") != null && PropertiesList.getConfigProperty("Headless").equals("true")) ||
             if(runHeadless)
                options.addArguments("--headless");


            synchronized (DataProvider.class) {
                if (PropertiesList.getConfigProperty("ChromeDriverVersion") != null)
                    WebDriverManager.chromedriver().driverVersion(PropertiesList.getConfigProperty("ChromeDriverVersion")).setup();
                else if (PropertiesList.getConfigProperty("ChromeVersion") != null)
                    WebDriverManager.chromedriver().browserVersion(PropertiesList.getConfigProperty("ChromeVersion")).setup();
                else
                    WebDriverManager.chromedriver().setup();
            }

            return new ChromeDriver(options);
        } catch (Exception e){
            LOGGER.error("Driver creation failed: " + e.getMessage());
            return null;
        }

    }

//
//
//    static public ChromeDriver getProxyDriver() {
//        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//        ChromeOptions options = new ChromeOptions();
//
//        System.setProperty("webdriver.chrome.driver", CHROME_PATH);
//
//        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
//        chromePrefs.put("profile.default_content_settings.popups", 0);
//        // chromePrefs.put("profile.managed_default_content_settings.plugins", 0);
//        // chromePrefs.put("profile.default_content_settings.popups", 2);
//        // chromePrefs.put("profile.managed_popups_blocked_for_urls", 0);
//
//        chromePrefs.put("download.default_directory", FileIO.OUTPUT_DIR);
//        chromePrefs.put("download.prompt_for_download", false);
//        // chromePrefs.put("profile.default_content_setting_values.automatic_downloads",2);
//        // chromePrefs.put("download.directory_upgrade", true);
//
//        chromePrefs.put("credentials_enable_service", false);
//        chromePrefs.put("profile.password_manager_enabled", false);
//        chromePrefs.put("profile.default_content_settings.popups", 0);
//
//        // disable flash and the PDF viewer
//        chromePrefs.put("plugins.plugins_disabled", new String[] {
//                "Adobe Flash Player",
//                "Chrome PDF Viewer"
//        });
//        options.addArguments("plugins.plugins_disabled=Chrome PDF Viewer");
//
//        options.setExperimentalOption("prefs", chromePrefs);
//        options.addArguments("start-maximized");
//        options.addArguments("disable-infobars");
//        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
//        capabilities.setCapability("chrome.switches", Arrays.asList("--incognito"));
////        options.addArguments("--disable-web-security");
////        options.addArguments("--no-proxy-server");
//      /*  Map<String, Object> prefs = new HashMap<String, Object>();
//        prefs.put("credentials_enable_service", false);
//        prefs.put("profile.password_manager_enabled", false);
//        prefs.put("profile.default_content_settings.popups", 0);
//        prefs.put("download.default_directory", FileHelper.DEFAULT_DOWNLOAD_DIR);
//        options.setExperimentalOption("prefs", prefs);
//        */
//
//        options.addArguments("--start-maximized");
//        options.addArguments("--test-type");
//
//        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
//        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
//
//        BrowserProxy.getInstance().startServer(capabilities);
//
//        // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
//        BrowserProxy.getInstance().proxyServer.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
//
//        ChromeDriver driver = new ChromeDriver(capabilities);
//
//        driver.get("about://blank");
//
//        return driver;
//    }

    public static WebDriver getDriver(boolean headless) {

        instance.set(getChrome(headless));

        //return instance;
        return instance.get();
    }

    public static void closeDriver(){
        //instance.quit();
        //instance.get().quit();
        //instance = null;
        instance.set(null);
    }

    public static String getCurrentBrowserName() {
        if (BROWSER_TYPE == null)
           switch (PropertiesList.getConfigProperty("Driver")) {
               case "firefox" : BROWSER_TYPE = BrowserType.FIREFOX;
                   break;
               case "proxy":  BROWSER_TYPE = "PROXY";
                   break;
               default: BROWSER_TYPE = BrowserType.CHROME;
           }

        return BROWSER_TYPE;
    }
}

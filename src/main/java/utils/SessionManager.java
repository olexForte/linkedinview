package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

/**
 * Holds all Session related properties and TestRailIDs of executed tests
 */
public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static Properties sessionProperties = new Properties();
    private static Properties sessionTests = new Properties();

    static String STARTED_FLAG = "STARTED";
    static String PASS_FLAG = "PASS";
    static String FAIL_FLAG = "FAIL";

    static String SCREENSHOT_KEY = "SCREENSHOTS";

    public static String getSessionID() {
        if(sessionProperties.get("SESSION_ID") == null) {
            sessionProperties.put("SESSION_ID", Tools.getCurDateTime());
            logger.info("Session ID: " + (String) sessionProperties.get("SESSION_ID"));
        }
        return (String) sessionProperties.get("SESSION_ID");
    }

    public static void addToSession(String key, String value){
        synchronized (SessionManager.class){
            sessionProperties.put(key, value);
        }
    }

    public static String getFromSession(String key){
            return String.valueOf(sessionProperties.get(key));
    }

    public static void addToTests(String key, String value){
        synchronized (SessionManager.class){
            sessionTests.put(key, value);
        }
    }

    public static String getFromTests(String key){
        return String.valueOf(sessionTests.get(key));
    }

    /**
     * add test id to list of started tests
     * @param ids - list of TestRail IDs
     */
    public static void addTest(String ids){
        logger.info("Test started: " + ids);
        for(String id : ids.split(","))
            addToTests(id, STARTED_FLAG);
    }

    /**
     * mark test ass Passed in list of started tests
     * @param ids - list of TestRail IDs
     */
    public static void passTest(String ids) {
        logger.info("Test passed: " + ids);
        for(String id : ids.split(","))
            addToTests(id, PASS_FLAG);
    }
//
//    public static void updateTestStatus(String id, boolean testStatus){
//        logger.info("Test updated: " + id);
//        if(testStatus)
//            addToTests(id, PASS_FLAG);
//        else
//            addToTests(id, FAIL_FLAG);
//    }

    /**
     * returns status TRUE if tests finished
     * @param id
     * @return
     */
    public static boolean getTestStatus(String id){
        String status = getFromTests(id);
        return status.equals(PASS_FLAG);
    }

    public static String[] getAllTestIDs() {
        return Arrays.copyOf(sessionTests.keySet().toArray(), sessionTests.keySet().toArray().length, String[].class);
    }

    public static void addScreenshotNameToSession(String screenshotLocation) {
        addToSession(SCREENSHOT_KEY, getFromSession(SCREENSHOT_KEY) + screenshotLocation + ";");
    }

    public static String[] getScreenshotNamesFromSession() {
       return getFromSession(SCREENSHOT_KEY).split(";");
    }
}

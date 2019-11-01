package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Interation with properties
 */
public class PropertiesList {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesList.class);

    static String CONFIG_FILE = System.getProperty("config");
    static String PROPERTIES_FILE =  (( CONFIG_FILE == null ) ? "default" : CONFIG_FILE) + ".properties";
    static private Properties localProps = loadProperties();

    public static String getConfigProperty(String fieldName){
        String result   = null;

        if(System.getProperty(fieldName) != null)
            return System.getProperty(fieldName);

        if(localProps.getProperty(fieldName) != null)
            return localProps.getProperty(fieldName);

        return result;
    }

    public static Properties loadProperties(){
        Properties result = new Properties();

        try {

            //open file
            File file = new File(PROPERTIES_FILE);
            //open input stream to read file
            FileInputStream fileInput = new FileInputStream(file);
            //create Properties object
            result = new Properties();
            //load properties from file
            result.load(fileInput);
            //close file
            fileInput.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
           // ReporterManager.Instance.fail("Config was not found");
        } catch (IOException e) {
            e.printStackTrace();
            //ReporterManager.Instance.fail("Config was not opened");
        } catch (Exception e){
            e.printStackTrace();
            //ReporterManager.Instance.fail("Field was not found: " + PROPERTIES_FILE);
        }
        return result;
    }

    public static void saveCurrentState() {
        try {
            localProps.store(new FileOutputStream(new File(PROPERTIES_FILE)), "Saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setConfigProperty(String key, String value) {
        localProps.setProperty(key, value);
    }
}


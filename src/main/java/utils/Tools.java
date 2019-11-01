package utils;


import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Common tools
 */

public class Tools {

    /**
     * get string in format yyyyMMdd_HHmmss
     * @return
     */
    public static String getCurDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * get user email in format yyyyMMdd_HHmmss_*@gmail.com
     * @return
     */
    public static String getRandomUserEmail() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_");
        return sdf.format(new Date(System.currentTimeMillis())) + String.valueOf(System.currentTimeMillis()).substring(10,13) + "@gmail.com";
    }

    /**
     * get random number in range 0 - i
     * @param i
     * @return
     */
    public static String getRandomNumber(int i) {
        Random r = new Random();
        return String.valueOf(r.nextInt(i));
    }


    /**
     * compare lists
     * @param fromSource1
     * @param fromSource2
     */
    public static boolean simpleCompareTwoLists(List fromSource1, List fromSource2){

        boolean result = true;

        // search for records in Source2 but not in Source1 response
        int i = 0;
        for (Object entityFromSource2 : fromSource2) {
            i++;
            boolean found = false;
            //LOGGER.info(i + " - " + entity);
            if(entityFromSource2 != null) {
                for (Object entityFromSource1 : fromSource1) {
                    if (entityFromSource1 == null)
                        break;
                    if (entityFromSource1.toString().equals(entityFromSource2.toString())) {
                        found = true;
                        break;
                    }
                }
                ;
                if (!found) {
                    return false;
                }
            }
        }


        //search for records in Source1 response but not in Source2 report
        i = 0;
        for (Object entityFromSource1 : fromSource1) {
            i++;
            boolean found = false;
            if (entityFromSource1 != null) {
                for (Object entityFromSource2 : fromSource2) {
                    if (entityFromSource2 == null)
                        break;
                    if (entityFromSource1.toString().equals(entityFromSource2.toString())) {
                        found = true;
                        break;
                    }
                }
                ;
            }
            if(!found) {
                return false;
            }
        }

        return result;
    }



    /**
     * Count number of digits after "."
     * @param val
     * @return
     */
    public static int getLevelOfPrecision(String val) {
        return (val.split("\\."))[1].trim().length();
    }


    /**
     * return month name by index (0-11)
     * @param num
     * @return
     */
    public static String getMonthNameByNumber(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    /**
     * get random string
     * @param length
     * @return
     */
    public static String getRndString(int length) {
        String result = "";
        for(int i = 0; i < length; i++)
            result = result + String.valueOf(i).charAt(0);
        return result;
    }

    /**
     * Validate list is sorted
     * @param allStations
     * @param ascending (1 - ascending , -1 - descending)
     * @return
     */
    public static  boolean validateListSorted(List<String> allStations, int ascending) {
        boolean result = true;
        for(int i = 0; i < allStations.size()-1; i++){
            if(allStations.get(i).compareTo(allStations.get(i + 1)) == ascending && allStations.get(i).compareTo(allStations.get(i + 1)) != 0)
                return false;
        }
        return result;
    }


    /**
     * compare strings letter by letter with respect to inserted chars <br>
     *     required for comparison of OCR recognized strings
     * @param str1
     * @param str2
     * @return
     */
    public static int compareStrings(String str1, String str2) {
        int result = 0;
        char[] chars1 = str1.toCharArray();
        char[] chars2 = str2.toCharArray();
        char insertedChar = 0;
        if(chars1.length > chars2.length) {
            for (int i = 0; i < chars1.length-1; i++) {
                if (chars1[i + (insertedChar==0?0:1)] != chars2[i]) {
                    insertedChar = chars2[i];
                    result++;
                    if(result == 2)
                        return result;
                }
            }
        }else {
            for (int i = 0; i < chars2.length-1; i++) {
                if (chars2[i + (insertedChar==0?0:1)] != chars1[i]) {
                    insertedChar = chars1[i];
                    result++;
                    if(result == 2)
                        return result;
                }
            }
        }

        return result;
    }

    /**
     * move timestamp to expected format
     * @param time
     * @return
     */
    public static String fixTimeStamp(String time) {
        String result = time.trim().replaceAll("A$", "AM").replaceAll("P$", "PM");

        if(result.length() < 7)
            result = "0" + result;

        return result;
    }
}

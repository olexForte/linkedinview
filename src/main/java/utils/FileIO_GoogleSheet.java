package utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.charset.Charset.defaultCharset;

/**
 * Interation with file system
 */
public class FileIO_GoogleSheet {

    public static String TARGET_FOLDER = "target";
    static String DATA_RESOURCES = "src/main/resources/data/";
    static String MAIN_RESOURCES = "src/main/resources/";
    static String CONFIG_FILE = System.getProperty("config");
    static final String EXPECTED_FILES_SUBDIRNAME = "expectedFiles";

    //folder in .target with downloaded/created during session files)
    public static String OUTPUT_DIR = "./target/" + SessionManager.getSessionID();

    /**
     * get full file path from Data directory
     * @param filename
     * @return
     */
    public static String getDataFile(String filename){
        return DATA_RESOURCES + PropertiesList.getConfigProperty("EnvType") + "/" + filename ;
    }

    /**
     * get content of file from Data dir as a string
     * @param filename
     * @return
     */
    public static String getDataFileContent(String filename){
        String result = null;
        try {
            result = FileUtils.readFileToString(new File(DATA_RESOURCES + PropertiesList.getConfigProperty("EnvType") + "/" + filename), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    /**
     * get content of file as string
     * @param filename
     * @return
     */
    public static String getFileContent(String filename){
        String result = null;
        try {
            result = FileUtils.readFileToString(new File(filename), defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }

    /**
     * get list of data files (.xlsx) in specified dir
     * @param dirName
     * @return
     */
    public static List<String> getDataFilesInDirectory(String dirName) {
        List<String> filesInDir = new ArrayList<String>();
        File folder = new File(DATA_RESOURCES + PropertiesList.getConfigProperty("EnvType") + "/" + dirName );
        File[] listOfFiles = folder.listFiles();
        for(File current : listOfFiles){
            if(current.getName().contains(".xlsx"))
                filesInDir.add(DATA_RESOURCES + PropertiesList.getConfigProperty("EnvType") + "/" + dirName + "/" + current.getName());
        }
        return filesInDir;
    }

    /**
     * create dir
     * @param path
     * @throws IOException
     */
    public static void createDir(String path) throws IOException {
        FileUtils.forceMkdir(new File(path));
    }

    /**
     * get full file path from Output Directory (folder in .target with downloaded files)
     * @param fileName
     * @return
     */
    public static String getFileFromDownloadDir(String fileName) {
        return OUTPUT_DIR + File.separator + fileName;
    }

    /**
     * get full file path from Data folder in EXPECTED_FILES_SUBDIRNAME directory
     * @param fileName
     * @return
     */
    public static String getDataFileFromExpectedSubdir(String fileName) {
        return DATA_RESOURCES + PropertiesList.getConfigProperty("EnvType") + File.separator + EXPECTED_FILES_SUBDIRNAME + File.separator + fileName;
    }

    /**
     * wait for file existance
     * @param actualFileLocation
     * @return
     */
    public static boolean waitForFile(String actualFileLocation) {
        boolean result = false;
        boolean timeoutReached = false;
        int currentTime = 0;
        while(!timeoutReached){
            if(Files.exists(Paths.get(actualFileLocation))){
                return true;
            }
            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //
            }
            if(currentTime >= Integer.valueOf(PropertiesList.getConfigProperty("DefaultTimeoutInSeconds")))
                timeoutReached = true;
        }
        return result;
    }


    public static void appendLineToFile(String dataFile, String data) throws IOException {
        File file = new File(dataFile);
        FileWriter fr = new FileWriter(file, true);
        BufferedWriter br = new BufferedWriter(fr);
        br.write(data + "\n");

        br.close();
        fr.close();
    }

    public static void appendToFile(String dataFile, String data) throws IOException {
        File file = new File(dataFile);
        FileWriter fr = new FileWriter(file, true);
        BufferedWriter br = new BufferedWriter(fr);
        br.write(data);

        br.close();
        fr.close();
    }

    public static File getFileFromMainResources(String fileName) {
        return new File(MAIN_RESOURCES + fileName );
    }

    public static boolean waitForDownloadedFile(String name){
        return waitForFile(name);
    }

    public static String archiveFiles(ArrayList<String> listOfResultsFile) {
        String archiveName = SessionManager.getSessionID() + ".zip";

        try (FileOutputStream fos = new FileOutputStream(archiveName);
             ZipOutputStream zipOut = new ZipOutputStream(fos);)
        {
            for (String file : listOfResultsFile) {
                if(file != null) {
                    File fileToZip = new File(file);
                    zipFile(fileToZip, fileToZip.getName(), zipOut);
                }
            };
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        };

        return archiveName;
    }

        private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
            if (fileToZip.isHidden()) {
                return;
            }
            if (fileToZip.isDirectory()) {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                    zipOut.closeEntry();
                }
                File[] children = fileToZip.listFiles();
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }

    /**
     * check if file has substring
     * @param fileName
     * @param substring
     * @return
     */
    public static boolean substringWasFoundInFile(String fileName, String substring) {

        File file = new File(fileName);

        try {
            Scanner scanner = new Scanner(file);
            int lineNum = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if(line.contains(substring)) {
                    return true;
                }
            }
        } catch(FileNotFoundException e) {
            //handle this
        }

        return false;
    }

    public static void appendToResults(
            String reportFileBase,
            String parentName,
            String parentTitle,
            String parentCompany,
            String parentEmail,
            String parentPhone,
            String parentInfo,
            String name,
            String title,
            String company,
            String allContactsInfo) throws IOException {

        String reportFile = reportFileBase + ".html";

        FileIO_GoogleSheet.appendToFile(reportFile, "<tr>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + parentName + "</td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + parentTitle + " </td> ");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + parentCompany + " </td> ");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + parentEmail + " </td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + parentPhone + " </td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + parentInfo + " </td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + name + "</td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + title + "</td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + company + "</td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "<td> " + allContactsInfo + "</td>");
        FileIO_GoogleSheet.appendToFile(reportFile, "</tr>\n");

        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(parentName) + ", ");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(parentTitle) + " , ");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(parentCompany) + " , ");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(parentEmail) + " ,");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(parentPhone) + " ,");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(parentInfo) + " ,");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(name) + ",");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(title) + ",");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(company) + ",");
        FileIO_GoogleSheet.appendToFile(reportFileBase, " " + Tools.cleanForCell(allContactsInfo) + "\n");

    }

    public static void closeResultsFile(String reportFile) throws IOException {
        FileIO_GoogleSheet.appendToFile(reportFile + ".html", "</table>");
    }

    public static void openReportFile(String reportFile) throws IOException {
        FileIO_GoogleSheet.appendToFile(reportFile+ ".html", "<table>");
        FileIO_GoogleSheet.appendToResults(reportFile,"1st Connection Name","1st Connection Title", "1st Connection Company", "1st Connection Email","1st Connection Phone", "1st Connection Info","2nd Connection Name","2nd Connection Title","2nd Connection Company","2nd Connection Links");
    }

    public static void copyFile(String fileName1, String fileName2) throws IOException {
        FileUtils.copyFile(new File(fileName1), new File(fileName2));
    }
}


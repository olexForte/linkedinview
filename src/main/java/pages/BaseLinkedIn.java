package pages;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.LoggerFactory;
import utils.DriverProvider;
import utils.FileIO;
import utils.SessionManager;
import utils.Tools;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static pages.BasePage.driver;

public class BaseLinkedIn implements Runnable{

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BaseLinkedIn.class);
    private static final int MAX_NUMBER_OF_FAILURES = 1;
    int numberOfFailures = 0;

    public volatile boolean running = true;

    String reportFile;
    String login;
    String password;
    String title1;
    String contactToSearch;
    String listOfCompanies;
    String title2;


    String sessionFile;
    WebDriver driverLocal;

    public BaseLinkedIn(WebDriver driver, String reportFile, String login, String password, String title1, String listOfCompanies, String title2, String contacts, String sessionFile) {
        this.driverLocal = driver;
        this.reportFile = reportFile;
        this.login = login.trim();
        this.password = password;
        this.title1 = title1.trim();
        this.listOfCompanies = listOfCompanies;
        this.title2 = title2.trim();
        this.contactToSearch = contacts.trim();
        this.sessionFile = sessionFile;
    }

    public boolean readAllConnectionsWithTitle() throws Exception {
        return readAllConnectionsWithTitle(reportFile, login, password, title1, listOfCompanies, title2, contactToSearch, sessionFile);
    }

    public boolean readAllConnectionsWithTitle(String reportFile, String login, String password, String titleToSearch1, String listOfCompanies, String titleToSearch2, String contactToSearch, String sessionFile) throws Exception {

        boolean result = false;

        FileIO.openReportFile(reportFile);
//        FileIO.appendToFile(reportFile, "<table>");

        try {
            //driver.set(DriverProvider.getDriver());
            driver.set(driverLocal);

            LoginPage loginPage = new LoginPage();
            loginPage.open();

//            if(!loginPage.loginPageWasOpened())
//                loginPage.clickSubmit();
//
                loginPage
                        .enterUsername(login);
//                        .enterPassword(password)
//                        .submitForm()
//                        .waitForPageToLoadAndSpinnerToDisappear();


            MainPage mainPage = new MainPage();

            int DEFAULT_TIMEOUT = 300;
            LocalDateTime expectedEnd = LocalDateTime.now().plusSeconds(DEFAULT_TIMEOUT);
            while(LocalDateTime.now().isBefore(expectedEnd)) {
                Thread.sleep(5000);
                try {
                    if(mainPage.isOpen()) break;
                } catch (Exception e){
                    //expected
                }
            }

            mainPage.openUserConnections();

            //search contact OR by title1
            if(contactToSearch.equals("")) {
                mainPage.
                        openFilters().
                        setSearchForFirstLevel().
                        setFilterTitle(titleToSearch1);
                mainPage.applyFilter();
            } else
                mainPage.setContactSearch(contactToSearch);


            if (mainPage.wasNoResultsFound())
                throw new Exception("No results found");
            if (!mainPage.wasResultsFound())
                throw new Exception("No result items found");


            boolean hasNext = true;
            while (hasNext) {
                int numberOfItemsOnCurrentResultsPage = mainPage.getNumberOfResultsFromCurrentPage();
                LOGGER.info("Primary results on current page: " + numberOfItemsOnCurrentResultsPage);
                for (int i = 0; i < numberOfItemsOnCurrentResultsPage; i++) {
                    LOGGER.info("Contact " + i);
                    //mainPage.scrollUsingJS(1000);
                    mainPage.scrollToItemInResults(i);
                    String parentName = mainPage.getNameFromResults(i);
                    LOGGER.info("1st: " + parentName);

                    if( FileIO.substringWasFoundInFile(sessionFile, parentName)) {
                        LOGGER.info("Skipped (already processed first level contact) " + parentName);
                        continue;
                    };

//                    if( !contactToSearch.equals("") && !contactToSearch.contains(parentName)){
//                        LOGGER.info("Skipped (not in list of expected contactToSearch) " + parentName);
//                        continue;
//                    };
                    LOGGER.info("Click on Primary: " + parentName);
                    mainPage.openItemInResults(i);   // new tab opened

//                    try {
//
                            String parentTitleAndCompany = mainPage.getTitleAndCompanyFromContactPage();
                        String parentTitle = parentTitleAndCompany.split(mainPage.TITLE_SEPARATOR)[0].replace("Title", "");
                        String parentCompany = parentTitleAndCompany.split(mainPage.TITLE_SEPARATOR)[1].replace("Company Name", "");

                        LOGGER.info("Open contact info");
                        mainPage.openContactsPanel();
                        String parentEmail = mainPage.getCurrentContactEmail();
                        String parentPhone = mainPage.getCurrentContactPhone();

                        mainPage.goBack();

                        LOGGER.info(parentTitleAndCompany + " - " + parentEmail);

                        if (mainPage.isConnectionsOpenForContact()) {
                            LOGGER.info("Connections are opened");
                            mainPage.openConnections().
                                    openCurrentFilter().
                                    setSearchForSecondLevel().
                                    setFilterTitle(titleToSearch2).
                                    setCurrentCompanies(listOfCompanies).
                                    applyFilter();

                            if (mainPage.wasNoResultsFound() || !mainPage.wasResultsFound()) {
                                LOGGER.warn("No results found");
                                //mainPage.goBack();
                                mainPage.closeTab();
                                FileIO.appendToResults(reportFile, parentName, parentTitle, parentCompany, parentEmail, parentPhone, "No results found", "", "", "");

                            } else { // some secondary results were found
                                LOGGER.info("Secondary results were found");
                                boolean hasSecondaryNext = true;
                                while (hasSecondaryNext) {
                                    int numberOfItemsOnCurrentResultsPageForContact = mainPage.getNumberOfResultsFromCurrentPage();
                                    LOGGER.info("Secondary results on current page: " + numberOfItemsOnCurrentResultsPage);
                                    for (int j = 0; j < numberOfItemsOnCurrentResultsPageForContact; j++) {
                                        LOGGER.info("2nd Contact " + j);
                                        mainPage.sleepFor(2000);
                                        mainPage.scrollToItemInResults(j);
                                        String name = mainPage.getNameFromResults(j);
                                        LOGGER.info(name);

                                        if( FileIO.substringWasFoundInFile(sessionFile, name)) {
                                            LOGGER.info("Skipped (already processed second level contact) " + name);
                                        } else {
                                            LOGGER.info("Click on Secondary: " + name);
                                            mainPage.clickOnItemInResults(j);
                                            String titleAndCompany = mainPage.getTitleAndCompanyFromContactPage();

                                            LOGGER.info("Open contacts panel");
                                            mainPage.openContactsPanel();
                                            String allContactsInfo = mainPage.getCurrentContactDetails();
                                            LOGGER.info("Found contact info: " + allContactsInfo);

                                            mainPage.goBack();

                                            String title = titleAndCompany.split(mainPage.TITLE_SEPARATOR)[0].replace("Title", "");
                                            String company = titleAndCompany.split(mainPage.TITLE_SEPARATOR)[1].replace("Company Name", "");

                                            LOGGER.info(" - " + title);

                                            mainPage.goBack(); //go back to secondary search
                                            mainPage.waitForPageToLoad();

                                            FileIO.appendToResults(reportFile, parentName, parentTitle, parentCompany, parentEmail, parentPhone, name, title, company, allContactsInfo);
                                            FileIO.appendLineToFile(sessionFile, name);
                                        };
                                    };
                                    hasSecondaryNext = mainPage.isNextPageOfResultsAvailable();
                                    if (hasSecondaryNext) {
                                        LOGGER.info("Click Next link on Secondary search results");
                                        mainPage.clickOnNextResultLink();
                                    }else {
                                        LOGGER.info("No Next link");
                                        mainPage.closeTab();
                                    }
                                };
                            };

                        } else { // no connections opened

                            //mainPage.goBack();
                            LOGGER.info("No opened connections");
                            mainPage.closeTab();
                            FileIO.appendToResults(reportFile, parentName, parentTitle, parentCompany, parentEmail, parentPhone, "No open connections", "", "", "");
//                            FileIO.appendToFile(reportFile, "<tr>");
//                            FileIO.appendToFile(reportFile, "<td> " + parentName + "</td>");
//                            FileIO.appendToFile(reportFile, "<td> " + parentTitle + " </td> ");
//                            FileIO.appendToFile(reportFile, "<td> " + parentCompany + " </td> ");
//                            FileIO.appendToFile(reportFile, "<td> " + parentEmail + " </td>");
//                            FileIO.appendToFile(reportFile, "<td> " + parentPhone + " </td>");
//                            FileIO.appendToFile(reportFile, "<td> No open connections </td>");
//                            FileIO.appendToFile(reportFile, "</tr>");
                        };

                        FileIO.appendLineToFile(sessionFile, parentName); // add name to session
                        if (!contactToSearch.equals("")) {
                            String sessionContent = FileIO.getFileContent(sessionFile);
                            boolean found = true;
                            if (sessionContent.toLowerCase().contains(contactToSearch.toLowerCase()))
//                        for(String contactToProcess : contactToSearch.split(";")){
//                            if(!sessionContent.contains(contactToProcess)) {
//                                found = false;
//                                break;
//                            }
//                        };
                                if (found) {
                                    LOGGER.info("All required contactToSearch were found");
                                    BasePage.takeScreenshot(driver(), Tools.getCurDateTime());
                                    FileIO.closeResultsFile(reportFile);
                                    //FileIO.appendToFile(reportFile, "</table>");
                                    //close driver
                                    DriverProvider.closeDriver();
                                    driver().quit();

                                    return true;
                                }

                        }
//                    }catch (Exception e) {
//                        mainPage.closeItemTab(); // go back to original page with first search results
//                        LOGGER.error(e.getMessage());
//                    }
                };
                hasNext = mainPage.isNextPageOfResultsAvailable();
                if (hasNext) {
                    LOGGER.info("Click Next on Primary test results");
                    mainPage.clickOnNextResultLink();
                }
            }
            result = true;
            FileIO.closeResultsFile(reportFile);
            //FileIO.appendToFile(reportFile, "</table>");
            //close driver
            BasePage.takeScreenshot(driver(), Tools.getCurDateTime());
            DriverProvider.closeDriver();
            driver().quit();
            terminate();
        } catch (Exception e){
            LOGGER.error(e.getMessage() + "\n"  );
            FileIO.closeResultsFile(reportFile);
            //FileIO.appendToFile(reportFile, "</table>");
            //close driver
            BasePage.takeScreenshot(driver(), Tools.getCurDateTime());
            //DriverProvider.closeDriver();
            //driver().quit();
            throw e;
        }
        return result;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (readAllConnectionsWithTitle()) {
                    LOGGER.info("Finished: " + Tools.getCurDateTime());
                    terminate();
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                numberOfFailures++;
                if(numberOfFailures >= MAX_NUMBER_OF_FAILURES){
                    LOGGER.error("Too many failures during execution");
                    terminate();
                }
            }
        };
    }

    public void terminate() {
        running = false;
    }
}

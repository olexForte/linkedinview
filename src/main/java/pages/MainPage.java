package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Tools;

/**
 * Linkedin Login Page
 */
public class MainPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainPage.class);
    public static final String TITLE_SEPARATOR = " // ";

    static String connectionsURL = "https://www.linkedin.com/mynetwork/invite-connect/connections/";

    private static MainPage instance;
    public static MainPage Instance = (instance != null) ? instance : new MainPage();

    public MainPage() {
        pageURL = "";
    }

    By connectionsLink = By.xpath("//a[@data-test-global-nav-link='mynetwork'] | //a[@data-alias='relationships']");

    By connectionsSecondLink = By.xpath("(//div[contains(.,'Connections') and @class='mn-community-summary__entity-info'])[1]");

    By searchWithFilters = By.xpath(("//a[@data-control-name='search_with_filters']"));

    By allFiltersLink = By.xpath(("//span[contains(.,'All Filters')] | //button[@aria-label='All filters']"));

    By filterFieldTitleInput = By.xpath("//input[@id='search-advanced-title'] | //label[contains(.,'Title')]/input[@class='mt1']");
    By filterFieldCompanyInput = By.xpath(("(//input[@aria-label='Add a current company'])[1] | //label[contains(.,'Company')]/input[@class='mt1']"));

    By filterFieldContactInput = By.xpath(("//input[@id='mn-connections-search-input']"));

    By firstConnectionsCheckbox = By.xpath("(//div[@id='connections-facet-values'])[1]//label[contains(.,'1st')]/preceding::input[1] | //input[@id='network-F']");
    By secondConnectionsCheckbox = By.xpath("(//div[@id='connections-facet-values'])[1]//label[contains(.,'2nd')]/preceding::input[1] | //input[@id='network-S']");

    By connectionsOfField = By.xpath(("//legend[contains(.,'Connections of')]/following::ol[1]//input[1]"));

    By applyFilters = By.xpath("(//button[contains(.,'Apply')])[1] | (//button[contains(.,'Show results')])[1]"); //button[@id='ember764'] | //button[@data-control-name='all_filters_apply']");


    By nextPageLink = By.xpath("//button[@aria-label='Next'] | //span[@class='artdeco-button__text'][.='Next']");

    By resultsOfSearch = By.xpath(("//ul[contains(@class,'search-results__list')]/li | //div[@class='mn-connection-card ember-view'] | //li[@class='mn-connection-card artdeco-list ember-view'] | //div[@class='entity-result__item']"));
    //String resultOSearchItem = "(//ul[contains(@class,'search-results__list')]/li)";

    String resultOSearchItem = "//span[@class='entity-result__title']//a[1] | //span[contains(@class,'mn-connection-card__name')] | //span[@class='entity-result__title']//a[@data-control-name='entity_result']";
    String resultOSearchItemAlt = "//div[@class='linked-area cursor-pointer']";

    By resultsInfo = By.xpath("//div[contains(@class,'search-result__info')]");

    By nameFromResult = By.xpath(".//span[@class='name actor-name'] ");
    By positionFromResult = By.xpath("./p[1]/span[1]");

    By allConnectionsLink = By.xpath("//a[@data-control-name='topcard_view_all_connections']");
    By contactsLink = By.xpath(("//a[@data-control-name='contact_see_more']"));

    By mailFromContactDialog = By.xpath("//a[contains(@href, 'mailto:')]");
    By phoneFromContactDialog = By.xpath("//li[@class='pv-contact-info__ci-container']/span[1]");


    By closeContactDialog = By.xpath(("//button[@aria-label='Dismiss']"));

    By noResultsFoundMessage = By.xpath(("//h1[contains(.,'No results found')]"));

    public MainPage openUserConnections() {
        LOGGER.info("User Connections opening");
//        waitForElement(connectionsLink);
//        try {
//            findElement(connectionsLink).click();
//        } catch (Exception e){
//            LOGGER.warn("Trying JS");
//            clickOnElementUsingJS(findElement(connectionsLink));
//        }
//        waitForElement(connectionsSecondLink);
//        findElement(connectionsSecondLink).click();
        driver().get(connectionsURL);
        LOGGER.info("User Connections opened");
        return this;
    }

    public MainPage openConnections() {
        clickOnElementUsingJS(findElement(allConnectionsLink));
        return this;
    }


    public String getTitleAndCompanyFromContactPage() {
        LOGGER.info("Getting title and company");

        sleepFor(1000);
        try {
            resetScroll();
            scrollToExperience();
            //scrollToElement(findElement(By.xpath("//h2[contains(.,'Experience')]")));
        } catch (Exception e) {
            if(findElements(By.xpath("//section[contains(@class,'pv-top-card-v3')]//h2")).size() > 0)
                return findElement(By.xpath("//section[contains(@class,'pv-top-card-v3')]//h2")).getText();
            else
                return "Unknown";
        }

        By lastPositionTitle;
        By lastPositionCompany;

        By extendedFormatOfRecord = By.xpath("(//h2[contains(.,'Experience')]/parent::header[1]/following::ul[1]//li[1])[1]//ul['pv-entity__position-group']");

        lastPositionTitle = By.xpath("(//h2[contains(.,'Experience')]/parent::header[1]/following::ul[1]//li[1]//ul['pv-entity__position-group']//h3[1])[1]");
        lastPositionCompany = By.xpath("(//h2[contains(.,'Experience')]/parent::header[1]/following::ul[1]//li[1]//h3)[1]");
        // Experience 1st Title
        if (findElements(extendedFormatOfRecord).size() == 0) {
            lastPositionTitle = By.xpath("(//h2[contains(.,'Experience')]/parent::header[1]/following::ul[1]//h3)[1]");
            lastPositionCompany = By.xpath("(//h2[contains(.,'Experience')]/parent::header[1]/following::ul[1]//h3/following::p[2])[1]");
        }

        String position = "---";
        String company = "---";
        try {
            position = findElement(lastPositionTitle).getText();
            company = findElement(lastPositionCompany).getText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("Finished getting title and company: "+ position + TITLE_SEPARATOR + company);
        return position + TITLE_SEPARATOR + company;
    }

    private void scrollToExperience() {
        LOGGER.info("Scroll to experience");
        while (!userIsOnTheBottomOfDocument())
            if (findElements(By.xpath("//h2[contains(.,'Experience')]")).size() > 0) {
                LOGGER.info("Experience container was not found");
                break;
            } else
                scrollUsingJS(200);

    }

    public MainPage openFilters() {
        LOGGER.info("Open filters");
        waitForElement(searchWithFilters);
        findElement(searchWithFilters).click();
        waitForElement(allFiltersLink);
        findElement(allFiltersLink).click();
        LOGGER.info("Filters opened");
        return this;
    }

    public MainPage openCurrentFilter() {
        LOGGER.info("Open current filters");
        waitForElement(allFiltersLink);
        clickOnElementUsingJS(findElement(allFiltersLink));
        LOGGER.info("Filters opened");
        return this;
    }

    public MainPage setSearchForFirstLevel() {
        if (!findElement(firstConnectionsCheckbox).isSelected())
            clickOnElementUsingJS(findElement(firstConnectionsCheckbox));
        if (findElement(secondConnectionsCheckbox).isSelected())
            clickOnElementUsingJS(findElement(secondConnectionsCheckbox));
        return this;
    }

    public MainPage setSearchForSecondLevel() {
        if (findElement(firstConnectionsCheckbox).isSelected())
            clickOnElementUsingJS(findElement(firstConnectionsCheckbox));
        if (!findElement(secondConnectionsCheckbox).isSelected())
            clickOnElementUsingJS(findElement(secondConnectionsCheckbox));
        return this;
    }

    public MainPage setFilterTitle(String titleToSearch) {
        findElement(filterFieldTitleInput).sendKeys(titleToSearch);
        findElement(filterFieldTitleInput).sendKeys(Keys.TAB);
        return this;
    }

    public MainPage setContactSearch(String titleToSearch) {
        waitForPageToLoad();
        findElement(filterFieldContactInput).sendKeys(titleToSearch);
        findElement(filterFieldContactInput).sendKeys(Keys.TAB);
        return this;
    }

    public MainPage applyFilter() {
        findElement(applyFilters).click();
        waitForPageToLoad();
        return this;
    }

    public boolean isNextPageOfResultsAvailable() {
        waitForPageToLoad();
        scrollToEndOfResults();
        boolean result = false;
        LOGGER.info("Check Next link availability");
        if (findElements(nextPageLink).size() > 0) {
            LOGGER.info("Next link was found");
            result = findElement(nextPageLink).isEnabled();
        }else {
            LOGGER.info("Next link was not found");
            reactivateBrowser();
            setDriverContextToPage(driver());
            if (findElements(nextPageLink).size() > 0){
                LOGGER.info("Next link was found");
                result = findElement(nextPageLink).isEnabled();
            }

        }
        LOGGER.info("Next link was found? " + result);
        return result;
    }

    private void scrollToEndOfResults() {
        reactivateBrowser();
        while (!userIsOnTheBottomOfDocument())
            scrollUsingJS(2000);
    }

    public int getNumberOfResultsFromCurrentPage() {
        if (findElements(resultsOfSearch).size() == 0)
            sleepFor(5000);
        return findElements(resultsOfSearch).size();
    }

    public void scrollToItemInResults(int index) {
        waitForElement(resultsOfSearch);
        //scrollToElement(findElements(resultsOfSearch).get(i));
        resetScroll();
        while (!userIsOnTheBottomOfDocument())
            if (findElements(By.xpath(resultOSearchItem + "[" + (index + 1) + "]")).size() > 0) {
                scrollToElement(resultOSearchItem + "[" + (index + 1) + "]");
                break;
            } else
                scrollUsingJS(200);


    }


    public String getNameFromResults(int i) {
        String name;
        LOGGER.info("Opening contact # " + i);
        try {
            //scrollToElement(resultOSearchItem + "[" + (i+1) + "]");
            name = findElement(By.xpath(resultOSearchItem + "[" + (i + 1) + "]")).getText();
        } catch (Exception e) {
            name = "";
        };

        if(name.equals("")) {
            try {
                LOGGER.info("Scroll up..." + (i + 1));
                resetScroll();
                scrollToElement(resultOSearchItem + "[" + (i + 1) + "]");
                name = findElement(By.xpath(resultOSearchItem + "[" + (i + 1) + "]")).getText();
            } catch (Exception e) {
                name = "";
            };
        }

        if(name.equals("")) {
            try {
                LOGGER.info("Reload and Scroll up..." + (i + 1));
                reloadPage();
                scrollToElement(resultOSearchItem + "[" + (i + 1) + "]");
                name = findElement(By.xpath(resultOSearchItem + "[" + (i + 1) + "]")).getText();
            } catch (Exception e) {
                LOGGER.error("Item was not found " + (i + 1));
                takeScreenshot(driver(), Tools.getCurDateTime());
                name = "";
            };
        }

        //last attempt
        if (name.equals(""))
            name = findElement(By.xpath(resultOSearchItemAlt)).getText();

        return name; //findElements(resultsOfSearch).get(i).findElement(nameFromResult).getText();
    }

    public void clickOnNextResultLink() {
        LOGGER.info("Open next page of results");
        clickOnElementUsingJS(findElement(nextPageLink));
        waitForPageToLoad();
    }

    public void clickOnItemInResults(int i) {
        //clickOnElementUsingJS(findElements(resultsOfSearch).get(i).findElement(nameFromResult));
        clickOnElement((By.xpath(resultOSearchItem + "[" + (i + 1) + "]")));
        waitForPageToLoad();
    }

    public void openItemInResults(int i) {
        //clickOnElementUsingJS(findElements(resultsOfSearch).get(i).findElement(nameFromResult));
        reactivateBrowser();
        cntrlClickOnElement(findElement(By.xpath(resultOSearchItem + "[" + (i + 1) + "]")));
        openNextTab();
        waitForPageToLoad();
    }

    public void closeItemTab() {
        closeTab();
    }

    public boolean isConnectionsOpenForContact() {
        return findElements(allConnectionsLink).size() > 0;
    }

    public MainPage setCurrentCompanies(String listOfCompanies) {
        LOGGER.info("Set current companies");
        if(!listOfCompanies.trim().equals("")) {
            for (String companyName : listOfCompanies.trim().split(";")) {
                try {
                    findElement(filterFieldCompanyInput).sendKeys(companyName.trim());
                    sleepFor(2000);
                    findElement(By.xpath("//span[@class='search-typeahead-v2__hit-info truncate']")).click();
                } catch (Exception e) {
                    LOGGER.warn("Company was not found: " + companyName);
                    findElement(filterFieldCompanyInput).clear();
                    sleepFor(1000);
//                findElement(filterFieldCompanyInput).sendKeys(companyName.trim().substring(0, companyName.length() - 3));
//                sleepFor(1000);
//                findElement(filterFieldCompanyInput).sendKeys(companyName.trim().substring(companyName.length() - 3, companyName.length() - 2));
//                sleepFor(1000);
//                findElement(filterFieldCompanyInput).sendKeys(companyName.trim().substring(companyName.length() - 2, companyName.length() - 1));
//                sleepFor(3000);
//                findElement(By.xpath("//span[@class='search-typeahead-v2__hit-info truncate']")).click();

                }
            }
        }
        LOGGER.info("Companies selected");
        return this;
    }

    public void openContactsPanel() {
        waitForElement(contactsLink);
        clickOnElementUsingJS(findElement(contactsLink));
    }

    public String getCurrentContactEmail() {
        String result = "No email specified";
        waitForElement(mailFromContactDialog);
        try {
            result = findElement(mailFromContactDialog).getText();
        }catch (Exception e){
            LOGGER.info("No email specified");
        }
        //findElement(closeContactDialog).click();
        return result;
    }


    public String getCurrentContactPhone() {
        String result = "No phone specified";
        try {
            result = findElement(phoneFromContactDialog).getText();
        }catch (Exception e){
            LOGGER.info("No phone specified");
        }
        //findElement(closeContactDialog).click();
        return result;
    }

    public boolean wasNoResultsFound() {
        return findElements(noResultsFoundMessage).size() > 0;
    }

    public boolean wasResultsFound(){
        return findElements(By.xpath(resultOSearchItem)).size() > 0;
    }

    public String getCurrentContactDetails() {
        try {
            waitForElement(By.xpath("//h2[.='Contact Info']/following::div[1]"));
            return findElement(By.xpath(("//h2[contains(.,'Contact Info')]/following::div[1]"))).getText().replaceAll("\n", " -- ");
        }catch (Exception e) {
            return "No open contact info";
        }
    }
}

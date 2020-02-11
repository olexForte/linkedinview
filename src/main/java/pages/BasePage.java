package pages;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import utils.PropertiesList;
import utils.SessionManager;
//import utils.ReporterManager;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Base class for Page objects
 */
public class BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);
    //static public ReporterManager reporter = ReporterManager.Instance;
    
    private static int DEFAULT_TIMEOUT          = 15;
    private static int DEFAULT_SHORT_TIMEOUT    = 3000;
    private static int DEFAULT_STATIC_TIMEOUT   = 3000;

    public final static String BASE_URL = (PropertiesList.getConfigProperty("Environment"));

    public String pageURL = "";
    public String pageTitle = "";

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

    public static final int MAIN_TIMEOUT = getTimeout();
    public static final int SHORT_TIMEOUT = getShortTimeout();
    public static final int STATIC_TIMEOUT =  getStaticTimeout();

    private static int getTimeout() {
        String timeout = PropertiesList.getConfigProperty("DefaultTimeoutInSeconds");
        if (timeout == null ) {
            //reporter.fail("DefaultTimeoutInSeconds parameter was not found");
            return DEFAULT_TIMEOUT;
        };

        return Integer.parseInt(timeout);
    }

    private static int getShortTimeout() {
        String timeout = PropertiesList.getConfigProperty("ShortTimeoutInSeconds");
        if (timeout == null ) {
            return DEFAULT_SHORT_TIMEOUT;
        };

        return Integer.parseInt(timeout);
    }

    private static int getStaticTimeout() {
        String timeout = PropertiesList.getConfigProperty("StaticTimeoutMilliseconds");
        if (timeout == null ) {
            return DEFAULT_STATIC_TIMEOUT;
        };
        return Integer.parseInt(timeout);
    }

    public BasePage() {
        // waitForPageToLoad();
    }

    public static WebDriver driver(){
        return driver.get();
    }

    public boolean isPageLoaded() {
        boolean result = false;
        //reporter.info("Page title is: " + driver().getTitle());
        //reporter.info("Page URL is: " + driver().getCurrentUrl());
        if (driver().getTitle().contains(pageTitle))
            result = true;
        else {
            //reporter.info("Expected title: " + pageTitle);
            result = false;
        }

        if (driver().getCurrentUrl().contains(pageURL))
            result = true;
        else {
            //reporter.info("Expected URL: " + pageURL);
            result = false;
        }

        return result;
    }

    public void reloadPage() {
        driver().navigate().refresh();
    }

    public void open() {

        //reporter.info("Opening the page: " + "\"" + BASE_URL + pageURL + "\"");
        driver().get(BASE_URL + pageURL);
        //driver().manage().window().maximize();
    }

    public void close() {
        //reporter.info("Closing the browser");
        driver().close();
    }

    public void closeTab() {
        //reporter.info("Closing the browser");
        driver().close();
        Object[] handles = driver().getWindowHandles().toArray();
        driver().switchTo().window( String.valueOf(handles[0]) );
    }

    public String getTitle() {
        //reporter.info("The page title is: " + "\"" + pageTitle + "\"");
        return pageTitle;
    }

    public String getURL() {
        //reporter.info("The requested URL is: " + BASE_URL + pageURL);
        return BASE_URL + pageURL;
    }

    protected void sendText(String cssSelector, String text) {
        findElement(By.cssSelector(cssSelector)).sendKeys(text);
    }

    public void setText(By element, String value){
        if (value != null) {
            findElement(element).clear();
            findElement(element).sendKeys(value);
        }
    }

    public boolean isTextPresent(String text) {
        return driver().getPageSource().contains(text);
    }

    public boolean isTextPresentInElement(String text) {
        return driver().findElements(By.xpath("//div[.='" + text + "']")).size() > 0;
    }

    public boolean isElementPresent(By by) {
        try {
            WebElement element = driver().findElements(by).get(0);
            return element.isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isElementPresent(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isElementPresent(String _cssSelector) {
        try {
            findElementIgnoreException(By.cssSelector(_cssSelector));
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isElementPresentAndDisplay(By by) {
        try {
            return findElementIgnoreException(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementDisplayedRightNow(By by) {
        try {
            boolean result = driver().findElements(by).size() > 0;
            LOGGER.debug("Elements were found " + by.toString());
            return result;
        } catch (Exception e) {
            LOGGER.debug("Elements were NOT found " + by.toString());
            return false;
        }
    }


    public void selectFromDropdown(By element, String value){
        Select dropdown = new Select(findElement(element));
        dropdown.selectByVisibleText(value);
    }


    public void clickOnElementIgnoreException(By element, int... timeout) {
        waitForPageToLoad();
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        try {
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            driver().findElement(element).click();
        } catch (Exception e) {
            // nothing
        }
        waitForPageToLoad();
    }

    public WebElement findElementIgnoreException(By element, int... timeout) {
        //waitForPageToLoad();
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            LOGGER.debug("findElementIgnoreException " + element.toString());
            //synchronize();
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            return driver().findElement(element);
        } catch (Exception e) {
            LOGGER.debug("EfindElementIgnoreException element NOT found " + element.toString());
            return null;
        }
    }

    public List<WebElement> findElementsIgnoreException(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        //waitForPageToLoad();
        try {
            //synchronize();
//            (new WebDriverWait(driver(), timeoutForFindElement))
//                    .until(ExpectedConditions.presenceOfElementLocated(element));
            return driver().findElements(element);
        } catch (Exception e) {
            ////reporter.info("Got exception. Exception is expected and ignored.");
            return new ArrayList<WebElement>();
        }
    }

    public void clickOnElement(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        //waitForPageToLoad();
//        try {
//            (new WebDriverWait(driver(), timeoutForFindElement))
//                    .until(ExpectedConditions.presenceOfElementLocated(element)).click();
//        } catch (Exception e) {
//            //reporter.failWithScreenshot(ReporterManager.getStackTrace(e));
//            throw new RuntimeException("Failure clicking on element: " + e.getMessage() );
//        }


        for (int i = 0 ; i < MAIN_TIMEOUT; i++ ) {
           try{
               driver().findElement(element).click();
               LOGGER.debug("clickOnElement " + element.toString());
               return;
        } catch (Exception e){
            if(i == MAIN_TIMEOUT) {
                LOGGER.debug("clickOnElement FAILED: " + element.toString());
                //reporter.failWithScreenshot(ReporterManager.getStackTrace(e));
                throw new RuntimeException("Failure clicking on element: " + e.getMessage() );
            }
            sleepFor(1000);
        }

    }

        waitForPageToLoad();
    }

    public WebElement findElement(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        //waitForPageToLoad();

        try {
            //synchronize();
            LOGGER.debug("findElement: " + element.toString());
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            return driver().findElement(element);
        } catch (Exception e) {
            LOGGER.debug("findElement FAILED: " + element.toString());
            reactivateBrowser();
        }

        try {
            //synchronize();
//            (new WebDriverWait(driver(), timeoutForFindElement))
//                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            return driver().findElement(element);
        } catch (Exception e) {
            //reporter.failWithScreenshot(ReporterManager.getStackTrace(e));
            LOGGER.debug("findElement FAILED AGAIN: " + element.toString());
            throw new RuntimeException("Failure finding element: " + element.toString());
        }
    }


    public void cntrlClickOnElement(WebElement element) {
        LOGGER.debug("cntrlClickOnElement: " + element.toString());
        Actions actions = new Actions(driver());
        Keys keyToHold = (System.getProperty("os.name").toLowerCase().contains("win")) ?  Keys.LEFT_CONTROL : Keys.COMMAND;
        actions.keyDown(keyToHold)
                .click(element)
                .keyUp(keyToHold)
                .build()
                .perform();
        LOGGER.debug("cntrlClickOnElement Done: " + element.toString());
    }

    public WebElement findElementPresent(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        //waitForPageToLoad();
        try {
            //synchronize();
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.presenceOfElementLocated(element));
            return driver().findElement(element);
        } catch (Exception e) {
            //reporter.failWithScreenshot(ReporterManager.getStackTrace(e));
            throw new RuntimeException("Failure finding element " + element.toString());
        }
    }

    public List<WebElement> findElements(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        //waitForPageToLoad();
        try {
            //synchronize();
//            (new WebDriverWait(driver(), timeoutForFindElement))
//                    .until(ExpectedConditions.presenceOfElementLocated(element));
            LOGGER.debug("findElements: " + element.toString());
            return driver().findElements(element);
        } catch (Exception e) {
            //reporter.failWithScreenshot(ReporterManager.getStackTrace(e));
            LOGGER.debug("findElements FAILED: " + element.toString());
            throw new RuntimeException("Failure finding elements " + element.toString());
        }
    }

    public String getAttributeIDIgnoreExecption(By element, int... timeout) {
        waitForPageToLoad();
        try {
            return getAttributeID(element, timeout[0]);
        } catch (RuntimeException e) {
            //reporter.info("Got exception. Exception is expected and ignored.");
        }
        return null;
    }

    public String getAttributeID(By element, int... timeout) {
        int timeoutForFindElement = timeout.length < 1 ? MAIN_TIMEOUT : timeout[0];
        waitForPageToLoad();
        try {
            //synchronize();
            LOGGER.debug("getAttributeID: " + element.toString());
            (new WebDriverWait(driver(), timeoutForFindElement))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));
            String id = findElement(element).getAttribute("id");
            return id;
        } catch (Exception e) {
            LOGGER.debug("getAttributeID FAILED: " + element.toString());
            throw new RuntimeException("Failure getting attribute id of an element");
        }
    }

    public void setDriverContextToPage(WebDriver driver) {
        //reporter.info("Setting the context mode to Page");
        driver.switchTo().defaultContent();
    }

    public void scrollToElement(WebElement element) {
        waitForPageToLoad();
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView();", element);
    }

    public void scrollToShopElement(WebElement element){
        waitForPageToLoad();
        ((JavascriptExecutor) driver()).executeScript("arguments[0].focus(); window.scroll(0, window.scrollY+=200)",element);
    }

    public void scrollUsingJS(int y){
        waitForPageToLoad();
        LOGGER.info(String.valueOf(
                ((JavascriptExecutor) driver()).executeScript("return  window.scrollY")));
        ((JavascriptExecutor) driver()).executeScript("window.scroll(0, window.scrollY+=" + y + ")");
    }

    public void resetScroll(){
        LOGGER.info("Reset scroll");
        waitForPageToLoad();
        ((JavascriptExecutor) driver()).executeScript("window.scrollY=0");
    }

    public boolean userIsOnTheBottomOfDocument() {
        String script = "var d = document.documentElement;\n" +
                "        var offset = d.scrollTop + window.innerHeight+100;\n" +
                "        var height = d.offsetHeight;\n" +
                "\n" +
                "        console.log('offset = ' + offset);\n" +
                "        console.log('height = ' + height);\n" +
                "\n" +
                "        return offset > height";
        return (Boolean)((JavascriptExecutor) driver()).executeScript(script);


    }

    /**
     * wait until page is completely downloaded
     */
    public void waitForPageToLoad() {
        sleepFor(STATIC_TIMEOUT); // todo fixme
        ExpectedCondition<Boolean> expectationReadyState = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver()).executeScript("return document.readyState")
                        .equals("complete");
            }
        };

        Wait<WebDriver> waitReadyState = new WebDriverWait(driver(), MAIN_TIMEOUT);

        try {
           // if(!waitReadyState.until(expectationReadyState))
                //reporter.info("JavaScript readyState query timeout - The page has not finished loading");
        } catch (Exception error) {
            ////reporter.failWithScreenshot("JavaScript readyState query timeout - The page has not finished loading");
            //reporter.info("The page has not finished loading: " + error.getMessage());
        }

    }


    /**
     * wait until page is completely downloaded and spinner is disappeared
     */
    public void waitForPageToLoadAndSpinnerToDisappear(){
        //sleepFor(2000);
        sleepFor(DEFAULT_SHORT_TIMEOUT);
        ExpectedCondition<Boolean> expectationReadyState = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver()).executeScript("return document.readyState")
                        .equals("complete");
            }
        };

        ExpectedCondition<Boolean> expectationSpinnerDisappearance = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return (Boolean)((JavascriptExecutor) driver()).executeScript(
                        "return document.getElementsByClassName('spinner').length == 0");
            }
        };

        ExpectedCondition<Boolean> expectationBigSpinnerDisappearance = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                boolean result = (Boolean)((JavascriptExecutor) driver()).executeScript(
                        "return document.getElementsByClassName('spin-loader').length == 0");
                //System.out.println(result);
                return result;
            }
        };

        Wait<WebDriver> waitReadyState = new WebDriverWait(driver(), MAIN_TIMEOUT);
        Wait<WebDriver> waitSpinnerDisapearance = new WebDriverWait(driver(), MAIN_TIMEOUT);

//        try {
//            if(!waitReadyState.until(expectationReadyState))
//                //reporter.info("JavaScript readyState query timeout - The page has not finished loading");;
//            if(!waitSpinnerDisapearance.until(expectationSpinnerDisappearance))
//                //reporter.info("JavaScript Spinner waiting query timeout - The page has not finished loading");;
//            if(!waitSpinnerDisapearance.until(expectationBigSpinnerDisappearance))
//                //reporter.info("JavaScript BigSpinner waiting timeout - The page has not finished loading");;
//        } catch (Exception error) {
//            ////reporter.failWithScreenshot("JavaScript readyState query timeout - The page has not finished loading");
//            //reporter.info("The page has not finished loading: " + error.getMessage());
//        }

//        String source = driver().getPageSource();
//
//        expectation = new ExpectedCondition<Boolean>() {
//
//            public Boolean apply(WebDriver driver)
//            {
//                return ((JavascriptExecutor) driver).executeScript("return jQuery.active")
//                        .equals("0");
//            }
//
//        };
//
//        wait = new WebDriverWait(driver(), MAIN_TIMEOUT);
//
//        try
//        {
//            wait.until(expectation);
//        } catch (Exception error)
//        {
//            //reporter.failWithScreenshot("The page has not finished loading");
//        }

    }

    public boolean waitForElement(By by){
        //WebDriverWait wait = new WebDriverWait(driver(), MAIN_TIMEOUT);
        //wait.until(ExpectedConditions.presenceOfElementLocated(by));
        boolean result = false;
        for(int i = 0; i < MAIN_TIMEOUT; i++){
            if(!isElementDisplayedRightNow(by)){
                sleepFor(1000);
            } else {
                return true;
            }
        }
        return result;
    }

    public void sleepFor(int timeout){
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    void waitForAlert(WebDriver driver, int timeout) {
        int i = 0;
        while (i++ < timeout) {
            try {
                Alert alert = driver.switchTo().alert();
                break;
            } catch (NoAlertPresentException e)  // wait for second
            {
                sleepFor(1);
                continue;
            }
        }
    }


    // Does not work because of geckodriver bug - https://stackoverflow.com/questions/40360223/webdriverexception-moveto-did-not-match-a-known-command
    public void hoverItem(By element){
        //reporter.info("Put mouse pointer over element: " + element.toString());
        Actions action = new Actions(driver());
        action.moveToElement(findElement(element)).build().perform();
    }

    public void switchToFrame(By xpath) {
        //reporter.info("Switch to frame: " + xpath.toString());
        driver().switchTo().frame(findElement(xpath));
    }

    public void switchToDefaultContent(){
        //reporter.info("Switch to default content");
        driver().switchTo().defaultContent();
    }

    /**
     * scroll page to element by xPath
     * @param xPath    xPath of element
     */
    public void scrollToElement(String xPath) {
        try {
            ((JavascriptExecutor) driver()).executeScript(
                    "var element = document.evaluate(\"" + xPath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                            "element.scrollIntoView();window.scrollBy(0,-450)"
            );
        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * click on Horizontal scroller element to move scroller
     * @param locator
     * @param scrollRight
     */
    public void moveHorizontalScroller(By locator, boolean scrollRight){
        Actions act = new Actions(driver());
        WebElement element = driver().findElement(locator);
        act.click(element).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT).
                sendKeys(Keys.ARROW_RIGHT)
        .build().perform();
    }

    /**
     * get title of page
     * @return      page title
     */
    public String getPageTitle() {
        String title = findElement(By.xpath("//div[@id='mainTitle']/h1")).getText();
        String smallText = "";
        try{
            smallText = findElement(By.xpath("//div[@id='mainTitle']/h1/small")).getText();
        } catch (Exception e) {
            return title;   //if title on page without small text
        }

        return title.replace(smallText, "").trim();
    }

    /**
    * Open next tab
    */
    public void openNextTab() {
        boolean idWasFoundOnPreviousIteration = false;
        String currentWindowID = driver().getWindowHandle();
        Object[] allWindowIDS = driver().getWindowHandles().toArray();
        for(Object id : allWindowIDS){
            if(idWasFoundOnPreviousIteration){
                driver().switchTo().window((String)id);
                return;
            }
            if ((id).equals(currentWindowID)){
                idWasFoundOnPreviousIteration = true;
            }
        }
        LOGGER.error("Next tab was not open");
    }

    /**
    * check is new tab was opened
    * @return   boolean value is new tab is opened
    */
    public boolean isNewTabOpened() {
        String currentWindowID = driver().getWindowHandle();
        Object[] allWindowIDS = driver().getWindowHandles().toArray();

        int currentWindowIndex = Arrays.asList(allWindowIDS).indexOf(currentWindowID);
        try {
            Object nextTabtWindowID = allWindowIDS[currentWindowIndex + 1];
            return true;
        } catch (Exception e) {
            LOGGER.error("Next tab was not opened");
            return false;
        }
    }

    /**
     * check is WebElement has specific class
     * @param element       WebElement which has to be verified
     * @param className     class that is being sought
     * @return              boolean value
     */
    public boolean isHasCertainClass(WebElement element, String className) {
        String[] classes = element.getAttribute("class").split( " ");
        for (String elementClass : classes) {
            if (elementClass.equals(className)) {
                return true;
            }
        }

        return false;
    }

    /**
     * go on previous page
     */
    public void goBack(){
        driver().navigate().back();
        waitForPageToLoad();
    }

    /**
     *
     * @param element
     * @return
     */
    public boolean waitForElementToDisappear(By element) {
        boolean result = false;
        for(int i = 0; i < MAIN_TIMEOUT; i++){
            if(isElementDisplayedRightNow(element)){
                sleepFor(1000);
            } else {
                return true;
            }
        }
        return result;
    }

    /**
     * click on element using JS
     * @param element
     */
    public void clickOnElementUsingJS(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click()",element);
    }


    /**
     * Zoom in/Out  (NOTE: NOT FOR PARALLEL EXCUTION)
     *
     * @param zoomLevel<br>
     *   zoomLevel  -1 -2 -3 -4 -5 .. - corresponds to Zoom OUT 90, 80, 75, 67, 50 <br>
     *   zoom level  1 2 3 4 5      .. - corresponds to Zoom IN 110, 125, 150, 175, 200 <br>
     *   zoom level > 5 || < -5 - Zoom using JS
     * @throws AWTException
     */
    public void zoom(int zoomLevel) throws AWTException {
        sleepFor(2000);
        if(zoomLevel > 5 || zoomLevel < -5) {
            ((JavascriptExecutor) driver()).executeScript("document.body.style.zoom = '" + zoomLevel + "%'");

        } else {
        Robot r = new Robot();
        r.mouseMove(2000,200);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        if(zoomLevel > 0) {
            for (int i = 0; i < zoomLevel; i++) {
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    r.keyPress(KeyEvent.VK_META);
                    sleepFor(1000);
                    r.keyPress(KeyEvent.VK_ADD);
                    r.keyRelease(KeyEvent.VK_ADD);
                    sleepFor(1000);
                    r.keyRelease(KeyEvent.VK_META);
                } else {
                    r.keyPress(KeyEvent.VK_CONTROL);
                    sleepFor(1000);
                    r.keyPress(KeyEvent.VK_ADD);
                    r.keyRelease(KeyEvent.VK_ADD);
                    sleepFor(1000);
                    r.keyRelease(KeyEvent.VK_CONTROL);
                };
            };
        }else {
            for (int i = 0; i > zoomLevel; i--) {
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    r.keyPress(KeyEvent.VK_META);
                    sleepFor(1000);
                    r.keyPress(KeyEvent.VK_SUBTRACT);
                    r.keyRelease(KeyEvent.VK_SUBTRACT);
                    sleepFor(1000);
                    r.keyRelease(KeyEvent.VK_META);
                } else {
                    r.keyPress(KeyEvent.VK_CONTROL);
                    sleepFor(1000);
                    r.keyPress(KeyEvent.VK_SUBTRACT);
                    r.keyRelease(KeyEvent.VK_SUBTRACT);
                    sleepFor(1000);
                    r.keyRelease(KeyEvent.VK_CONTROL);
                };
            }
        }
        }
    }

    public void clickOnElementRightButton(By element) {
        waitForPageToLoad();
        try {
            (new WebDriverWait(driver(), MAIN_TIMEOUT))
                    .until(ExpectedConditions.visibilityOfElementLocated(element));

            Actions action= new Actions(driver());
            action.contextClick(driver().findElement(element)).build().perform();
        } catch (Exception e) { // try again in case of exception
            Actions action= new Actions(driver());
            action.contextClick(driver().findElement(element)).build().perform();
        }
        waitForPageToLoad();
    }

    public void zoomInzoomOut(String value) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver();
            js.executeScript("document.body.style.zoom='" + value + "'");
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * take screenshot
     * @param driver
     * @param name
     * @return
     */
    public static String takeScreenshot(WebDriver driver, String name){
        String filename = name + "screen.png";
        try {
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            SessionManager.addScreenshotNameToSession(filename);
            try {
                FileUtils.copyFile(file, new File(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            LOGGER.error("No screenshot");
            return "";
        }
        return filename;
    }

    public void reactivateBrowser(){
        String window = driver().getWindowHandle();
        ((JavascriptExecutor) driver()).executeScript("alert('Go back')");
        driver().switchTo().alert().accept();
        driver().switchTo().window(window);
    }
}

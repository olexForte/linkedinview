package pages;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.MainPage;

/**
 * Rene Linkedin Page
 */
public class LoginPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPage.class);
    private static LoginPage instance;
    public static LoginPage Instance = (instance != null) ? instance : new LoginPage();

    public LoginPage(){
        pageURL = "";
    }

    private By usernameLocator = By.xpath("//input[@name='session_key']");
    private By passwordLocator = By.xpath("//input[@name='session_password']");
    private By loginButtonLocator = By.xpath(("//button[contains(.,'Sign in')]"));

    By signInLink = By.xpath("//a[.='Sign in']");

    /**
     * enter username to login form
     * @param user  username
     * @return      instance of LoginPage
     */
    public LoginPage enterUsername(String user) {
        LOGGER.info("Entering username: " + user);
        waitForPageToLoad();
        findElement(usernameLocator).clear();
        findElement(usernameLocator).sendKeys(user);
        return this;
    }

    /**
     * enter password to login form
     * @param pass  username
     * @return      instance of LoginPage
     */
    public LoginPage enterPassword(String pass) {
        LOGGER.info("Entering password");
        findElement(passwordLocator).clear();
        findElement(passwordLocator).sendKeys(pass);
        return this;
    }

    /**
     * click on button Login on login form
     * @return      instance of HomePage
     */
    public MainPage submitForm() {
        LOGGER.info("Clicked the button 'LOGIN'");
        clickOnElement(loginButtonLocator);
        return MainPage.Instance;
    };

    public boolean loginPageWasOpened() {
        waitForPageToLoad();
        return findElements(usernameLocator).size() > 0;
    }

    public void clickSubmit() {
        findElement(signInLink).click();
    }
}

package pages.login;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.BasePage;
import pages.MainPage;

/**
 * Login Auth Linkedin Page
 */
public class LoginPage_CodeAuth extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPage_CodeAuth.class);
    private static LoginPage_CodeAuth instance;
    public static LoginPage_CodeAuth Instance = (instance != null) ? instance : new LoginPage_CodeAuth();

    public LoginPage_CodeAuth() {
        pageURL = "";
    }

    private By enterCodeLocator = By.xpath("//body//h1[contains(text(),'Enter the code')]");
    private By submitButtonLocator = By.xpath("//body//button[contains(@class, 'form__submit')]");
    private By resendSmsButtonLocator = By.xpath("//body//button[contains(text(), 'Resend code by SMS')]");
    private By resendCallButtonLocator = By.xpath("//body//button[contains(text(), 'Resend code by phone call')]");

    /**
     * return is page displayed
     * @return boolean
     */
    public Boolean isDisplayed() {
        LOGGER.info("Checking LoginPage_CodeAuth is displayed" );
        waitForPageToLoad();
        return findElement(enterCodeLocator).isDisplayed();
    }
}

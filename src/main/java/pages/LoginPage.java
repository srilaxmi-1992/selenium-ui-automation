package pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.SeleniumUtils;

public class LoginPage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    SeleniumUtils utils;

    private By email = By.id("userEmail");
    private By password = By.id("userPassword");
    private By login = By.id("login");
    private By invalidEmailTxt = By.xpath("//input[@id='userEmail']/following-sibling::div[@class='invalid-feedback']/div");
    private By invalidPasswordTxt = By.xpath("//input[@id='userPassword']/following-sibling::div[@class='invalid-feedback']/div");
    private By errorMsg = By.xpath("//div[@id='toast-container']//div[contains(@class, 'toast-error')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.utils  = new SeleniumUtils(driver);
        log.debug("LoginPage initialised");
    }

    @Step("Type email: {emailId}")
    public void typeEmail(String emailId) {
        log.debug("Typing email: {}", emailId);
        utils.type(email, emailId);
    }

    @Step("Type password")
    public void typePassword(String pwrd) {
        log.debug("Typing password (masked)");
        utils.type(password, pwrd);
    }

    @Step("Click Login button")
    public void clickLoginButton() {
        log.debug("Clicking Login button");
        utils.click(login);
    }

    @Step("Perform login with email: {emailId}")
    public void performLogin(String emailId, String pwrd) {
        log.info("Performing login for email: {}", emailId);
        typeEmail(emailId);
        typePassword(pwrd);
        clickLoginButton();
        log.info("Login form submitted for: {}", emailId);
    }

    @Step("Get error toast message")
    public String getErrorMessage() {
        String msg = utils.getText(errorMsg);
        log.debug("Error toast message: [{}]", msg);
        return msg;
    }

    @Step("Get email field validation message")
    public String getInvalidEmailMessage() {
        String msg = utils.getText(invalidEmailTxt);
        log.debug("Email field error: [{}]", msg);
        return msg;
    }

    @Step("Get password field validation message")
    public String getInvalidPasswordMessage() {
        String msg = utils.getText(invalidPasswordTxt);
        log.debug("Password field error: [{}]", msg);
        return msg;
    }
}

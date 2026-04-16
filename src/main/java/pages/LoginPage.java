package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

public class LoginPage {

    WebDriver driver;
    SeleniumUtils utils;

    @FindBy(id = "userEmail")
    WebElement email;

    @FindBy(id = "userPassword")
    WebElement password;

    @FindBy(id = "login")
    WebElement login;

    @FindBy(xpath = "//input[@id='userEmail']/following-sibling::div[@class='invalid-feedback']/div")
    WebElement invalidEmailTxt;

    @FindBy(xpath = "//input[@id='userPassword']/following-sibling::div[@class='invalid-feedback']/div")
    WebElement invalidPasswordTxt;


    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.utils = new SeleniumUtils(driver);
        PageFactory.initElements(driver, this);
    }

    public void typeEmail(String emailId) {
        utils.type(email, emailId);
    }

    public void typePassword(String pwrd) {
        utils.type(password, pwrd);
    }

    public void clickLoginButton() {
        utils.click(login);
    }

    @FindBy(xpath = "//div[@id='toast-container']//div[contains(@class, 'toast-error')]")
    WebElement errorMsg;

    public String getErrorMessage() {
        return utils.getText(errorMsg);
    }

    public void performLogin(String emailId, String pwrd) {
        typeEmail(emailId);
        typePassword(pwrd);
        clickLoginButton();
    }

    public String getInvalidEmailMessage() {
        return utils.getText(invalidEmailTxt);
    }

    public String getInvalidPasswordMessage() {
        return utils.getText(invalidPasswordTxt);
    }

}

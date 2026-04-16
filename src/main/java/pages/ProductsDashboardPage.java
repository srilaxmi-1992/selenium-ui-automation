package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

public class ProductsDashboardPage {

    WebDriver driver;
    SeleniumUtils utils;

    public ProductsDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.utils = new SeleniumUtils(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//div[@id='toast-container']//div[contains(@class, 'toast-success')]")
    WebElement successMsg;

    public String getSuccessMessage(){
        return utils.getText(successMsg);
    }

    public String getCurrentUrl(String url){
        return utils.getCurrentUrl(url);
    }

}

package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

import java.util.BitSet;
import java.util.List;

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

    @FindBy(xpath = "//button[normalize-space()='HOME']")
    WebElement homeBtn;

    @FindBy(css = "div[class='card']")
    List<WebElement> productsList;

    @FindBy(xpath = "//div[@role='alert' and contains(@class,'toast-message')]")
    WebElement addToCartMessage;

    @FindBy(css = "button[routerlink*='cart'] label")
    WebElement cartCount;

    @FindBy(css = "button[routerlink*='cart']")
    WebElement cartBtn;

    public String getSuccessMessage() {
        return utils.getText(successMsg);
    }

    public String getCurrentUrl(String url) {
        return utils.getCurrentUrl(url);
    }

    public boolean isDashboardLoaded() {
        return utils.isElementEnabled(homeBtn) && utils.isElementDisplayed(homeBtn);
    }

    public int getNumberOfProducts() {
        return productsList.size();
    }

    public boolean isAddToCartButtonEnabled(String productName) {
        return utils.isElementEnabled(getAddToCartButtonLocator(productName));
    }

    public void clickOnAddToCartButton(String productName) {
        utils.clickUsingActions(getAddToCartButtonLocator(productName));
    }

    private By getAddToCartButtonLocator(String productName) {
        return By.xpath("//div[@class='card']//b[text()='" + productName + "']/parent::h5/following-sibling::button[contains(@class,'w-10')]");
    }

    public String getAddToCartMessage() {
        return utils.getText(addToCartMessage);
    }

    public int getNumberOfProductsAddedToCart() {
        return Integer.parseInt(utils.getText(cartCount));
    }

    public void waitForOverlayToDisappear(){
        utils.waitForOverlayToDisappear(By.cssSelector(".ngx-spinner-overlay"));
    }

    public void clickCartButton(){
        waitForOverlayToDisappear();
        utils.waitForElement(cartBtn);
        utils.click(cartBtn);
    }

    public void addProductsToCart(List<String> productNames, String expectedMessage) {
        for (String product : productNames) {
            if (isAddToCartButtonEnabled(product)) {
                clickOnAddToCartButton(product);
                String toast = getAddToCartMessage();
                if (!toast.equals(expectedMessage)) {
                    throw new RuntimeException("Unexpected toast message: " + toast);
                }
                waitForOverlayToDisappear();
            }
        }
    }


}

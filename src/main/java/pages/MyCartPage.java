package pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MyCartPage {

    private static final Logger log = LogManager.getLogger(MyCartPage.class);

    WebDriver driver;
    SeleniumUtils utils;

    @FindBy(css = "div[class='cartSection'] h3")
    List<WebElement> productNames;

    @FindBy(css = "div[class='cartSection'] p:nth-of-type(2)")
    List<WebElement> productPrices;

    @FindBy(xpath = "//li[@class='totalRow']/span[text()='Total']/following-sibling::span[@class='value']")
    WebElement totalPrice;

    @FindBy(xpath = "//button[normalize-space()='Checkout']")
    WebElement checkoutBtn;

    @FindBy(xpath = "//button[normalize-space()='Continue Shopping']")
    WebElement continueShoppingBtn;

    By checkoutBoxLocator = By.xpath("//button[normalize-space()='Checkout']");

    public MyCartPage(WebDriver driver) {
        this.driver = driver;
        this.utils  = new SeleniumUtils(driver);
        PageFactory.initElements(driver, this);
        log.debug("MyCartPage initialised");
    }

    public void waitForContinueShoppingBtn() {
        log.debug("Waiting for Continue Shopping button to be visible");
        utils.waitForElement(continueShoppingBtn);
    }

    @Step("Click Continue Shopping")
    public void clickContinueShoppingButton() {
        log.debug("Clicking Continue Shopping button");
        waitForContinueShoppingBtn();
        utils.click(continueShoppingBtn);
        log.info("Clicked Continue Shopping — navigating back to dashboard");
    }

    @Step("Check cart is empty")
    public boolean isCartEmpty() {
        boolean empty = productNames.isEmpty();
        log.debug("Cart is empty: {}", empty);
        return empty;
    }

    @Step("Click Checkout button")
    public void clickCheckoutButton() {
        log.debug("Clicking Checkout button");
        utils.waitForElementVisible(checkoutBoxLocator);
        utils.clickUsingActions(checkoutBoxLocator);
        log.info("Clicked Checkout — navigating to Place Order page");
    }

    @Step("Get product names from cart")
    public List<String> getProductNames() {
        List<String> names = productNames.stream()
                .map(e -> e.getText().toUpperCase())
                .collect(Collectors.toList());
        log.debug("Cart product names: {}", names);
        return names;
    }

    @Step("Get product prices from cart")
    public List<String> getProductPrices() {
        List<String> prices = productPrices.stream()
                .map(e -> e.getText()
                        .replaceAll("MRP", "")
                        .replaceAll(" ", ""))
                .collect(Collectors.toList());
        log.debug("Cart product prices: {}", prices);
        return prices;
    }

    @Step("Get cart total")
    public String getTotal() {
        String total = utils.getText(totalPrice)
                .replaceAll("MRP", "")
                .replaceAll(" ", "");
        log.debug("Cart total: {}", total);
        return total;
    }
}

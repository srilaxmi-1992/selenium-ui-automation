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

public class OrderPage {

    private static final Logger log = LogManager.getLogger(OrderPage.class);

    WebDriver driver;
    SeleniumUtils utils;

    @FindBy(css = "h1.hero-primary")
    WebElement headingElement;

    @FindBy(css = "table.order-summary td.line-item.m-3 div.title")
    List<WebElement> productNames;

    @FindBy(css = "table.order-summary td.line-item[style*='right'] div.title")
    List<WebElement> productPrices;

    private By heading = By.cssSelector("h1.hero-primary");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.utils  = new SeleniumUtils(driver);
        PageFactory.initElements(driver, this);
        log.debug("OrderPage initialised");
    }

    @Step("Wait for order confirmation heading")
    public void waitForHeading() {
        log.debug("Waiting for order confirmation heading to appear");
        utils.waitForElementVisible(heading);
        log.debug("Order confirmation heading is visible");
    }

    @Step("Get order confirmation heading text")
    public String getHeadingText() {
        waitForHeading();
        String text = utils.getText(headingElement);
        log.debug("Order confirmation heading: [{}]", text);
        return text;
    }

    @Step("Get confirmed product names")
    public List<String> getProductNames() {
        waitForHeading();
        List<String> names = productNames.stream()
                .map(e -> e.getText().trim().toUpperCase())
                .collect(Collectors.toList());
        log.debug("Order confirmation product names: {}", names);
        return names;
    }

    @Step("Get confirmed product prices")
    public List<String> getProductPrices() {
        waitForHeading();
        List<String> prices = productPrices.stream()
                .map(e -> e.getText()
                        .replaceAll(".*MRP", "")
                        .replaceAll("\\s+", ""))
                .collect(Collectors.toList());
        log.debug("Order confirmation product prices: {}", prices);
        return prices;
    }

    @Step("Get order confirmation page URL")
    public String getPageUrl() {
        String url = driver.getCurrentUrl();
        log.debug("Order confirmation URL: {}", url);
        return url;
    }
}

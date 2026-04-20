package pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.SeleniumUtils;

import java.util.List;
import java.util.stream.Collectors;

public class OrderPage {

    private static final Logger log = LogManager.getLogger(OrderPage.class);

    WebDriver driver;
    SeleniumUtils utils;

    private By headingElement = By.cssSelector("h1.hero-primary");
    private By productNames = By.cssSelector("table.order-summary td.line-item.m-3 div.title");
    private By productPrices = By.cssSelector("table.order-summary td.line-item[style*='right'] div.title");
    private By heading = By.cssSelector("h1.hero-primary");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.utils  = new SeleniumUtils(driver);
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
        List<String> names = utils.getElementsText(productNames).stream()
                .map(text -> text.toUpperCase())
                .collect(Collectors.toList());
        log.debug("Order confirmation product names: {}", names);
        return names;
    }

    @Step("Get confirmed product prices")
    public List<String> getProductPrices() {
        waitForHeading();
        List<String> prices = utils.getElementsText(productPrices).stream()
                .map(text -> text
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

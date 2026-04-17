package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

import java.util.List;
import java.util.stream.Collectors;

public class OrderPage {
    WebDriver driver;
    SeleniumUtils utils;

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.utils = new SeleniumUtils(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "h1.hero-primary")
    WebElement headingElement;

    @FindBy(css = "table.order-summary td.line-item.m-3 div.title")
    List<WebElement> productNames;

    @FindBy(css = "table.order-summary td.line-item[style*='right'] div.title")
    List<WebElement> productPrices;

    @FindBy(xpath = "//button[normalize-space()='Click To Download Order Details in CSV']")
    WebElement downloadBtn;

    By heading = By.cssSelector("h1.hero-primary");

    public void waitForHeading() {
        utils.waitForElementVisible(heading);
    }

    public List<String> getProductNames() {
        waitForHeading();
        return productNames.stream()
                .map(e -> e.getText().trim().toUpperCase())
                .collect(Collectors.toList());
    }

    public List<String> getProductPrices() {
        waitForHeading();

        return productPrices.stream()
                .map(e -> e.getText())
                .map(text -> text.replaceAll(".*MRP", "")   // remove everything before MRP
                        .replaceAll("\\s+", ""))  // remove all whitespace
                .collect(Collectors.toList());
    }

    public String getHeadingText() {
        waitForHeading();
        return utils.getText(headingElement);
    }

    public String getPageUrl() {
        return driver.getCurrentUrl();
    }
}

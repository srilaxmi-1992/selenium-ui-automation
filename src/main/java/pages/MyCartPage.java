package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MyCartPage {

    WebDriver driver;
    SeleniumUtils utils;

    public MyCartPage(WebDriver driver) {
        this.driver = driver;
        this.utils = new SeleniumUtils(driver);
        PageFactory.initElements(driver, this);
    }

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

    public void waitForContinueShoppingBtn() {
        utils.waitForElement(continueShoppingBtn);
    }

    public void clickCheckoutButton() {
        utils.waitForElementVisible(checkoutBoxLocator);
        utils.clickUsingActions(checkoutBoxLocator);
    }

    public List<String> getProductNames() {
        List<String> names = productNames.stream().map(element -> element.getText().toUpperCase())
                .collect(Collectors.toList());
        return names;
    }

    public List<String> getProductPrices() {
        return productPrices.stream()
                .map(e -> e.getText())
                .map(text -> text.replaceAll("MRP", "")   // remove everything before MRP
                        .replaceAll(" ", ""))  // remove all whitespace
                .collect(Collectors.toList());
    }

    public String getTotal() {
        return utils.getText(totalPrice).replaceAll("MRP", "").replaceAll(" ", "");
    }
}

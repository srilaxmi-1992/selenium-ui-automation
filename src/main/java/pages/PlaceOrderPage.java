package pages;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.SeleniumUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceOrderPage {

    private static final Logger log = LogManager.getLogger(PlaceOrderPage.class);

    WebDriver driver;
    SeleniumUtils utils;

    private By productNames = By.cssSelector("div.item__details div.item__title");
    private By productPrices = By.cssSelector("div.item__details div.item__price");
    private By cvvTxtBox = By.xpath("//div[normalize-space()='CVV Code ?']/following-sibling::input");
    private By nameTxtBox = By.xpath("//div[normalize-space()='Name on Card']/following-sibling::input");
    private By emailIdTxtBox = By.xpath("(//div[contains(@class,'user__name')]//input)[1]");
    private By countrySuggestBox = By.cssSelector("input[placeholder='Select Country']");
    private By placeOrderBtn = By.xpath("//a[normalize-space()='Place Order']");

    public PlaceOrderPage(WebDriver driver) {
        this.driver = driver;
        this.utils  = new SeleniumUtils(driver);
        log.debug("PlaceOrderPage initialised");
    }

    @Step("Enter CVV: {cvv}")
    public void typeInCVV(String cvv) {
        log.debug("Entering CVV");
        utils.waitForElement(cvvTxtBox);
        utils.type(cvvTxtBox, cvv);
    }

    @Step("Enter Name on Card: {name}")
    public void typeInName(String name) {
        log.debug("Entering name on card: {}", name);
        utils.waitForElement(nameTxtBox);
        utils.type(nameTxtBox, name);
    }

    @Step("Enter email: {email}")
    public void typeInUserMail(String email) {
        log.debug("Entering email: {}", email);
        utils.waitForElement(emailIdTxtBox);
        utils.type(emailIdTxtBox, email);
    }

    @Step("Select country: {country}")
    public void selectCountry(String country) {
        log.debug("Selecting country: {}", country);
        utils.waitForElement(countrySuggestBox);
        utils.type(countrySuggestBox, "Ind");
        By locator = By.xpath(
                "//section[contains(@class,'list-group')]//button[normalize-space()='" + country + "']");
        utils.clickUsingActions(locator);
        log.debug("Country selected: {}", country);
    }

    @Step("Click Place Order button")
    public void clickPlaceOrder() {
        log.debug("Clicking Place Order button");
        utils.waitForElement(placeOrderBtn);
        utils.click(placeOrderBtn);
        log.info("Place Order clicked — waiting for confirmation");
    }

    @Step("Fill checkout form and place order")
    public void placeOrder(JsonNode order) {
        String name    = order.get("name").asText();
        String cvv     = order.get("cvv").asText();
        String email   = order.get("email").asText();
        String country = order.get("country").asText();

        log.info("Placing order — name: {}, email: {}, country: {}", name, email, country);
        typeInCVV(cvv);
        typeInName(name);
        typeInUserMail(email);
        selectCountry(country);
        clickPlaceOrder();
        log.info("Order placement form submitted");
    }

    @Step("Get product names on checkout")
    public List<String> getProductNames() {
        List<String> names = utils.getElementsText(productNames).stream()
                .map(text -> text.toUpperCase())
                .collect(Collectors.toList());
        log.debug("Checkout product names: {}", names);
        return names;
    }

    @Step("Get product prices on checkout")
    public List<String> getProductPrices() {
        List<String> prices = utils.getElementsText(productPrices).stream()
                .map(text -> text
                        .replaceAll(".*MRP", "")
                        .replaceAll("\\s+", ""))
                .collect(Collectors.toList());
        log.debug("Checkout product prices: {}", prices);
        return prices;
    }

    @Step("Check all checkout form fields are displayed")
    public boolean areAllFormFieldsDisplayed() {
        boolean result = utils.isElementDisplayed(cvvTxtBox)
                && utils.isElementDisplayed(nameTxtBox)
                && utils.isElementDisplayed(emailIdTxtBox)
                && utils.isElementDisplayed(countrySuggestBox);
        log.debug("All checkout form fields displayed: {}", result);
        return result;
    }

    public int getProductCount() {
        int count = utils.getElementsSize(productNames);
        log.debug("Product count on checkout page: {}", count);
        return count;
    }
}

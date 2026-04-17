package pages;

import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceOrderPage {

    WebDriver driver;
    SeleniumUtils utils;

    public PlaceOrderPage(WebDriver driver) {
        this.driver = driver;
        this.utils = new SeleniumUtils(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "div.item__details div.item__title")
    List<WebElement> productNames;

    @FindBy(css = "div.item__details div.item__price")
    List<WebElement> productPrices;

    @FindBy(xpath = "//div[normalize-space()='CVV Code ?']/following-sibling::input")
    WebElement cvvTxtBox;

    @FindBy(xpath = "//div[normalize-space()='Name on Card']/following-sibling::input")
    WebElement nameTxtBox;

    @FindBy(xpath = "(//div[contains(@class,'user__name')]//input)[1]")
    WebElement emailIdTxtBox;

    @FindBy(css = "input[placeholder='Select Country']")
    WebElement countrySuggestBox;

    @FindBy(xpath = "//a[normalize-space()='Place Order']")
    WebElement placeOrderBtn;

    public void clickPlaceOrder(){
        utils.waitForElement(placeOrderBtn);
        utils.click(placeOrderBtn);
    }


    public void typeInCVV(String cvv) {
        utils.waitForElement(cvvTxtBox);
        utils.type(cvvTxtBox, cvv);
    }

    public void typeInName(String name) {
        utils.waitForElement(nameTxtBox);
        utils.type(nameTxtBox, name);
    }

    public void typeInUserMail(String email) {
        utils.waitForElement(emailIdTxtBox);
        utils.type(emailIdTxtBox, email);
    }

    public void selectCountry(String country){
        utils.waitForElement(countrySuggestBox);
        utils.type(countrySuggestBox, "Ind");
        By locator = By.xpath("//section[contains(@class,'list-group')]//button[normalize-space()='"+country+"']");
        utils.clickUsingActions(locator);
    }
    public List<String> getProductNames() {

        return productNames.stream()
                .map(e -> e.getText().trim().toUpperCase())
                .collect(Collectors.toList());
    }

    public List<String> getProductPrices() {

        return productPrices.stream()
                .map(e -> e.getText())
                .map(text -> text.replaceAll(".*MRP", "")   // remove everything before MRP
                        .replaceAll("\\s+", ""))  // remove all whitespace
                .collect(Collectors.toList());
    }

    public boolean areAllFormFieldsDisplayed() {
        return utils.isElementDisplayed(cvvTxtBox)
                && utils.isElementDisplayed(nameTxtBox)
                && utils.isElementDisplayed(emailIdTxtBox)
                && utils.isElementDisplayed(countrySuggestBox);
    }

    public int getProductCount() {
        return productNames.size();
    }

    public void placeOrder(JsonNode order){

        typeInCVV(order.get("cvv").asText());
        typeInName(order.get("name").asText());
        typeInUserMail(order.get("email").asText());
        selectCountry(order.get("country").asText());
        clickPlaceOrder();

    }
}

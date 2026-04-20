package pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.SeleniumUtils;

import java.util.List;

public class ProductsDashboardPage {

    private static final Logger log = LogManager.getLogger(ProductsDashboardPage.class);

    WebDriver driver;
    SeleniumUtils utils;

    private By successMsg = By.xpath("//div[@id='toast-container']//div[contains(@class, 'toast-success')]");
    private By homeBtn = By.xpath("//button[normalize-space()='HOME']");
    private By addToCartMessage = By.xpath("//div[@role='alert' and contains(@class,'toast-message')]");
    private By cartCount = By.cssSelector("button[routerlink*='cart'] label");
    private By cartBtn = By.cssSelector("button[routerlink*='cart']");
    private By productsList = By.cssSelector("div[class='card']");
    private By productNameElements = By.xpath("//div[@class='card']//b");

    public ProductsDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.utils = new SeleniumUtils(driver);
        log.debug("ProductsDashboardPage initialised");
    }

    @Step("Get success toast message")
    public String getSuccessMessage() {
        String msg = utils.getText(successMsg);
        log.debug("Success toast: [{}]", msg);
        return msg;
    }

    public String getCurrentUrl(String expectedFragment) {
        String url = utils.getCurrentUrl(expectedFragment);
        log.debug("Current URL (contains '{}'): {}", expectedFragment, url);
        return url;
    }

    @Step("Check dashboard is loaded")
    public boolean isDashboardLoaded() {
        boolean loaded = utils.isElementEnabled(homeBtn) && utils.isElementDisplayed(homeBtn);
        log.debug("Dashboard loaded: {}", loaded);
        return loaded;
    }

    public int getNumberOfProducts() {
        int count = utils.getElementsSize(productsList);
        log.debug("Number of products on dashboard: {}", count);
        return count;
    }

    public List<String> getProductNames() {
        List<String> names = utils.getElementsText(productNameElements);
        log.debug("Product names on dashboard: {}", names);
        return names;
    }

    public boolean isAddToCartButtonEnabled(String productName) {
        boolean enabled = utils.isElementEnabled(getAddToCartButtonLocator(productName));
        log.debug("Add to Cart button enabled for [{}]: {}", productName, enabled);
        return enabled;
    }

    @Step("Click Add to Cart for: {productName}")
    public void clickOnAddToCartButton(String productName) {
        log.debug("Clicking Add to Cart for: {}", productName);
        utils.clickUsingActions(getAddToCartButtonLocator(productName));
    }

    private By getAddToCartButtonLocator(String productName) {
        return By.xpath("//div[@class='card']//b[text()='" + productName
                + "']/parent::h5/following-sibling::button[contains(@class,'w-10')]");
    }

    @Step("Get Add to Cart toast message")
    public String getAddToCartMessage() {
        String msg = utils.getText(addToCartMessage);
        log.debug("Add to Cart toast: [{}]", msg);
        return msg;
    }

    public int getNumberOfProductsAddedToCart() {
        int count = Integer.parseInt(utils.getText(cartCount));
        log.debug("Cart badge count: {}", count);
        return count;
    }

    public void waitForOverlayToDisappear() {
        log.debug("Waiting for spinner overlay to disappear");
        utils.waitForOverlayToDisappear(By.cssSelector(".ngx-spinner-overlay"));
    }

    @Step("Click Cart button")
    public void clickCartButton() {
        log.debug("Clicking cart button");
        waitForOverlayToDisappear();
        utils.waitForElement(cartBtn);
        utils.click(cartBtn);
        log.info("Navigated to cart");
    }

    @Step("Add products to cart: {productNames}")
    public void addProductsToCart(List<String> productNames, String expectedMessage) {
        log.info("Adding {} product(s) to cart: {}", productNames.size(), productNames);
        for (String product : productNames) {
            if (isAddToCartButtonEnabled(product)) {
                clickOnAddToCartButton(product);
                String toast = getAddToCartMessage();
                log.debug("Toast after adding [{}]: [{}]", product, toast);
                if (!toast.equals(expectedMessage)) {
                    throw new RuntimeException("Unexpected toast for product [" + product + "]: " + toast);
                }
                waitForOverlayToDisappear();
            } else {
                log.warn("Add to Cart button not enabled for product: {}", product);
            }
        }
        log.info("All products added to cart successfully");
    }
}

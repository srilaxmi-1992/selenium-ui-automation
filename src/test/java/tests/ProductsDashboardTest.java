package tests;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;

@Feature("Products Dashboard")
public class ProductsDashboardTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(ProductsDashboardTest.class);

    @Test(description = "Validate products are displayed on dashboard", groups = {"requiresLogin"})
    @Story("Product listing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that at least one product card is visible after login")
    public void validateProductsAreDisplayed() {
        log.info("Thread [{}] -validateProductsAreDisplayed started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        int count = dashboard.getNumberOfProducts();
        log.debug("Products found on dashboard: {}", count);

        Assert.assertTrue(count > 0, "No products found on dashboard");
        log.info("Thread [{}] -validateProductsAreDisplayed PASSED, count={}", Thread.currentThread().getId(), count);
    }

    @Test(description = "Validate user can add multiple products to cart", groups = {"requiresLogin"})
    @Story("Add to cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adds multiple products from JSON test data and verifies cart count matches")
    public void validateAddToCart() {
        log.info("Thread [{}] -validateAddToCart started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        JsonNode data         = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_001");
        List<String> products = JSONReader.extractProductNames(data);
        String expectedToast  = data.get("expectedMessage").asText();

        log.debug("Products to add: {}", products);
        dashboard.addProductsToCart(products, expectedToast);

        int count = dashboard.getNumberOfProductsAddedToCart();
        log.debug("Cart count after adding: {}, expected: {}", count, products.size());

        Assert.assertEquals(count, products.size(),
                "Cart count does not match number of products added");
        log.info("Thread [{}] -validateAddToCart PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate every product card has a non-empty name", groups = {"requiresLogin"})
    @Story("Product listing")
    @Severity(SeverityLevel.MINOR)
    @Description("Checks that no product card has a blank or empty title")
    public void validateProductNamesAreNotEmpty() {
        log.info("Thread [{}] -validateProductNamesAreNotEmpty started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        List<String> names = dashboard.getProductNames();
        log.debug("Product names retrieved: {}", names);

        Assert.assertFalse(names.isEmpty(), "Product name list is empty");
        names.forEach(name -> {
            log.debug("Checking product name is not blank: [{}]", name);
            Assert.assertFalse(name.isBlank(), "Found a product card with a blank name");
        });
        log.info("Thread [{}] -validateProductNamesAreNotEmpty PASSED, {} names checked",
                Thread.currentThread().getId(), names.size());
    }

    @Test(description = "Validate Add to Cart button is enabled for each product", groups = {"requiresLogin"})
    @Story("Add to cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Iterates every product on the dashboard and confirms its Add to Cart button is enabled")
    public void validateAddToCartButtonEnabledForAllProducts() {
        log.info("Thread [{}] -validateAddToCartButtonEnabledForAllProducts started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        List<String> names = dashboard.getProductNames();
        log.debug("Checking Add to Cart button for products: {}", names);

        Assert.assertFalse(names.isEmpty(), "No products found to check");
        names.stream()
                .map(name -> name.equals("IPHONE 13 PRO") ? name.toLowerCase() : name)
                .forEach(name -> {
                    boolean enabled = dashboard.isAddToCartButtonEnabled(name);
                    log.debug("Add to Cart button enabled for [{}]: {}", name, enabled);
                    Assert.assertTrue(enabled, "Add to Cart button disabled for product: " + name);
                });
        log.info("Thread [{}] -validateAddToCartButtonEnabledForAllProducts PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate toast message when a single product is added to cart", groups = {"requiresLogin"})
    @Story("Add to cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clicks Add to Cart for one product and verifies the success toast text matches expected")
    public void validateAddToCartToastMessage() {
        log.info("Thread [{}] -validateAddToCartToastMessage started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_003");
        String productName   = JSONReader.extractProductNames(data).get(0);
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Adding product [{}], expecting toast: [{}]", productName, expectedToast);
        dashboard.clickOnAddToCartButton(productName);
        String actualToast = dashboard.getAddToCartMessage();
        log.debug("Toast received: [{}]", actualToast);

        Assert.assertEquals(actualToast, expectedToast,
                "Toast message mismatch after adding product to cart");
        log.info("Thread [{}] -validateAddToCartToastMessage PASSED", Thread.currentThread().getId());
    }
}

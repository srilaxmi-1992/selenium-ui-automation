package tests;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.MyCartPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;

@Feature("Cart")
public class MyCartTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(MyCartTest.class);

    @Test(description = "Validate product names, prices and total in cart", groups = {"requiresLogin"})
    @Story("Cart contents validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adds products, opens cart, and soft-asserts names, prices and total all match test data")
    public void validateProductsDisplayedInMyCart() {
        log.info("Thread [{}] -validateProductsDisplayedInMyCart started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        List<String> prices  = JSONReader.extractProductPrices(data);
        String expectedTotal = data.get("total").asText();
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Products to add: {}, expected total: {}", names, expectedTotal);
        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(getDriver());
        cart.waitForContinueShoppingBtn();

        List<String> actualNames  = cart.getProductNames();
        List<String> actualPrices = cart.getProductPrices();
        String actualTotal        = cart.getTotal();

        log.debug("Cart names  — expected: {}, actual: {}", names.stream().map(String::toUpperCase).toList(), actualNames);
        log.debug("Cart prices — expected: {}, actual: {}", prices, actualPrices);
        log.debug("Cart total  — expected: {}, actual: {}", expectedTotal, actualTotal);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(actualNames, names.stream().map(String::toUpperCase).toList(),
                "Product names mismatch in cart");
        soft.assertEquals(actualPrices, prices, "Product prices mismatch in cart");
        soft.assertEquals(actualTotal, expectedTotal, "Cart total mismatch");
        soft.assertAll();

        log.info("Thread [{}] -validateProductsDisplayedInMyCart PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate Continue Shopping navigates back to products dashboard", groups = {"requiresLogin"})
    @Story("Cart navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Adds a product, opens cart, clicks Continue Shopping, and verifies dashboard reloads")
    public void validateContinueShoppingNavigation() {
        log.info("Thread [{}] -validateContinueShoppingNavigation started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_003");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Adding product [{}] then navigating to cart", names);
        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(getDriver());
        cart.waitForContinueShoppingBtn();
        log.debug("Clicking Continue Shopping button");
        cart.clickContinueShoppingButton();

        boolean loaded = dashboard.isDashboardLoaded();
        log.debug("Dashboard loaded after Continue Shopping: {}", loaded);

        Assert.assertTrue(loaded, "Clicking Continue Shopping did not return to the products dashboard");
        log.info("Thread [{}] -validateContinueShoppingNavigation PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate cart is empty when no products are added", groups = {"requiresLogin"})
    @Story("Empty cart state")
    @Severity(SeverityLevel.NORMAL)
    @Description("Opens cart without adding any product and verifies the cart contains no items")
    public void validateEmptyCartHasNoProducts() {
        log.info("Thread [{}] -validateEmptyCartHasNoProducts started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        log.debug("Navigating to cart with no products added");
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(getDriver());
        cart.waitForContinueShoppingBtn();

        boolean empty = cart.isCartEmpty();
        log.debug("Cart is empty: {}", empty);

        Assert.assertTrue(empty, "Cart should contain no products when nothing was added");
        log.info("Thread [{}] -validateEmptyCartHasNoProducts PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate cart total equals sum of individual product prices", groups = {"requiresLogin"})
    @Story("Cart total calculation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Independently computes the expected total from JSON prices and verifies the cart UI shows the correct sum")
    public void validateCartTotalMatchesSumOfPrices() {
        log.info("Thread [{}] -validateCartTotalMatchesSumOfPrices started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        List<String> prices  = JSONReader.extractProductPrices(data);
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Products: {}, prices from JSON: {}", names, prices);
        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(getDriver());
        cart.waitForContinueShoppingBtn();

        int computedTotal = prices.stream()
                .map(p -> p.replace("$", "").replace(",", "").trim())
                .mapToInt(Integer::parseInt)
                .sum();
        String expectedTotal = "$" + computedTotal;
        String actualTotal   = cart.getTotal();

        log.debug("Computed expected total: {}, actual cart total: {}", expectedTotal, actualTotal);

        Assert.assertEquals(actualTotal, expectedTotal,
                "Cart total does not match the sum of individual product prices");
        log.info("Thread [{}] -validateCartTotalMatchesSumOfPrices PASSED", Thread.currentThread().getId());
    }
}

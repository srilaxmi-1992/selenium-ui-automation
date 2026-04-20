package tests;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.MyCartPage;
import pages.PlaceOrderPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;
import java.util.stream.Collectors;

@Feature("Checkout")
public class PlaceOrderTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(PlaceOrderTest.class);

    @Test(description = "Validate product names and prices on checkout page", groups = {"requiresLogin"})
    @Story("Checkout product details")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Adds products, navigates to checkout, and soft-asserts names and prices match test data")
    public void validateProductDetailsOnCheckout() {
        log.info("Thread [{}] -validateProductDetailsOnCheckout started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        MyCartPage cart                 = new MyCartPage(getDriver());
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(getDriver());

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data).stream()
                .map(String::toUpperCase).collect(Collectors.toList());
        List<String> prices  = JSONReader.extractProductPrices(data);
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Expected product names: {}", names);
        log.debug("Expected prices: {}", prices);

        dashboard.addProductsToCart(JSONReader.extractProductNames(data), expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();

        List<String> actualNames  = placeOrderPage.getProductNames();
        List<String> actualPrices = placeOrderPage.getProductPrices();
        log.debug("Actual product names on checkout : {}", actualNames);
        log.debug("Actual product prices on checkout: {}", actualPrices);

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(actualNames.containsAll(names),
                "One or more product names missing on checkout page");
        soft.assertTrue(actualPrices.containsAll(prices),
                "One or more product prices missing on checkout page");
        soft.assertAll();

        log.info("Thread [{}] -validateProductDetailsOnCheckout PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate all payment form fields are visible on checkout", groups = {"requiresLogin"})
    @Story("Checkout form visibility")
    @Severity(SeverityLevel.NORMAL)
    @Description("Navigates to checkout and verifies CVV, Name on Card, Email and Country fields are all displayed")
    public void validateCheckoutFormFieldsAreVisible() {
        log.info("Thread [{}] -validateCheckoutFormFieldsAreVisible started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        MyCartPage cart                 = new MyCartPage(getDriver());
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(getDriver());

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_003");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Navigating to checkout with product: {}", names);
        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();

        boolean allVisible = placeOrderPage.areAllFormFieldsDisplayed();
        log.debug("All checkout form fields visible: {}", allVisible);

        Assert.assertTrue(allVisible,
                "One or more checkout form fields (CVV, Name, Email, Country) are not visible");
        log.info("Thread [{}] -validateCheckoutFormFieldsAreVisible PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate product count on checkout matches number of products added", groups = {"requiresLogin"})
    @Story("Checkout product details")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies the count of product rows on the checkout page equals the number of products added to cart")
    public void validateProductCountOnCheckout() {
        log.info("Thread [{}] -validateProductCountOnCheckout started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        MyCartPage cart                 = new MyCartPage(getDriver());
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(getDriver());

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Adding {} products to cart then navigating to checkout", names.size());
        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();

        int actualCount = placeOrderPage.getProductCount();
        log.debug("Product count on checkout — expected: {}, actual: {}", names.size(), actualCount);

        Assert.assertEquals(actualCount, names.size(),
                "Product count on checkout page does not match number of products added");
        log.info("Thread [{}] -validateProductCountOnCheckout PASSED", Thread.currentThread().getId());
    }
}

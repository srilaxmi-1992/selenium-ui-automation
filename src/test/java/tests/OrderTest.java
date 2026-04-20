package tests;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.MyCartPage;
import pages.OrderPage;
import pages.PlaceOrderPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;
import java.util.stream.Collectors;

@Feature("Order Confirmation")
public class OrderTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(OrderTest.class);

    @Test(description = "Validate product names and prices on order confirmation", groups = {"requiresLogin"})
    @Story("Order confirmation details")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Completes full checkout flow and soft-asserts product names and prices on the confirmation page")
    public void validateProductDetailsOnOrderConfirmation() {
        log.info("Thread [{}] -validateProductDetailsOnOrderConfirmation started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        MyCartPage cart                 = new MyCartPage(getDriver());
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(getDriver());
        OrderPage orderPage             = new OrderPage(getDriver());

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data).stream()
                .map(String::toUpperCase).collect(Collectors.toList());
        List<String> prices  = JSONReader.extractProductPrices(data);
        String expectedToast = data.get("expectedMessage").asText();

        log.debug("Expected names: {}, expected prices: {}", names, prices);

        dashboard.addProductsToCart(JSONReader.extractProductNames(data), expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();
        placeOrderPage.placeOrder(data.get("order"));

        List<String> actualNames  = orderPage.getProductNames();
        List<String> actualPrices = orderPage.getProductPrices();
        log.debug("Actual names on confirmation  : {}", actualNames);
        log.debug("Actual prices on confirmation : {}", actualPrices);

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(actualNames.containsAll(names),
                "One or more product names missing on order confirmation");
        soft.assertTrue(actualPrices.containsAll(prices),
                "One or more product prices missing on order confirmation");
        soft.assertAll();

        log.info("Thread [{}] -validateProductDetailsOnOrderConfirmation PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate order confirmation heading contains 'Thankyou'", groups = {"requiresLogin"})
    @Story("Order confirmation page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Places a full order and verifies the h1 heading text contains 'thankyou'")
    public void validateOrderConfirmationHeading() {
        log.info("Thread [{}] -validateOrderConfirmationHeading started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        MyCartPage cart                 = new MyCartPage(getDriver());
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(getDriver());
        OrderPage orderPage             = new OrderPage(getDriver());

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();
        placeOrderPage.placeOrder(data.get("order"));

        String heading = orderPage.getHeadingText();
        log.debug("Order confirmation heading: [{}]", heading);

        Assert.assertTrue(heading.toLowerCase().contains("thankyou"),
                "Order confirmation heading not found. Actual: " + heading);
        log.info("Thread [{}] -validateOrderConfirmationHeading PASSED", Thread.currentThread().getId());
    }

    @Test(description = "Validate URL changes to order confirmation page after placing order", groups = {"requiresLogin"})
    @Story("Order confirmation page")
    @Severity(SeverityLevel.NORMAL)
    @Description("Places an order and verifies the browser URL changes to the order confirmation path")
    public void validateOrderConfirmationUrl() {
        log.info("Thread [{}] -validateOrderConfirmationUrl started", Thread.currentThread().getId());

        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        MyCartPage cart                 = new MyCartPage(getDriver());
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(getDriver());
        OrderPage orderPage             = new OrderPage(getDriver());

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();
        placeOrderPage.placeOrder(data.get("order"));

        orderPage.waitForHeading();
        String currentUrl = orderPage.getPageUrl();
        log.debug("URL after placing order: {}", currentUrl);

        Assert.assertTrue(currentUrl.contains("thanks"),
                "URL did not change to order confirmation. Actual URL: " + currentUrl);
        log.info("Thread [{}] -validateOrderConfirmationUrl PASSED", Thread.currentThread().getId());
    }
}

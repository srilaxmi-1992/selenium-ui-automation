package tests;

import com.fasterxml.jackson.databind.JsonNode;
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

public class OrderTest extends BaseTest {

    @Test(description = "Validate product names and prices on order confirmation",
            groups = {"requiresLogin"})
    public void validateProductDetailsOnOrderConfirmation() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        MyCartPage cart                 = new MyCartPage(driver);
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(driver);
        OrderPage orderPage             = new OrderPage(driver);

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data).stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        List<String> prices  = JSONReader.extractProductPrices(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(JSONReader.extractProductNames(data), expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();
        placeOrderPage.placeOrder(data.get("order"));

        // SoftAssert — both names and prices verified independently
        SoftAssert soft = new SoftAssert();
        soft.assertTrue(orderPage.getProductNames().containsAll(names),
                "One or more product names missing on order confirmation");
        soft.assertTrue(orderPage.getProductPrices().containsAll(prices),
                "One or more product prices missing on order confirmation");
        soft.assertAll();
    }

    @Test(description = "Validate order confirmation heading contains 'Thankyou'",
            groups = {"requiresLogin"})
    public void validateOrderConfirmationHeading() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        MyCartPage cart                 = new MyCartPage(driver);
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(driver);
        OrderPage orderPage             = new OrderPage(driver);

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();
        placeOrderPage.placeOrder(data.get("order"));

        String heading = orderPage.getHeadingText();
        Assert.assertTrue(heading.toLowerCase().contains("thankyou"),
                "Order confirmation heading not found. Actual: " + heading);
    }

    @Test(description = "Validate URL changes to order confirmation page after placing order",
            groups = {"requiresLogin"})
    public void validateOrderConfirmationUrl() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        MyCartPage cart                 = new MyCartPage(driver);
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(driver);
        OrderPage orderPage             = new OrderPage(driver);

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
        Assert.assertTrue(currentUrl.contains("thanks"),
                "URL did not change to order confirmation. Actual URL: " + currentUrl);
    }
}

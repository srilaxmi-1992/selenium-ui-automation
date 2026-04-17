package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.MyCartPage;
import pages.OrderPage;
import pages.PlaceOrderPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;
import java.util.stream.Collectors;

public class OrderTest extends BaseTest {


    @Test(groups = {"requiresLogin"})
    public void validateProductDetailsOnPlaceOrder() {

        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        MyCartPage myCartPage = new MyCartPage(driver);
        PlaceOrderPage placeOrderPage = new PlaceOrderPage(driver);
        OrderPage orderPage = new OrderPage(driver);

        JsonNode testCaseData = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> productNames = JSONReader.extractProductNames(testCaseData);
        List<String> productPrices = JSONReader.extractProductPrices(testCaseData);
        String expectedMessage = testCaseData.get("expectedMessage").asText();
        dashboardPage.addProductsToCart(productNames, expectedMessage);
        dashboardPage.clickCartButton();
        myCartPage.waitForContinueShoppingBtn();
        myCartPage.clickCheckoutButton();

        JsonNode order = testCaseData.get("order");
        placeOrderPage.placeOrder(order);

        productNames = productNames.stream().map(String::toUpperCase).collect(Collectors.toList());
        Assert.assertTrue(orderPage.getProductNames().containsAll(productNames));
        Assert.assertTrue(orderPage.getProductPrices().containsAll(productPrices));

    }
}

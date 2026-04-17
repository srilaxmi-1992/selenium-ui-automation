package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.MyCartPage;
import pages.PlaceOrderPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceOrderTest extends BaseTest {


    @Test(description = "Validate product names and prices on checkout page",
            groups = {"requiresLogin"})
    public void validateProductDetailsOnCheckout() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        MyCartPage cart                 = new MyCartPage(driver);
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(driver);

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

        // SoftAssert — names and prices both checked; one failure doesn't skip the other
        SoftAssert soft = new SoftAssert();
        soft.assertTrue(placeOrderPage.getProductNames().containsAll(names),
                "One or more product names missing on checkout page");
        soft.assertTrue(placeOrderPage.getProductPrices().containsAll(prices),
                "One or more product prices missing on checkout page");
        soft.assertAll();
    }

    @Test(description = "Validate all payment form fields are visible on checkout",
            groups = {"requiresLogin"})
    public void validateCheckoutFormFieldsAreVisible() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        MyCartPage cart                 = new MyCartPage(driver);
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(driver);

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_003");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();

        Assert.assertTrue(placeOrderPage.areAllFormFieldsDisplayed(),
                "One or more checkout form fields (CVV, Name, Email, Country) are not visible");
    }

    @Test(description = "Validate product count on checkout matches number of products added",
            groups = {"requiresLogin"})
    public void validateProductCountOnCheckout() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        MyCartPage cart                 = new MyCartPage(driver);
        PlaceOrderPage placeOrderPage   = new PlaceOrderPage(driver);

        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();
        cart.waitForContinueShoppingBtn();
        cart.clickCheckoutButton();

        Assert.assertEquals(placeOrderPage.getProductCount(), names.size(),
                "Product count on checkout page does not match number of products added");
    }

}

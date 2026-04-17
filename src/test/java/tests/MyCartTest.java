package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.MyCartPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;

public class MyCartTest extends BaseTest {

    @Test(description = "Validate product names, prices and total in cart",
            groups = {"requiresLogin"})
    public void validateProductsDisplayedInMyCart() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        JsonNode data          = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names     = JSONReader.extractProductNames(data);
        List<String> prices    = JSONReader.extractProductPrices(data);
        String expectedTotal   = data.get("total").asText();
        String expectedToast   = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(driver);
        cart.waitForContinueShoppingBtn();

        // SoftAssert — all 3 validations run even if one fails
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(cart.getProductNames(),
                names.stream().map(String::toUpperCase).toList(),
                "Product names mismatch in cart");
        soft.assertEquals(cart.getProductPrices(), prices,
                "Product prices mismatch in cart");
        soft.assertEquals(cart.getTotal(), expectedTotal,
                "Cart total mismatch");
        soft.assertAll();
    }

    @Test(description = "Validate Continue Shopping navigates back to products dashboard",
            groups = {"requiresLogin"})
    public void validateContinueShoppingNavigation() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_003");
        List<String> names   = JSONReader.extractProductNames(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(driver);
        cart.waitForContinueShoppingBtn();
        cart.clickContinueShoppingButton();

        Assert.assertTrue(dashboard.isDashboardLoaded(),
                "Clicking Continue Shopping did not return to the products dashboard");
    }

    @Test(description = "Validate cart is empty when no products are added",
            groups = {"requiresLogin"})
    public void validateEmptyCartHasNoProducts() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(driver);
        cart.waitForContinueShoppingBtn();

        Assert.assertTrue(cart.isCartEmpty(),
                "Cart should contain no products when nothing was added");
    }

    @Test(description = "Validate cart total equals sum of individual product prices",
            groups = {"requiresLogin"})
    public void validateCartTotalMatchesSumOfPrices() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        JsonNode data        = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> names   = JSONReader.extractProductNames(data);
        List<String> prices  = JSONReader.extractProductPrices(data);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.addProductsToCart(names, expectedToast);
        dashboard.clickCartButton();

        MyCartPage cart = new MyCartPage(driver);
        cart.waitForContinueShoppingBtn();

        // Compute expected total from JSON prices (e.g. "$11500" + "$55000" = "$66500")
        int expectedTotal = prices.stream()
                .map(p -> p.replace("$", "").replace(",", "").trim())
                .mapToInt(Integer::parseInt)
                .sum();

        Assert.assertEquals(cart.getTotal(), "$" + expectedTotal,
                "Cart total does not match the sum of individual product prices");
    }


}

package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;


public class ProductsDashboardTest extends BaseTest {

    @Test(description = "Validate products displayed", groups = {"requiresLogin"})
    public void validateProductsAreDisplayed() {

        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        int proudcts = dashboardPage.getNumberOfProducts();
        Assert.assertTrue(proudcts > 0);

    }

    @Test(description = "Validate user can add product to Cart", groups = {"requiresLogin"})
    public void validateAddToCart() {

        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        JsonNode testCaseData = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_001");
        List<String> productNames = JSONReader.extractProductNames(testCaseData);
        String expectedMessage = testCaseData.get("expectedMessage").asText();
        dashboardPage.addProductsToCart(productNames, expectedMessage);
        int count = dashboardPage.getNumberOfProductsAddedToCart();
        Assert.assertEquals(count, productNames.size());
    }

    @Test(description = "Validate every product card has a non-empty name",
            groups = {"requiresLogin"})
    public void validateProductNamesAreNotEmpty() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        List<String> names = dashboard.getProductNames();

        Assert.assertFalse(names.isEmpty(), "Product name list is empty");
        names.forEach(name ->
                Assert.assertFalse(name.isBlank(),
                        "Found a product card with a blank name"));
    }

    @Test(description = "Validate Add to Cart button is enabled for each product",
            groups = {"requiresLogin"})
    public void validateAddToCartButtonEnabledForAllProducts() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        List<String> names = dashboard.getProductNames();

        Assert.assertFalse(names.isEmpty(), "No products found to check");
        names.stream()
                .map(name -> name.equals("IPHONE 13 PRO") ? name.toLowerCase() : name)
                .forEach(name ->
                        Assert.assertTrue(dashboard.isAddToCartButtonEnabled(name),
                                "Add to Cart button disabled for product: " + name));

    }

    @Test(description = "Validate toast message when a single product is added to cart",
            groups = {"requiresLogin"})
    public void validateAddToCartToastMessage() {
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);
        JsonNode data = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_003");
        String productName = JSONReader.extractProductNames(data).get(0);
        String expectedToast = data.get("expectedMessage").asText();

        dashboard.clickOnAddToCartButton(productName);
        String actualToast = dashboard.getAddToCartMessage();

        Assert.assertEquals(actualToast, expectedToast,
                "Toast message mismatch after adding product to cart");
    }

}

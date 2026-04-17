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


}

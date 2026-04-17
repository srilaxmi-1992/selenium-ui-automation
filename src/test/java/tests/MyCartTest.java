package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.MyCartPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

import java.util.List;

public class MyCartTest extends BaseTest {

    @Test(groups = {"requiresLogin"})
    public void validateProductsDisplayedInMyCart() {
        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        JsonNode testCaseData = JSONReader.getTestData("productsTestData.json", "PRODUCTS_TC_002");
        List<String> productNames = JSONReader.extractProductNames(testCaseData);
        String expectedMessage = testCaseData.get("expectedMessage").asText();
        dashboardPage.addProductsToCart(productNames, expectedMessage);
        dashboardPage.clickCartButton();
        MyCartPage myCartPage = new MyCartPage(driver);
        List<String> productPrices = JSONReader.extractProductPrices(testCaseData);
        Assert.assertEquals(myCartPage.getProductNames(), productNames.stream().map(String::toUpperCase).toList());
        Assert.assertEquals(myCartPage.getProductPrices(), productPrices);
        Assert.assertEquals(myCartPage.getTotal(), testCaseData.get("total").asText());
    }

}

package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

public class LoginPageTest extends BaseTest {

    @DataProvider(name = "loginScenarios")
    public Object[][] loginScenarios() {
        return new Object[][]{
                {"TC_001"},
                {"TC_002"},
                {"TC_003"},
                {"TC_004"},
                {"TC_005"},
                {"TC_006"}
        };
    }

    @Test(dataProvider = "loginScenarios",
            description = "Data-driven login validation")
    public void validateLoginScenario(String tcId) {

        JsonNode data = JSONReader.getTestData("loginTestData.json", tcId);
        String email = data.get("email").asText();
        String password = data.get("password").asText();
        String expectedMessage = data.get("expectedMessage").asText();
        String assertionType = data.get("assertionType").asText();

        LoginPage loginPage = new LoginPage(driver);
        ProductsDashboardPage dashboard = new ProductsDashboardPage(driver);

        loginPage.performLogin(email, password);

        switch (assertionType) {

            case "SUCCESS_TOAST" -> Assert.assertEquals(dashboard.getSuccessMessage(), expectedMessage,
                    tcId + ": Success toast message mismatch");

            case "DASHBOARD_URL" -> Assert.assertTrue(dashboard.getCurrentUrl("dashboard")
                            .contains(expectedMessage),
                    tcId + ": URL does not contain 'dashboard'");

            case "ERROR_TOAST" -> Assert.assertEquals(loginPage.getErrorMessage(), expectedMessage,
                    tcId + ": Error toast message mismatch");

            case "EMAIL_FIELD_ERROR" -> Assert.assertEquals(loginPage.getInvalidEmailMessage(), expectedMessage,
                    tcId + ": Email field error message mismatch");

            case "PASSWORD_FIELD_ERROR" -> Assert.assertEquals(loginPage.getInvalidPasswordMessage(), expectedMessage,
                    tcId + ": Password field error message mismatch");

            default -> throw new IllegalArgumentException(
                    "Unknown assertionType in JSON: " + assertionType);
        }
    }
}
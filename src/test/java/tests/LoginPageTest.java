package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsDashboardPage;
import utils.JSONReader;

public class LoginPageTest extends BaseTest {

    @Test(description = "Login with valid email id and password")
    public void loginWithValidEmailAndPassword() {
        LoginPage loginPage = new LoginPage(driver);
        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        JsonNode data = JSONReader.getTestData("loginTestData.json", "TC_001");
        String email = data.get("email").asText();
        String password = data.get("password").asText();
        String expectedMessage = data.get("expectedMessage").asText();
        loginPage.performLogin(email, password);
        String actualMessage = dashboardPage.getSuccessMessage();
        Assert.assertEquals(actualMessage, expectedMessage);
    }

    @Test(description = "Validate user is redirected to dashboard after successful login")
    public void validateRedirectedToDashboard() {
        LoginPage loginPage = new LoginPage(driver);
        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        JsonNode data = JSONReader.getTestData("loginTestData.json", "TC_002");
        String email = data.get("email").asText();
        String password = data.get("password").asText();
        loginPage.performLogin(email, password);
        System.out.println(dashboardPage.getCurrentUrl("dashboard"));
        Assert.assertTrue(dashboardPage.getCurrentUrl("dashboard").contains("dashboard"));
    }

    @Test(description = "Login with Invalid email id and Valid password")
    public void loginWithInvalidEmailAndValidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        JsonNode data = JSONReader.getTestData("loginTestData.json", "TC_003");
        String email = data.get("email").asText();
        String password = data.get("password").asText();
        String expectedMessage = data.get("expectedMessage").asText();
        loginPage.performLogin(email, password);
        String actualMessage = loginPage.getErrorMessage();
        Assert.assertEquals(actualMessage, expectedMessage);
    }

    @Test(description = "Login with valid email id and Invalid password")
    public void loginWithValidEmailAndInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        JsonNode data = JSONReader.getTestData("loginTestData.json", "TC_004");
        String email = data.get("email").asText();
        String password = data.get("password").asText();
        String expectedMessage = data.get("expectedMessage").asText();
        loginPage.performLogin(email, password);
        String actualMessage = loginPage.getErrorMessage();
        Assert.assertEquals(actualMessage, expectedMessage);
    }

    @Test(description = "Login with empty email id and validate the error message")
    public void loginWithEmptyEmail() {
        LoginPage loginPage = new LoginPage(driver);
        JsonNode data = JSONReader.getTestData("loginTestData.json", "TC_005");
        String email = data.get("email").asText();
        String password = data.get("password").asText();
        String expectedMessage = data.get("expectedMessage").asText();
        loginPage.performLogin(email, password);
        String actualMessage = loginPage.getInvalidEmailMessage();
        Assert.assertEquals(actualMessage, expectedMessage);
    }

    @Test(description = "Login with empty password and validate the error message")
    public void loginWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver);
        JsonNode data = JSONReader.getTestData("loginTestData.json", "TC_006");
        String email = data.get("email").asText();
        String password = data.get("password").asText();
        String expectedMessage = data.get("expectedMessage").asText();
        loginPage.performLogin(email, password);
        String actualMessage = loginPage.getInvalidPasswordMessage();
        Assert.assertEquals(actualMessage,expectedMessage);
    }
}

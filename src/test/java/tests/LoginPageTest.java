package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsDashboardPage;

public class LoginPageTest extends BaseTest {

    @Test(description = "Login with valid email id and password")
    public void loginWithValidEmailAndPassword() {
        LoginPage loginPage = new LoginPage(driver);
        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        loginPage.performLogin("srilaxmilucky123@gmail.com", "Naresh@12345");
        String actualMessage = dashboardPage.getSuccessMessage();
        Assert.assertEquals(actualMessage, "Login Successfully");
    }

    @Test(description = "Validate user is redirected to dashboard after successful login")
    public void validateRedirectedToDashboard() {
        LoginPage loginPage = new LoginPage(driver);
        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        loginPage.performLogin("srilaxmilucky123@gmail.com", "Naresh@12345");
        System.out.println(dashboardPage.getCurrentUrl("dashboard"));
        Assert.assertTrue(dashboardPage.getCurrentUrl("dashboard").contains("dashboard"));
    }

    @Test(description = "Login with Invalid email id and Valid password")
    public void loginWithInvalidEmailAndValidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.performLogin("srilaxm3@gmail.com", "Naresh@12345");
        String actualMessage = loginPage.getErrorMessage();
        Assert.assertEquals(actualMessage, "Incorrect email or password.");
    }

    @Test(description = "Login with valid email id and Invalid password")
    public void loginWithValidEmailAndInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.performLogin("srilaxmilucky123@gmail.com", "@12345");
        String actualMessage = loginPage.getErrorMessage();
        Assert.assertEquals(actualMessage, "Incorrect email or password.");
    }

    @Test(description = "Login with empty email id and validate the error message")
    public void loginWithEmptyEmail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.performLogin("", "Naresh@12345");
        String actualMessage = loginPage.getInvalidEmailMessage();
        Assert.assertEquals(actualMessage, "*Email is required");
    }

    @Test(description = "Login with empty password and validate the error message")
    public void loginWithEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.performLogin("srilaxmilucky123@gmail.com", "");
        String actualMessage = loginPage.getInvalidPasswordMessage();
        Assert.assertEquals(actualMessage, "*Password is required");
    }
}

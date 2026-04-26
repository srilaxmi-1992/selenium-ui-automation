package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import pages.LoginPage;
import pages.ProductsDashboardPage;
import utils.DriverFactory;
import utils.JSONReader;

import java.lang.reflect.Method;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    public WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setup(@Optional String browser, Method method) {
        // Set logging context first
        ThreadContext.clearAll();
        String testName = getClass().getSimpleName() + "_" + method.getName()
                .replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        ThreadContext.put("testName", testName);
        log.info("=== Starting test: {} ===", testName);

        // Init driver via factory
        DriverFactory.initDriver(browser);
    }

    @BeforeMethod(onlyForGroups = "requiresLogin", dependsOnMethods = "setup")
    public void setupAndLogin() {
        log.info("Thread [{}] - performing pre-test login", Thread.currentThread().getId());
        JsonNode testData = JSONReader.getTestData("loginTestData.json", "TC_001");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.performLogin(
                testData.get("email").asText(),
                testData.get("password").asText()
        );
        ProductsDashboardPage dashboard = new ProductsDashboardPage(getDriver());
        Assert.assertTrue(dashboard.isDashboardLoaded(), "Dashboard did not load after login");
        log.info("Thread [{}] - login successful", Thread.currentThread().getId());
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        log.info("=== Finished test ===");
        DriverFactory.quitDriver();
        ThreadContext.clearAll();
    }
}
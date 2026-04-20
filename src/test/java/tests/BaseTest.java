package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import pages.ProductsDashboardPage;
import utils.ConfigReader;
import utils.JSONReader;

import java.time.Duration;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);
    private ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThread.get();
    }

    @BeforeMethod
    public void setup() {
        String browser = ConfigReader.getProperty("browser");
        String url     = ConfigReader.getProperty("url");
        long timeout   = Long.parseLong(ConfigReader.getProperty("timeout"));

        log.info("Thread [{}] - launching browser: {}", Thread.currentThread().getId(), browser);

        WebDriver driver = switch (browser.toLowerCase()) {
            case "chrome"  -> new ChromeDriver();
            case "firefox" -> new FirefoxDriver();
            case "edge"    -> new EdgeDriver();
            default        -> throw new RuntimeException("Unsupported browser: " + browser);
        };

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.get(url);
        driverThread.set(driver);

        log.info("Thread [{}]-browser ready, navigated to: {}", Thread.currentThread().getId(), url);
    }



    @BeforeMethod(onlyForGroups = "requiresLogin")
    public void setupAndLogin() {
        log.info("performing pre-test login (requiresLogin group)",
                Thread.currentThread().getId());
        LoginPage loginPage = new LoginPage(getDriver());
        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(getDriver());
        JsonNode tesdata = JSONReader.getTestData("loginTestData.json", "TC_001");
        String email = tesdata.get("email").asText();
        String password = tesdata.get("password").asText();
        loginPage.performLogin(email, password);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard did not load after login");
        log.info("login successful, dashboard loaded", Thread.currentThread().getId());
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        WebDriver driver = getDriver();
        if (driver != null) {
            log.info("Thread [{}] - quitting browser", Thread.currentThread().getId());
            driver.quit();
            driverThread.remove(); // prevents memory leak in thread pools
            log.info("Thread [{}] - driver removed from ThreadLocal", Thread.currentThread().getId());
        } else {
            log.warn("Thread [{}] - driver was null in teardown, nothing to quit",
                    Thread.currentThread().getId());
        }
    }

}

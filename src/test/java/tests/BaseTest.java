package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import pages.ProductsDashboardPage;
import utils.ConfigReader;
import utils.JSONReader;

import java.time.Duration;

public class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    WebDriver driver;

    @BeforeMethod()
    public void setup() {
        String browser = ConfigReader.getProperty("browser");
        String url = ConfigReader.getProperty("url");
        long timeout = Long.parseLong(ConfigReader.getProperty("timeout"));

        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            default:
                throw new RuntimeException("Unsupported browser: " + browser);
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.get(url);
    }

    @BeforeMethod(onlyForGroups = "requiresLogin")
    public void setupAndLogin() {
        LoginPage loginPage = new LoginPage(driver);
        ProductsDashboardPage dashboardPage = new ProductsDashboardPage(driver);
        JsonNode tesdata = JSONReader.getTestData("loginTestData.json", "TC_001");
        String email = tesdata.get("email").asText();
        String password = tesdata.get("password").asText();
        loginPage.performLogin(email, password);
        Assert.assertTrue(dashboardPage.isDashboardLoaded());
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        if (driver != null)
            driver.quit();
    }
}

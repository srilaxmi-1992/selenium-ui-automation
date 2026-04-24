package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import pages.LoginPage;
import pages.ProductsDashboardPage;
import utils.ConfigReader;
import utils.JSONReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);
    private ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    public WebDriver getDriver() {
        return driverThread.get();
    }

    @Parameters("browser")
    @BeforeMethod
    public void setup(@Optional String browser) {

        String url = ConfigReader.getProperty("url");
        long timeout = Long.parseLong(ConfigReader.getProperty("timeout"));

        WebDriver driver = initDriver(browser);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.get(url);
        driverThread.set(driver);

        log.info("Thread [{}]-browser ready, navigated to: {}", Thread.currentThread().getId(), url);
    }

    public WebDriver initDriver(String browserFromTestNG) {
        String browser = (browserFromTestNG == null ||
                browserFromTestNG.isEmpty()) ? ConfigReader.getProperty("browser") : browserFromTestNG;

        boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));
        boolean isRemote = Boolean.parseBoolean(ConfigReader.getProperty("remote"));
        if (isRemote)
            return initRemoteDriver(browser, isHeadless);
        else
            return initLocalDriver(browser, isHeadless);
    }

    public WebDriver initLocalDriver(String browser, boolean headless) {
        WebDriver driver = switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--window-size=1920,1080");
                }
                yield new ChromeDriver(options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                    options.addArguments("--width=1920");
                    options.addArguments("--height=1080");
                }
                yield new FirefoxDriver();
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new", "--window-size=1920,1080");
                }
                yield new EdgeDriver();
            }
            default -> throw new RuntimeException("Unsupported browser: " + browser);
        };
        log.info("Thread [{}] - launching browser: {}", Thread.currentThread().getId(), browser);
        return driver;
    }

    public WebDriver initRemoteDriver(String browser, boolean headless) {
        MutableCapabilities options;
        String gridUrl = ConfigReader.getProperty("grid.url");

        switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions chrome = new ChromeOptions();
                if (headless) {
                    chrome.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                }
                options = chrome;
            }
            case "firefox" -> {
                FirefoxOptions firefox = new FirefoxOptions();
                if (headless) {
                    firefox.addArguments("-headless", "--width=1920", "--height=1080");
                }
                options = firefox;
            }
            case "edge" -> {
                EdgeOptions edge = new EdgeOptions();
                if (headless) {
                    edge.addArguments("--headless=new", "--window-size=1920,1080");
                }
                options = edge;
            }
            default -> throw new RuntimeException("Unsupported browser: " + browser);
        }
        try {
            log.info("Thread [{}] - launching Remote browser : {}", Thread.currentThread().getId(), browser);
            return new RemoteWebDriver(
                    new URL(gridUrl),
                    options
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to connect to Selenium Grid", e);
        }
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

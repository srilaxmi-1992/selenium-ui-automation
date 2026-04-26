package utils;

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
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    // ThreadLocal — one driver instance per thread (parallel safe)
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return driverThread.get();
    }

    public static void initDriver(String browserFromTestNG) {
        String browser = (browserFromTestNG == null || browserFromTestNG.isEmpty())
                ? ConfigReader.getProperty("browser")
                : browserFromTestNG;

        boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));
        boolean isRemote   = Boolean.parseBoolean(ConfigReader.getProperty("remote"));
        long    timeout    = Long.parseLong(ConfigReader.getProperty("timeout"));
        String  url        = ConfigReader.getProperty("url");

        WebDriver driver = isRemote
                ? initRemoteDriver(browser, isHeadless)
                : initLocalDriver(browser, isHeadless);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        driver.get(url);

        driverThread.set(driver);
        log.info("Thread [{}] - browser [{}] ready, navigated to: {}",
                Thread.currentThread().getId(), browser, url);
    }

    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            log.info("Thread [{}] - quitting browser", Thread.currentThread().getId());
            driver.quit();
            driverThread.remove();   // prevents memory leak in thread pools
            log.info("Thread [{}] - driver removed from ThreadLocal", Thread.currentThread().getId());
        } else {
            log.warn("Thread [{}] - driver was null in quitDriver, nothing to quit",
                    Thread.currentThread().getId());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static WebDriver initLocalDriver(String browser, boolean headless) {
        log.info("Thread [{}] - launching local browser: {}", Thread.currentThread().getId(), browser);
        return switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                }
                yield new ChromeDriver(options);
            }
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) options.addArguments("-headless", "--width=1920", "--height=1080");
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                if (headless) options.addArguments("--headless=new", "--window-size=1920,1080");
                yield new EdgeDriver(options);
            }
            default -> throw new RuntimeException("Unsupported browser: " + browser);
        };
    }

    private static WebDriver initRemoteDriver(String browser, boolean headless) {
        log.info("Thread [{}] - launching remote browser: {}", Thread.currentThread().getId(), browser);
        String gridUrl = ConfigReader.getProperty("grid.url");
        MutableCapabilities options = switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions o = new ChromeOptions();
                if (headless) o.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                yield o;
            }
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless", "--width=1920", "--height=1080");
                yield o;
            }
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new", "--window-size=1920,1080");
                yield o;
            }
            default -> throw new RuntimeException("Unsupported browser: " + browser);
        };
        try {
            return new RemoteWebDriver(new URL(gridUrl), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to connect to Selenium Grid at: " + gridUrl, e);
        }
    }
}
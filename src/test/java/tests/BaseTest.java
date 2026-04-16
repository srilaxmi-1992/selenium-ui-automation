package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;

import java.time.Duration;

public class BaseTest {

    WebDriver driver;

    @BeforeMethod(alwaysRun = true)
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

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        if (driver != null)
            driver.quit();
    }
}

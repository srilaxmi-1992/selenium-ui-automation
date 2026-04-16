package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumUtils {

    WebDriver driver;
    WebDriverWait wait;

    public SeleniumUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Clicks an element
     */
    public void click(WebElement element) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(element));
            el.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click element: " + element, e);
        }
    }

    /**
     * Types text into element
     */
    public void type(WebElement element, String text) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOf(element));
            el.clear();
            el.sendKeys(text);
        } catch (Exception e) {
            throw new RuntimeException("Failed to type into element: " + element, e);
        }
    }

    public String getText(WebElement element) {
        String text = "";
        try {
            // Wait for visibility
            WebElement el = wait.until(ExpectedConditions.visibilityOf(element));
            text = el.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get text from element: " + element, e);
        }
        return text;
    }

    /**
     * Returns the current URL of the browser.
     * Waits until the URL is non-empty and stable.
     */
    public String getCurrentUrl(String url) {
        try {
            wait.until(ExpectedConditions.urlContains(url));
            return driver.getCurrentUrl();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get current URL", e);
        }
    }


}

package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumUtils {

    private static final Logger log = LogManager.getLogger(SeleniumUtils.class);

    WebDriver driver;
    WebDriverWait wait;

    public SeleniumUtils(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void click(WebElement element) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(element));
            el.click();
            log.debug("Clicked element: {}", element);
        } catch (Exception e) {
            log.error("Failed to click element: {}", element, e);
            throw new RuntimeException("Failed to click element: " + element, e);
        }
    }

    public void type(WebElement element, String text) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOf(element));
            el.clear();
            el.sendKeys(text);
            log.debug("Typed into element: {}", element);
        } catch (Exception e) {
            log.error("Failed to type into element: {}", element, e);
            throw new RuntimeException("Failed to type into element: " + element, e);
        }
    }

    public String getText(WebElement element) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOf(element));
            String text = el.getText();
            log.debug("Got text [{}] from element: {}", text, element);
            return text;
        } catch (Exception e) {
            log.error("Failed to get text from element: {}", element, e);
            throw new RuntimeException("Failed to get text from element: " + element, e);
        }
    }

    public String getCurrentUrl(String expectedFragment) {
        try {
            wait.until(ExpectedConditions.urlContains(expectedFragment));
            String url = driver.getCurrentUrl();
            log.debug("Current URL: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Failed to get current URL containing: {}", expectedFragment, e);
            throw new RuntimeException("Failed to get current URL", e);
        }
    }

    public boolean isElementEnabled(WebElement element) {
        try {
            boolean enabled = element.isEnabled();
            log.debug("Element enabled: {} — {}", enabled, element);
            return enabled;
        } catch (Exception e) {
            log.warn("isElementEnabled check failed, returning false. Element: {}", element);
            return false;
        }
    }

    public boolean isElementDisplayed(WebElement element) {
        try {
            boolean displayed = element.isDisplayed();
            log.debug("Element displayed: {} — {}", displayed, element);
            return displayed;
        } catch (Exception e) {
            log.warn("isElementDisplayed check failed, returning false. Element: {}", element);
            return false;
        }
    }

    public boolean isElementEnabled(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            boolean enabled = element.isEnabled();
            log.debug("Element enabled by locator {}: {}", locator, enabled);
            return enabled;
        } catch (Exception e) {
            log.warn("isElementEnabled(By) check failed for locator: {} — {}", locator, e.getMessage());
            return false;
        }
    }

    public void clickUsingActions(By locator) {
        try {
            Actions actions  = new Actions(driver);
            WebElement el    = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            actions.moveToElement(el).click().build().perform();
            log.debug("Actions click on locator: {}", locator);
        } catch (Exception e) {
            log.error("Actions click failed for locator: {}", locator, e);
            throw new RuntimeException("Failed to click element: " + locator, e);
        }
    }

    public void waitForOverlayToDisappear(By overlay) {
        try {
            log.debug("Waiting for overlay to disappear: {}", overlay);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(overlay));
            log.debug("Overlay gone: {}", overlay);
        } catch (Exception e) {
            log.error("Overlay did not disappear: {}", overlay, e);
            throw new RuntimeException("Failed to wait for element to disappear: " + e.getMessage());
        }
    }

    private FluentWait<WebDriver> getWait() {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public WebElement waitForElement(WebElement element) {
        log.debug("FluentWait for element visibility: {}", element);
        return getWait().until(driver -> {
            try {
                return element.isDisplayed() ? element : null;
            } catch (StaleElementReferenceException e) {
                log.warn("StaleElementReferenceException while waiting — retrying");
                return null;
            }
        });
    }

    public WebElement waitForElementVisible(By locator) {
        log.debug("FluentWait for locator visibility: {}", locator);
        return getWait().until(driver -> {
            WebElement el = driver.findElement(locator);
            return el.isDisplayed() ? el : null;
        });
    }
}

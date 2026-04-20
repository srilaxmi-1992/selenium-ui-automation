package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SeleniumUtils {

    private static final Logger log = LogManager.getLogger(SeleniumUtils.class);

    WebDriver driver;
    WebDriverWait wait;
    private Duration timeout = Duration.ofSeconds(15);

    public SeleniumUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    private FluentWait<WebDriver> getWait() {
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }

    public WebElement waitForElementVisible(By locator) {
        log.debug("FluentWait for locator visibility: {}", locator);
        return getWait().until(driver -> {
            WebElement el = driver.findElement(locator);
            return el.isDisplayed() ? el : null;
        });
    }

    public void click(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
            log.debug("Clicked element: {}", el);
        } catch (Exception e) {
            log.error("Failed to click element: {}", locator, e);
            throw new RuntimeException("Failed to click element: " + locator, e);
        }
    }

    public void type(By locator, String text) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.clear();
            element.sendKeys(text);
            log.debug("Typed into element: {}", element);
        } catch (Exception e) {
            log.error("Failed to type into element: {}", locator, e);
            throw new RuntimeException("Failed to type into element: " + locator, e);
        }
    }

    public String getText(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            String text = el.getText();
            log.debug("Got text [{}] from element: {}", text, el);
            return text;
        } catch (Exception e) {
            log.error("Failed to get text from element: {}", locator, e);
            throw new RuntimeException("Failed to get text from element: " + locator, e);
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

    public boolean isElementDisplayed(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            boolean enabled = element.isDisplayed();
            log.debug("Element displayed by locator {}: {}", locator, enabled);
            return enabled;
        } catch (Exception e) {
            log.warn("isElementDisplayed check failed, returning false. Element: {}", locator);
            return false;
        }
    }

    public boolean isElementEnabled(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
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
            Actions actions = new Actions(driver);
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
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


    public WebElement waitForElement(By locator) {
        log.debug("FluentWait for element visibility: {}", locator);
        return getWait().until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                return element.isDisplayed() ? element : null;
            } catch (StaleElementReferenceException e) {
                log.warn("StaleElementReferenceException while waiting — retrying");
                return null;
            }
        });
    }

    public int getElementsSize(By locator) {
        try {
            List<WebElement> elements = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
            );
            return elements.size();
        } catch (Exception e) {
            log.error("Error while getting element count : {}", locator, e);
            throw new RuntimeException("Failed to wait for element to disappear: " + e.getMessage());
        }
    }

    public List<String> getElementsText(By locator) {
        List<String> texts = new ArrayList<>();

        try {
            List<WebElement> elements = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(locator)
            );
            for (WebElement element : elements) {
                texts.add(element.getText().trim());
            }
        } catch (Exception e) {
            log.error("Error while getting text of all elements : {}", locator, e);
            throw new RuntimeException("Failed to get text of all elements : " + e.getMessage());
        }

        return texts;
    }

    public boolean isElementListEmpty(By locator) {
        try {
            return driver.findElements(locator).isEmpty();
        } catch (Exception e) {
            log.error("Error checking elements:  {}", locator, e);
            throw new RuntimeException("Failed to get elements size :  " + e.getMessage());
        }
    }
}

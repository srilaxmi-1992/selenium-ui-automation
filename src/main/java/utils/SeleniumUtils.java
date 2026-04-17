package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumUtils {

    WebDriver driver;
    WebDriverWait wait;


    public SeleniumUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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

    public boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementEnabled(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element.isEnabled();
        } catch (Exception e) {
            System.out.println("Exception --> " + e.getMessage());
            return false;
        }
    }

    public void clickUsingActions(By locator) {
        try {
            Actions actions = new Actions(driver);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            actions.moveToElement(element)
                    .click()
                    .build()
                    .perform();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click element: " + locator, e);
        }
    }


    public void waitForOverlayToDisappear(By overlay) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(overlay));
        } catch (Exception e) {
            throw new RuntimeException("Failed to wait for an element to disappear " + e.getMessage());
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
        return getWait().until(driver -> {
            try {
                return element.isDisplayed() ? element : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    public WebElement waitForElementVisible(By locator) {
        return getWait().until(driver -> {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed() ? element : null;
        });
    }

}

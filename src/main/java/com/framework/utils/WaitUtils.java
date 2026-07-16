package com.framework.utils;

import com.framework.config.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils — Wrapper around Selenium's WebDriverWait for explicit waits.
 *
 * WHY explicit waits?
 *   Implicit waits apply globally but can mask real timing issues.
 *   Explicit waits are condition-specific and more reliable for dynamic UIs.
 *
 * Default timeout is read from config.properties (explicit.wait key).
 * You can also pass a custom timeout for a specific wait instance.
 *
 * Usage in Page classes (inherited via BasePage):
 *   wait.waitForVisible(submitButton);        // waits until button is visible
 *   wait.waitForClickable(By.id("submit"));   // waits until element is clickable
 *   wait.waitForUrlContains("dashboard");     // waits for URL to change
 */
public class WaitUtils {

    private final WebDriverWait wait;

    // Default timeout read from config so it can be changed without recompiling
    private static final long DEFAULT_TIMEOUT =
            Long.parseLong(ConfigReader.get("explicit.wait", "15"));

    /**
     * Creates a WaitUtils with the default timeout from config.properties.
     *
     * @param driver The active WebDriver instance
     */
    public WaitUtils(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    /**
     * Creates a WaitUtils with a custom timeout.
     * Use when a specific operation is known to be slower or faster than the default.
     *
     * @param driver         The active WebDriver instance
     * @param timeoutSeconds Custom timeout in seconds
     */
    public WaitUtils(WebDriver driver, long timeoutSeconds) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Waits until the element is visible (present in DOM AND has non-zero size).
     * Use before reading text or checking state.
     *
     * @param element WebElement to wait for
     * @return The same element once visible (for chaining)
     */
    public WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until an element matching the locator is visible.
     * Use when you don't have a pre-found WebElement reference.
     *
     * @param locator By locator (e.g., By.id("submit"))
     * @return The located element once visible
     */
    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the element is visible AND enabled (ready to receive input or be clicked).
     * Always use this before calling click() to avoid ElementNotInteractableException.
     *
     * @param element WebElement to wait for
     * @return The same element once clickable
     */
    public WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until an element matching the locator is clickable.
     *
     * @param locator By locator
     * @return The located element once clickable
     */
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until the element disappears from view (e.g., a loading spinner hiding).
     *
     * @param element WebElement expected to become invisible
     * @return true once the element is no longer visible
     */
    public boolean waitForInvisible(WebElement element) {
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Waits until an element is present in the DOM (doesn't have to be visible).
     * Use for hidden elements or elements that exist but are off-screen.
     *
     * @param locator By locator
     * @return The located element once present
     */
    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until the browser URL contains the specified fragment.
     * Useful after clicking a link or submitting a form to confirm navigation happened.
     *
     * Example: waitForUrlContains("dashboard") confirms redirect to dashboard page.
     *
     * @param urlFragment Partial URL string to check for
     * @return true once URL matches
     */
    public boolean waitForUrlContains(String urlFragment) {
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Waits until a JavaScript alert/confirm/prompt dialog appears.
     * Must be called before driver.switchTo().alert() to avoid NoAlertPresentException.
     *
     * @return The Alert object once present
     */
    public Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Waits until the element contains the specified text.
     * Useful for asserting dynamic text updates (e.g., success messages, counters).
     *
     * @param element WebElement to inspect
     * @param text    Text that should appear in the element
     * @return true once the text is present
     */
    public boolean waitForTextInElement(WebElement element, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }
}

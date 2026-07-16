package com.framework.pages;

import com.framework.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * BasePage — Parent class for all Page Object classes.
 *
 * Implements the Page Object Model (POM) pattern:
 *   - Each web page of the application gets its own Page class (e.g., LoginPage, HomePage)
 *   - Each Page class extends BasePage to inherit common browser interactions
 *   - Locators are defined as fields using @FindBy annotations
 *   - Action methods (click, type, getText) are defined as methods
 *
 * This approach keeps test code clean — tests call page methods, not raw Selenium calls.
 *
 * Usage:
 *   public class LoginPage extends BasePage {
 *
 *       @FindBy(id = "username") private WebElement usernameField;
 *       @FindBy(id = "loginBtn")  private WebElement loginButton;
 *
 *       public LoginPage(WebDriver driver) {
 *           super(driver); // MUST call super — initializes @FindBy elements
 *       }
 *
 *       public void login(String user, String pass) {
 *           type(usernameField, user);   // inherited from BasePage
 *           click(loginButton);          // inherited from BasePage
 *       }
 *   }
 */
public class BasePage {

    // WebDriver instance passed from the test — each page holds a reference to the active driver
    protected final WebDriver driver;

    // WaitUtils wraps WebDriverWait — use it to wait for elements before interacting
    protected final WaitUtils wait;

    // Logger available in all page subclasses; getClass() ensures correct class name in logs
    protected final Logger log = LogManager.getLogger(this.getClass());

    /**
     * Constructor — must be called by every subclass via super(driver).
     * PageFactory.initElements scans @FindBy annotations and wires up the WebElements.
     *
     * @param driver The active WebDriver instance from the current test thread
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        PageFactory.initElements(driver, this); // initializes all @FindBy annotated fields
    }

    /**
     * Waits for the element to be clickable, then clicks it.
     * Prefer this over element.click() to avoid ElementNotInteractableException.
     */
    protected void click(WebElement element) {
        wait.waitForClickable(element);
        element.click();
        log.debug("Clicked element: {}", element);
    }

    /**
     * Clears the field and types the given text.
     * Waits for element visibility first to prevent StaleElementReferenceException.
     */
    protected void type(WebElement element, String text) {
        wait.waitForVisible(element);
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    /**
     * Returns trimmed visible text of an element.
     * Waits for the element to be visible before reading text.
     */
    protected String getText(WebElement element) {
        wait.waitForVisible(element);
        return element.getText().trim();
    }

    /**
     * Selects a dropdown option by its visible text label.
     * Works with HTML <select> elements only.
     */
    protected void selectByVisibleText(WebElement dropdown, String text) {
        wait.waitForVisible(dropdown);
        new Select(dropdown).selectByVisibleText(text);
        log.debug("Selected '{}' from dropdown", text);
    }

    /**
     * Selects a dropdown option by its HTML value attribute.
     * e.g., <option value="IN">India</option>  →  selectByValue(dropdown, "IN")
     */
    protected void selectByValue(WebElement dropdown, String value) {
        wait.waitForVisible(dropdown);
        new Select(dropdown).selectByValue(value);
    }

    /**
     * Checks whether an element is currently visible on the page.
     * Returns false instead of throwing an exception when element is absent.
     * Use this for conditional logic: if (isDisplayed(errorMsg)) { ... }
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Scrolls the page so the element is in the visible viewport.
     * Useful for elements below the fold or in long tables.
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Clicks an element using JavaScript — bypasses CSS overlays or disabled states.
     * Use as a fallback when regular click() fails due to UI overlay issues.
     */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /** Returns the current browser tab's page title. */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /** Returns the current browser URL. */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}

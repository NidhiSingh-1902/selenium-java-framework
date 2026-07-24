package com.pages.redmine;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RedmineLoginPage extends BasePage {

    // Lotus theme uses rf_username/rf_password — standard Redmine uses username/password
    @FindBy(id = "rf_username")
    private WebElement usernameField;

    @FindBy(id = "rf_password")
    private WebElement passwordField;

    // Lotus submit: id="login-submit", name="commit"
    @FindBy(id = "login-submit")
    private WebElement loginButton;

    // Lotus theme renders flash errors with class rf_flash_error, not id flash_error
    @FindBy(css = ".rf_flash_error")
    private WebElement flashError;

    // Fallback: standard Redmine flash error (used on non-Lotus pages)
    private static final By FLASH_ERROR_STANDARD = By.id("flash_error");

    public RedmineLoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        wait.waitForVisible(usernameField);
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        jsClick(loginButton);
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> !d.getCurrentUrl().contains("/login"));
        log.info("Logged in as: {}", username);
    }

    public void loginExpectingError(String username, String password) {
        wait.waitForVisible(usernameField);
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        jsClick(loginButton);
        // Wait for page to reload and show the error message
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.findElements(By.cssSelector(".rf_flash_error")).isEmpty()
                         || !d.findElements(By.id("flash_error")).isEmpty());
        log.info("Submitted login with expected failure for: {}", username);
    }

    public String getFlashErrorText() {
        try {
            wait.waitForVisible(flashError);
            return flashError.getText().trim();
        } catch (Exception e) {
            // Fallback to standard Redmine flash element
            return driver.findElement(FLASH_ERROR_STANDARD).getText().trim();
        }
    }

    public boolean isFlashErrorDisplayed() {
        try {
            wait.waitForVisible(flashError);
            return true;
        } catch (Exception e) {
            // Try standard Redmine flash element as fallback
            return !driver.findElements(FLASH_ERROR_STANDARD).isEmpty();
        }
    }

    public boolean isPageLoaded() {
        try {
            wait.waitForVisible(usernameField);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

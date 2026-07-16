package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage — Page Object for https://www.saucedemo.com (login screen).
 *
 * Locators found using Chrome DevTools (F12 → inspect each element).
 * All actions (type, click, getText) are inherited from BasePage.
 */
public class LoginPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────
    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // ── Constructor ───────────────────────────────────────────
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────

    /** Types username and password then clicks Login button */
    public void login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
    }

    /** Clicks Login without entering any credentials */
    public void clickLoginWithoutCredentials() {
        click(loginButton);
    }

    /** Returns the error message text shown on bad login */
    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /** Returns true if the red error message box is visible */
    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }
}

package com.pages.redmine;

import com.framework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RedmineLoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(name = "login")
    private WebElement loginButton;

    @FindBy(id = "flash_error")
    private WebElement flashError;

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
        new WebDriverWait(driver, Duration.ofSeconds(15))
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
    }

    public String getFlashErrorText() {
        wait.waitForVisible(flashError);
        return flashError.getText().trim();
    }

    public boolean isFlashErrorDisplayed() {
        return isDisplayed(flashError);
    }

    public boolean isPageLoaded() {
        return isDisplayed(usernameField);
    }
}

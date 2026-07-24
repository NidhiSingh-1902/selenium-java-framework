package com.tests.redmine.crm;

import com.framework.base.RedmineBaseTest;
import com.pages.redmine.RedmineLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TC-CRM-LG — Redmine CRM login tests.
 * These tests run WITHOUT calling loginAsAdmin() first;
 * they exercise the login page itself.
 */
public class CrmLoginTest extends RedmineBaseTest {

    @Test(description = "TC-CRM-LG-01: Valid admin credentials redirect away from login page")
    public void validLoginRedirectsToHome() {
        RedmineLoginPage loginPage = new RedmineLoginPage(getDriver());
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        loginPage.login("admin", "admin123");

        Assert.assertFalse(getDriver().getCurrentUrl().contains("/login"),
                "Should have redirected away from login page after successful login");
        log.info("TC-CRM-LG-01 PASSED — valid login redirected to: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-LG-02: Invalid password shows flash error on login page")
    public void invalidPasswordShowsError() {
        RedmineLoginPage loginPage = new RedmineLoginPage(getDriver());
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        loginPage.loginExpectingError("admin", "wrongpassword");

        Assert.assertTrue(loginPage.isFlashErrorDisplayed(),
                "Flash error should be visible after invalid login");
        log.info("TC-CRM-LG-02 PASSED — invalid login shows error: {}", loginPage.getFlashErrorText());
    }

    @Test(description = "TC-CRM-LG-03: Non-existent user shows flash error")
    public void nonExistentUserShowsError() {
        RedmineLoginPage loginPage = new RedmineLoginPage(getDriver());
        loginPage.loginExpectingError("nosuchuser", "password123");

        Assert.assertTrue(loginPage.isFlashErrorDisplayed(),
                "Flash error should be visible for unknown user");
        log.info("TC-CRM-LG-03 PASSED — unknown user shows error");
    }
}

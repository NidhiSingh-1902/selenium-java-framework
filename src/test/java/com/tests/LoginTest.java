package com.tests;

import com.framework.base.BaseTest;
import com.pages.LoginPage;
import com.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest — Tests for the SauceDemo login page.
 * URL: https://www.saucedemo.com
 *
 * SauceDemo provides these built-in test users:
 *   standard_user    / secret_sauce  → normal login
 *   locked_out_user  / secret_sauce  → blocked account
 *   problem_user     / secret_sauce  → UI issues after login
 *   performance_glitch_user / secret_sauce → slow login
 */
public class LoginTest extends BaseTest {

    // ── Test 1: Valid login ───────────────────────────────────
    @Test(priority = 1, description = "Valid credentials should open the Products page")
    public void validLoginTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");

        ProductsPage productsPage = new ProductsPage(getDriver());

        Assert.assertTrue(
            productsPage.isPageLoaded(),
            "Products page should load after valid login"
        );
        Assert.assertEquals(
            productsPage.getPageHeading(), "Products",
            "Page heading should be 'Products'"
        );
        log.info("Valid login test PASSED");
    }

    // ── Test 2: Wrong password ────────────────────────────────
    @Test(priority = 2, description = "Wrong password should show error message")
    public void invalidPasswordTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "wrong_password");

        Assert.assertTrue(
            loginPage.isErrorDisplayed(),
            "Error message should appear for wrong password"
        );
        Assert.assertTrue(
            loginPage.getErrorMessage().contains("Username and password do not match"),
            "Error text should mention credentials mismatch"
        );
        log.info("Invalid password test PASSED");
    }

    // ── Test 3: Locked out user ───────────────────────────────
    @Test(priority = 3, description = "Locked out user should see locked account error")
    public void lockedOutUserTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("locked_out_user", "secret_sauce");

        Assert.assertTrue(
            loginPage.isErrorDisplayed(),
            "Error message should appear for locked user"
        );
        Assert.assertTrue(
            loginPage.getErrorMessage().contains("locked out"),
            "Error should say user is locked out"
        );
        log.info("Locked out user test PASSED");
    }

    // ── Test 4: Empty username ────────────────────────────────
    @Test(priority = 4, description = "Empty username should show required field error")
    public void emptyUsernameTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("", "secret_sauce");

        Assert.assertTrue(
            loginPage.isErrorDisplayed(),
            "Error message should appear for empty username"
        );
        Assert.assertTrue(
            loginPage.getErrorMessage().contains("Username is required"),
            "Error should say username is required"
        );
        log.info("Empty username test PASSED");
    }

    // ── Test 5: Empty password ────────────────────────────────
    @Test(priority = 5, description = "Empty password should show required field error")
    public void emptyPasswordTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "");

        Assert.assertTrue(
            loginPage.isErrorDisplayed(),
            "Error message should appear for empty password"
        );
        Assert.assertTrue(
            loginPage.getErrorMessage().contains("Password is required"),
            "Error should say password is required"
        );
        log.info("Empty password test PASSED");
    }

    // ── Test 6: Logout ────────────────────────────────────────
    @Test(priority = 6, description = "User should be able to logout and return to login page")
    public void logoutTest() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");

        ProductsPage productsPage = new ProductsPage(getDriver());
        Assert.assertTrue(productsPage.isPageLoaded(), "Should be on Products page after login");

        productsPage.logout();

        Assert.assertEquals(
            getDriver().getCurrentUrl(), "https://www.saucedemo.com/",
            "Should redirect to login page after logout"
        );
        log.info("Logout test PASSED");
    }
}

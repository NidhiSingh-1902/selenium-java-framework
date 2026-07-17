package com.tests;

import com.framework.base.BaseTest;
import com.pages.CartPage;
import com.pages.CheckoutStep1Page;
import com.pages.LoginPage;
import com.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CheckoutStep1Test — Tests for SauceDemo Checkout Step 1 (customer info form).
 * URL: https://www.saucedemo.com/checkout-step-one.html
 *
 * This page is reached after clicking Checkout from the cart.
 * It presents a three-field form: First Name, Last Name, Zip Code.
 *
 * Test cases covered (TC-C1-01 to TC-C1-07):
 *   01  Page loads correctly from cart checkout button
 *   02  Submitting all fields correctly advances to step 2
 *   03  Continuing with an empty form shows a validation error
 *   04  Continuing without First Name shows an error
 *   05  Continuing without Last Name shows an error
 *   06  Continuing without Zip Code shows an error
 *   07  Cancel button returns to the cart page
 *
 * Pattern: Arrange → Act → Assert
 */
public class CheckoutStep1Test extends BaseTest {

    // ─────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs in, adds a product, opens the cart, and returns a CartPage.
     * All tests in this class need at least one item in cart to proceed.
     */
    private CartPage loginAndOpenCart() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");
        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.addToCart(0);
        productsPage.goToCart();
        return new CartPage(getDriver());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C1-01: Page loads correctly
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking Checkout from the cart page should navigate to
     * /checkout-step-one.html and show the "Checkout: Your Information" heading.
     */
    @Test(priority = 1, description = "TC-C1-01: Checkout page should load after clicking Checkout in cart")
    public void checkoutPageLoadsTest() {
        // Arrange
        CartPage cartPage = loginAndOpenCart();

        // Act
        cartPage.clickCheckout();

        // Assert
        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());
        Assert.assertTrue(
                step1.isPageLoaded(),
                "Checkout step-one page should load (URL should contain 'checkout-step-one')"
        );
        Assert.assertEquals(
                step1.getPageHeading(), "Checkout: Your Information",
                "Page heading should be 'Checkout: Your Information'"
        );
        log.info("TC-C1-01 PASSED — Checkout step 1 loaded: '{}'", step1.getPageHeading());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C1-02: Valid form submission advances to step 2
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Filling all three fields and clicking Continue should navigate
     * to checkout step 2 (/checkout-step-two.html).
     */
    @Test(priority = 2, description = "TC-C1-02: Valid form submission should advance to checkout step 2")
    public void validFormAdvancesToStep2Test() {
        // Arrange
        CartPage cartPage = loginAndOpenCart();
        cartPage.clickCheckout();
        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());

        // Act — fill all required fields and continue
        step1.fillFormAndContinue("John", "Doe", "12345");

        // Assert
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("checkout-step-two"),
                "URL should contain 'checkout-step-two' after valid form submission"
        );
        log.info("TC-C1-02 PASSED — Advanced to step 2: {}", getDriver().getCurrentUrl());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C1-03: Empty form shows validation error
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking Continue without filling any fields should show
     * a validation error and keep the user on the same page.
     *
     * SauceDemo shows: "Error: First Name is required"
     */
    @Test(priority = 3, description = "TC-C1-03: Submitting empty form should show a validation error")
    public void emptyFormShowsErrorTest() {
        // Arrange
        CartPage cartPage = loginAndOpenCart();
        cartPage.clickCheckout();
        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());

        // Act — click Continue without filling anything
        step1.clickContinueEmpty();

        // Assert — error is shown and page stays on step 1
        Assert.assertTrue(
                step1.isErrorDisplayed(),
                "Error message should be visible after submitting empty form"
        );
        Assert.assertTrue(
                step1.getErrorMessage().toLowerCase().contains("first name"),
                "Error message should mention 'First Name' when it is empty"
        );
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("checkout-step-one"),
                "User should remain on step-one page after validation error"
        );
        log.info("TC-C1-03 PASSED — Error shown: '{}'", step1.getErrorMessage());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C1-04: Missing First Name shows error
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Filling Last Name and Zip Code but leaving First Name empty
     * should show a validation error that specifically mentions "First Name".
     */
    @Test(priority = 4, description = "TC-C1-04: Missing First Name should show 'First Name is required' error")
    public void missingFirstNameShowsErrorTest() {
        // Arrange
        CartPage cartPage = loginAndOpenCart();
        cartPage.clickCheckout();
        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());

        // Act — fill Last Name and Zip only, leave First Name blank
        // Use submitForm (not fillFormAndContinue) — validation keeps us on step-one, no URL change
        step1.submitForm("", "Doe", "12345");

        // Assert — error mentions First Name
        Assert.assertTrue(
                step1.isErrorDisplayed(),
                "Error message should appear when First Name is empty"
        );
        Assert.assertTrue(
                step1.getErrorMessage().toLowerCase().contains("first name"),
                "Error should mention 'First Name'"
        );
        log.info("TC-C1-04 PASSED — Error for missing first name: '{}'", step1.getErrorMessage());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C1-05: Missing Last Name shows error
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Providing First Name and Zip Code but leaving Last Name empty
     * should show a validation error mentioning "Last Name".
     */
    @Test(priority = 5, description = "TC-C1-05: Missing Last Name should show 'Last Name is required' error")
    public void missingLastNameShowsErrorTest() {
        // Arrange
        CartPage cartPage = loginAndOpenCart();
        cartPage.clickCheckout();
        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());

        // Act — fill First Name and Zip only, leave Last Name blank
        step1.submitForm("John", "", "12345");

        // Assert
        Assert.assertTrue(
                step1.isErrorDisplayed(),
                "Error message should appear when Last Name is empty"
        );
        Assert.assertTrue(
                step1.getErrorMessage().toLowerCase().contains("last name"),
                "Error should mention 'Last Name'"
        );
        log.info("TC-C1-05 PASSED — Error for missing last name: '{}'", step1.getErrorMessage());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C1-06: Missing Zip Code shows error
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Providing First Name and Last Name but leaving Zip Code empty
     * should show a validation error mentioning "Postal Code".
     */
    @Test(priority = 6, description = "TC-C1-06: Missing Zip Code should show 'Postal Code is required' error")
    public void missingZipCodeShowsErrorTest() {
        // Arrange
        CartPage cartPage = loginAndOpenCart();
        cartPage.clickCheckout();
        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());

        // Act — fill First Name and Last Name only, leave Zip blank
        step1.submitForm("John", "Doe", "");

        // Assert
        Assert.assertTrue(
                step1.isErrorDisplayed(),
                "Error message should appear when Zip Code is empty"
        );
        Assert.assertTrue(
                step1.getErrorMessage().toLowerCase().contains("postal code"),
                "Error should mention 'Postal Code'"
        );
        log.info("TC-C1-06 PASSED — Error for missing zip: '{}'", step1.getErrorMessage());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C1-07: Cancel returns to cart page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking Cancel on the checkout step-one page should return
     * the user to the cart page (/cart.html) without losing cart contents.
     */
    @Test(priority = 7, description = "TC-C1-07: Cancel button should return to cart page")
    public void cancelReturnsToCartTest() {
        // Arrange
        CartPage cartPage = loginAndOpenCart();
        cartPage.clickCheckout();
        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());

        // Confirm we're on step 1
        Assert.assertTrue(step1.isPageLoaded(),
                "Pre-condition: should be on checkout step-one");

        // Act — click Cancel
        step1.clickCancel();

        // Assert — back on cart page
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("cart.html"),
                "URL should contain 'cart.html' after clicking Cancel"
        );
        CartPage backOnCart = new CartPage(getDriver());
        Assert.assertTrue(
                backOnCart.isPageLoaded(),
                "Cart page should load after clicking Cancel"
        );
        log.info("TC-C1-07 PASSED — Returned to cart page: {}", getDriver().getCurrentUrl());
    }
}

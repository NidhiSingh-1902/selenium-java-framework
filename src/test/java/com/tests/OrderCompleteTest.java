package com.tests;

import com.framework.base.BaseTest;
import com.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * OrderCompleteTest — Tests for the SauceDemo order confirmation page.
 * URL: https://www.saucedemo.com/checkout-complete.html
 *
 * This page is reached after clicking "Finish" on Checkout Step 2.
 * It confirms a successful order with a thank-you message, a Pony Express
 * image, and a "Back Home" button that returns to the products page.
 *
 * Test cases (TC-OC-01 to TC-OC-05):
 *   01  Page loads with heading "Checkout: Complete!"
 *   02  "Thank you for your order!" message is displayed
 *   03  Pony Express confirmation image is visible
 *   04  "Back Home" button is visible on the page
 *   05  Clicking "Back Home" returns to the products page
 *
 * Pattern: Arrange → Act → Assert
 */
public class OrderCompleteTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs in, adds one product, completes all checkout steps, and returns
     * a ready OrderCompletePage for each test to assert against.
     */
    private OrderCompletePage loginAndCompleteOrder() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.addToCart(0);
        productsPage.goToCart();

        CartPage cartPage = new CartPage(getDriver());
        cartPage.clickCheckout();

        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());
        step1.fillFormAndContinue("John", "Doe", "12345");

        CheckoutStep2Page step2 = new CheckoutStep2Page(getDriver());
        step2.clickFinish();

        return new OrderCompletePage(getDriver());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-OC-01: Page loads with correct heading
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After clicking Finish on step 2, the browser should navigate
     * to /checkout-complete.html with heading "Checkout: Complete!".
     */
    @Test(priority = 1, description = "TC-OC-01: Order complete page should load with 'Checkout: Complete!' heading")
    public void orderCompletePageLoadsTest() {
        // Arrange + Act
        OrderCompletePage orderComplete = loginAndCompleteOrder();

        // Assert
        Assert.assertTrue(
                orderComplete.isPageLoaded(),
                "Order complete page should be loaded (URL contains 'checkout-complete')"
        );
        Assert.assertEquals(
                orderComplete.getPageHeading(), "Checkout: Complete!",
                "Page heading should be 'Checkout: Complete!'"
        );
        log.info("TC-OC-01 PASSED — Order complete page loaded: '{}'", orderComplete.getPageHeading());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-OC-02: Thank-you message is displayed
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The primary confirmation message "Thank you for your order!"
     * should be visible and contain the expected text.
     */
    @Test(priority = 2, description = "TC-OC-02: 'Thank you for your order!' message should be displayed")
    public void thankYouMessageDisplayedTest() {
        // Arrange + Act
        OrderCompletePage orderComplete = loginAndCompleteOrder();

        // Assert
        Assert.assertTrue(
                orderComplete.isCompletionHeaderDisplayed(),
                "Completion header should be visible"
        );
        Assert.assertEquals(
                orderComplete.getCompletionHeader(), "Thank you for your order!",
                "Completion header text should be 'Thank you for your order!'"
        );
        log.info("TC-OC-02 PASSED — Completion header: '{}'", orderComplete.getCompletionHeader());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-OC-03: Pony Express image is visible
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The Pony Express delivery illustration (.pony_express) should
     * be visible on the confirmation page.
     */
    @Test(priority = 3, description = "TC-OC-03: Pony Express confirmation image should be visible")
    public void ponyExpressImageDisplayedTest() {
        // Arrange + Act
        OrderCompletePage orderComplete = loginAndCompleteOrder();

        // Assert
        Assert.assertTrue(
                orderComplete.isPonyExpressImageDisplayed(),
                "Pony Express confirmation image should be visible on the order complete page"
        );
        log.info("TC-OC-03 PASSED — Pony Express image is displayed");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-OC-04: Back Home button is visible
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The "Back Home" button should be present and visible
     * so the user has a clear path back to the products page.
     */
    @Test(priority = 4, description = "TC-OC-04: 'Back Home' button should be visible on the order complete page")
    public void backHomeButtonVisibleTest() {
        // Arrange + Act
        OrderCompletePage orderComplete = loginAndCompleteOrder();

        // Assert
        Assert.assertTrue(
                orderComplete.isBackHomeButtonDisplayed(),
                "Back Home button should be visible on the order complete page"
        );
        log.info("TC-OC-04 PASSED — Back Home button is visible");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-OC-05: Back Home returns to products page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "Back Home" should navigate back to the products /
     * inventory page (/inventory.html).
     */
    @Test(priority = 5, description = "TC-OC-05: 'Back Home' button should return to the products page")
    public void backHomeReturnsToProductsTest() {
        // Arrange
        OrderCompletePage orderComplete = loginAndCompleteOrder();

        // Act
        orderComplete.clickBackHome();

        // Assert — back on products page
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("inventory.html"),
                "URL should contain 'inventory.html' after clicking Back Home"
        );
        ProductsPage productsPage = new ProductsPage(getDriver());
        Assert.assertTrue(
                productsPage.isPageLoaded(),
                "Products page should load after clicking Back Home"
        );
        log.info("TC-OC-05 PASSED — Returned to products: {}", getDriver().getCurrentUrl());
    }
}

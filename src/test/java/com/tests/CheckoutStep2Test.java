package com.tests;

import com.framework.base.BaseTest;
import com.pages.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CheckoutStep2Test — Tests for SauceDemo Checkout Step 2 (order overview).
 * URL: https://www.saucedemo.com/checkout-step-two.html
 *
 * This page is reached after filling the customer info form in step 1.
 * It shows the complete order summary before the user confirms or cancels.
 *
 * Test cases (TC-C2-01 to TC-C2-06):
 *   01  Page loads with heading "Checkout: Overview"
 *   02  Cart items are visible in the overview
 *   03  Subtotal, tax, and total price labels are displayed
 *   04  Finish button navigates to the order complete page
 *   05  Cancel button returns to the products page
 *   06  Total price equals subtotal + tax
 *
 * Pattern: Arrange → Act → Assert
 */
public class CheckoutStep2Test extends BaseTest {

    // ─────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs in, adds one product, completes checkout step 1, and returns
     * a ready CheckoutStep2Page for each test to assert against.
     */
    private CheckoutStep2Page loginAndProceedToStep2() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");

        ProductsPage productsPage = new ProductsPage(getDriver());
        productsPage.addToCart(0);
        productsPage.goToCart();

        CartPage cartPage = new CartPage(getDriver());
        cartPage.clickCheckout();

        CheckoutStep1Page step1 = new CheckoutStep1Page(getDriver());
        step1.fillFormAndContinue("John", "Doe", "12345");

        return new CheckoutStep2Page(getDriver());
    }

    /**
     * Extracts the numeric dollar amount from a price label.
     * e.g. "Item total: $29.99" → 29.99, "Tax: $2.40" → 2.40
     */
    private double parsePriceFromLabel(String labelText) {
        return Double.parseDouble(labelText.replaceAll("[^0-9.]", ""));
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C2-01: Page loads with correct heading
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After submitting the customer info form, the browser should
     * navigate to /checkout-step-two.html and display "Checkout: Overview".
     */
    @Test(priority = 1, description = "TC-C2-01: Checkout step 2 page should load with correct heading")
    public void checkoutStep2PageLoadsTest() {
        // Arrange + Act
        CheckoutStep2Page step2 = loginAndProceedToStep2();

        // Assert
        Assert.assertTrue(
                step2.isPageLoaded(),
                "Checkout step-two URL and heading should be loaded"
        );
        Assert.assertEquals(
                step2.getPageHeading(), "Checkout: Overview",
                "Page heading should be 'Checkout: Overview'"
        );
        log.info("TC-C2-01 PASSED — Step 2 loaded: '{}'", step2.getPageHeading());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C2-02: Cart items visible in overview
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The item(s) added to the cart should appear in the order summary
     * table on checkout step 2.
     */
    @Test(priority = 2, description = "TC-C2-02: Cart items should be visible in the order overview")
    public void cartItemsVisibleInOverviewTest() {
        // Arrange + Act
        CheckoutStep2Page step2 = loginAndProceedToStep2();

        // Assert
        int itemCount = step2.getItemCount();
        Assert.assertTrue(
                itemCount > 0,
                "At least one cart item should be visible in the order overview"
        );
        log.info("TC-C2-02 PASSED — {} item(s) visible in overview, first item: '{}'",
                itemCount, step2.getItemName(0));
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C2-03: Price labels are displayed
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The subtotal, tax, and total price summary labels should all
     * be visible and non-empty in the order overview.
     */
    @Test(priority = 3, description = "TC-C2-03: Subtotal, tax, and total price labels should be displayed")
    public void priceLabelsDisplayedTest() {
        // Arrange + Act
        CheckoutStep2Page step2 = loginAndProceedToStep2();

        // Assert — each label is visible
        Assert.assertTrue(
                step2.isSubtotalDisplayed(),
                "Subtotal label should be visible in the order overview"
        );
        Assert.assertTrue(
                step2.isTaxDisplayed(),
                "Tax label should be visible in the order overview"
        );
        Assert.assertTrue(
                step2.isTotalDisplayed(),
                "Total label should be visible in the order overview"
        );
        log.info("TC-C2-03 PASSED — Subtotal: '{}', Tax: '{}', Total: '{}'",
                step2.getSubtotalText(), step2.getTaxText(), step2.getTotalText());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C2-04: Finish button navigates to order complete page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking Finish should confirm the order and navigate to
     * the order complete / thank-you page (/checkout-complete.html).
     */
    @Test(priority = 4, description = "TC-C2-04: Finish button should navigate to the order complete page")
    public void finishButtonNavigatesToOrderCompleteTest() {
        // Arrange
        CheckoutStep2Page step2 = loginAndProceedToStep2();

        // Act
        step2.clickFinish();

        // Assert
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("checkout-complete"),
                "URL should contain 'checkout-complete' after clicking Finish"
        );
        log.info("TC-C2-04 PASSED — Navigated to: {}", getDriver().getCurrentUrl());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C2-05: Cancel returns to the products page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking Cancel on step 2 should discard the checkout process
     * and return the user to the products / inventory page.
     */
    @Test(priority = 5, description = "TC-C2-05: Cancel button should return to the products page")
    public void cancelReturnsToProductsTest() {
        // Arrange
        CheckoutStep2Page step2 = loginAndProceedToStep2();

        // Act
        step2.clickCancel();

        // Assert
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("inventory.html"),
                "URL should contain 'inventory.html' after clicking Cancel on step 2"
        );
        log.info("TC-C2-05 PASSED — Returned to products: {}", getDriver().getCurrentUrl());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-C2-06: Total equals subtotal + tax
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The grand total displayed on the order overview should equal
     * the item subtotal plus the calculated tax — no rounding gap larger than $0.01.
     */
    @Test(priority = 6, description = "TC-C2-06: Total price should equal subtotal plus tax")
    public void totalEqualsSubtotalPlusTaxTest() {
        // Arrange + Act
        CheckoutStep2Page step2 = loginAndProceedToStep2();

        double subtotal = parsePriceFromLabel(step2.getSubtotalText());
        double tax      = parsePriceFromLabel(step2.getTaxText());
        double total    = parsePriceFromLabel(step2.getTotalText());

        // Assert — total == subtotal + tax within floating-point rounding tolerance
        Assert.assertEquals(
                total, subtotal + tax, 0.01,
                String.format("Total (%.2f) should equal subtotal (%.2f) + tax (%.2f)",
                        total, subtotal, tax)
        );
        log.info(String.format("TC-C2-06 PASSED — Total=%.2f, Subtotal=%.2f, Tax=%.2f", total, subtotal, tax));
    }
}

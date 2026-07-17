package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * CheckoutStep1Page — Page Object for the first checkout step (customer info form).
 * URL: https://www.saucedemo.com/checkout-step-one.html
 *
 * This page collects First Name, Last Name, and Zip/Postal Code before
 * the user can proceed to the order summary (step 2).
 *
 * How to reach this page:
 *   cartPage.clickCheckout();
 *   CheckoutStep1Page step1Page = new CheckoutStep1Page(getDriver());
 */
public class CheckoutStep1Page extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────

    /** Page heading — should read "Checkout: Your Information" */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /** First Name input field */
    @FindBy(id = "first-name")
    private WebElement firstNameField;

    /** Last Name input field */
    @FindBy(id = "last-name")
    private WebElement lastNameField;

    /** Zip / Postal Code input field */
    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    /**
     * "Continue" button — submits the form and advances to step 2.
     * Only enabled when all fields are filled.
     */
    @FindBy(id = "continue")
    private WebElement continueButton;

    /** "Cancel" button — returns to the cart page */
    @FindBy(id = "cancel")
    private WebElement cancelButton;

    /**
     * Error message container shown when form validation fails.
     * Appears when "Continue" is clicked with missing or invalid fields.
     */
    @FindBy(css = ".error-message-container")
    private WebElement errorContainer;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────

    public CheckoutStep1Page(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // Page state methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns true if the checkout step-one page has loaded.
     * Checks heading visibility and URL.
     */
    public boolean isPageLoaded() {
        return isDisplayed(pageTitle) && driver.getCurrentUrl().contains("checkout-step-one");
    }

    /** Returns the page heading text — should be "Checkout: Your Information" */
    public String getPageHeading() {
        return getText(pageTitle);
    }

    // ─────────────────────────────────────────────────────────────
    // Form interaction methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Fills all three fields and clicks Continue — the happy path.
     * Waits for the URL to advance to checkout-step-two before returning.
     * Only call this when all fields are non-empty and valid.
     *
     * @param firstName  Customer first name
     * @param lastName   Customer last name
     * @param postalCode Customer zip/postal code
     */
    public void fillFormAndContinue(String firstName, String lastName, String postalCode) {
        typeReact(firstNameField, firstName);
        typeReact(lastNameField, lastName);
        typeReact(postalCodeField, postalCode);
        jsClick(continueButton);
        wait.waitForUrlContains("checkout-step-two");
        log.info("Filled form and advanced to step 2 — first={}, last={}, zip={}", firstName, lastName, postalCode);
    }

    /**
     * Types values into the form fields (use "" to leave a field empty) and clicks
     * Continue — but does NOT wait for URL navigation.
     *
     * WHY a separate method?
     *   When a required field is empty, SauceDemo shows a validation error and stays
     *   on step-one. waitForUrlContains("checkout-step-two") would timeout in that case.
     *   This method is used by validation tests (TC-C1-03 through TC-C1-06) that need
     *   to verify the error without triggering a URL wait timeout.
     *
     * @param firstName  Customer first name (use "" to leave blank)
     * @param lastName   Customer last name (use "" to leave blank)
     * @param postalCode Customer zip/postal code (use "" to leave blank)
     */
    public void submitForm(String firstName, String lastName, String postalCode) {
        typeReact(firstNameField, firstName);
        typeReact(lastNameField, lastName);
        typeReact(postalCodeField, postalCode);
        jsClick(continueButton);
        log.info("Submitted form — first='{}', last='{}', zip='{}'", firstName, lastName, postalCode);
    }

    /**
     * Clicks Continue without filling any fields.
     * Shorthand for submitForm("", "", "") — used to test all-empty validation.
     */
    public void clickContinueEmpty() {
        jsClick(continueButton);
        log.info("Clicked Continue with empty form");
    }

    /**
     * Returns true if the error message container is visible.
     * Used to verify that validation errors are displayed.
     */
    public boolean isErrorDisplayed() {
        return isDisplayed(errorContainer);
    }

    /**
     * Returns the error message text shown after a failed form submission.
     * Example: "Error: First Name is required"
     */
    public String getErrorMessage() {
        return getText(errorContainer);
    }

    /**
     * Clicks Cancel to return to the cart page.
     * Uses jsClick for reliable React event handling.
     */
    public void clickCancel() {
        wait.waitForVisible(cancelButton);
        jsClick(cancelButton);
        wait.waitForUrlContains("cart.html");
        log.info("Clicked Cancel — returned to cart page");
    }

    /**
     * Returns the cart badge count on this page.
     * The badge persists across checkout steps — verifies cart state is maintained.
     */
    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        if (badges.isEmpty()) return 0;
        return Integer.parseInt(badges.get(0).getText().trim());
    }
}

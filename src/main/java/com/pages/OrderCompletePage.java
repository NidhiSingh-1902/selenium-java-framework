package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * OrderCompletePage — Page Object for the order confirmation screen.
 * URL: https://www.saucedemo.com/checkout-complete.html
 *
 * Reached by clicking "Finish" on Checkout Step 2.
 * Shows a thank-you message, a Pony Express image, and a "Back Home" button.
 *
 * How to reach this page:
 *   step2Page.clickFinish();
 *   OrderCompletePage orderComplete = new OrderCompletePage(getDriver());
 */
public class OrderCompletePage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────

    /** Page heading — should read "Checkout: Complete!" */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /**
     * Primary confirmation message — "Thank you for your order!"
     * CSS class: .complete-header
     */
    @FindBy(css = ".complete-header")
    private WebElement completionHeader;

    /**
     * Supporting confirmation text — describes the shipping dispatch.
     * CSS class: .complete-text
     */
    @FindBy(css = ".complete-text")
    private WebElement completionText;

    /**
     * Pony Express confirmation image shown on successful order.
     * CSS class: .pony_express
     */
    @FindBy(css = ".pony_express")
    private WebElement ponyExpressImage;

    /**
     * "Back Home" button — returns to the products / inventory page.
     * Maps to id="back-to-products" in the DOM.
     */
    @FindBy(id = "back-to-products")
    private WebElement backHomeButton;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────

    public OrderCompletePage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // Page state
    // ─────────────────────────────────────────────────────────────

    /** Returns true if the order complete page has loaded (heading visible + URL matches). */
    public boolean isPageLoaded() {
        return isDisplayed(pageTitle) && driver.getCurrentUrl().contains("checkout-complete");
    }

    /** Returns the page heading text — should be "Checkout: Complete!". */
    public String getPageHeading() {
        return getText(pageTitle);
    }

    /** Returns the primary confirmation header text — "Thank you for your order!". */
    public String getCompletionHeader() {
        return getText(completionHeader);
    }

    /** Returns the dispatch confirmation sub-text. */
    public String getCompletionText() {
        return getText(completionText);
    }

    /** Returns true if the thank-you message header is visible. */
    public boolean isCompletionHeaderDisplayed() {
        return isDisplayed(completionHeader);
    }

    /** Returns true if the Pony Express confirmation image is visible. */
    public boolean isPonyExpressImageDisplayed() {
        return isDisplayed(ponyExpressImage);
    }

    /** Returns true if the Back Home button is visible. */
    public boolean isBackHomeButtonDisplayed() {
        return isDisplayed(backHomeButton);
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────

    /**
     * Clicks the "Back Home" button to return to the products page.
     * Waits for navigation to inventory.html before returning.
     */
    public void clickBackHome() {
        wait.waitForVisible(backHomeButton);
        jsClick(backHomeButton);
        wait.waitForUrlContains("inventory.html");
        log.info("Clicked Back Home — returned to products page");
    }
}

package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * CheckoutStep2Page — Page Object for Checkout Step 2 (order overview).
 * URL: https://www.saucedemo.com/checkout-step-two.html
 *
 * This page shows the final order summary: cart items, subtotal, tax, and total.
 * From here the user can Finish (place the order) or Cancel (return to products).
 *
 * How to reach this page:
 *   step1Page.fillFormAndContinue("John", "Doe", "12345");
 *   CheckoutStep2Page step2 = new CheckoutStep2Page(getDriver());
 */
public class CheckoutStep2Page extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────

    /** Page heading — should read "Checkout: Overview" */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /** Each item row in the order summary */
    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    /** Product name within each cart item row */
    @FindBy(css = ".cart_item .inventory_item_name")
    private List<WebElement> itemNames;

    /** Unit price within each cart item row */
    @FindBy(css = ".cart_item .inventory_item_price")
    private List<WebElement> itemPrices;

    /**
     * Subtotal label — "Item total: $X.XX"
     * Excludes tax; sum of all item prices.
     */
    @FindBy(css = ".summary_subtotal_label")
    private WebElement subtotalLabel;

    /** Tax label — "Tax: $X.XX" */
    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;

    /**
     * Grand total label — "Total: $X.XX"
     * Should equal subtotal + tax.
     */
    @FindBy(css = ".summary_total_label")
    private WebElement totalLabel;

    /** "Finish" button — places the order and navigates to the order complete page */
    @FindBy(id = "finish")
    private WebElement finishButton;

    /** "Cancel" button — abandons checkout and returns to the products page */
    @FindBy(id = "cancel")
    private WebElement cancelButton;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────

    public CheckoutStep2Page(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // Page state
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns true if the checkout step-two page has loaded (heading visible + URL matches).
     */
    public boolean isPageLoaded() {
        return isDisplayed(pageTitle) && driver.getCurrentUrl().contains("checkout-step-two");
    }

    /** Returns the page heading text — should be "Checkout: Overview". */
    public String getPageHeading() {
        return getText(pageTitle);
    }

    // ─────────────────────────────────────────────────────────────
    // Item summary
    // ─────────────────────────────────────────────────────────────

    /** Returns the number of items shown in the order summary. */
    public int getItemCount() {
        return cartItems.size();
    }

    /** Returns the product name of the item at the given index. */
    public String getItemName(int index) {
        return getText(itemNames.get(index));
    }

    /** Returns the unit price text (e.g. "$29.99") of the item at the given index. */
    public String getItemPrice(int index) {
        return getText(itemPrices.get(index));
    }

    // ─────────────────────────────────────────────────────────────
    // Price summary
    // ─────────────────────────────────────────────────────────────

    /** Returns the full subtotal label text — e.g. "Item total: $9.99". */
    public String getSubtotalText() {
        return getText(subtotalLabel);
    }

    /** Returns the full tax label text — e.g. "Tax: $0.80". */
    public String getTaxText() {
        return getText(taxLabel);
    }

    /** Returns the full total label text — e.g. "Total: $10.79". */
    public String getTotalText() {
        return getText(totalLabel);
    }

    /** Returns true if the subtotal label is visible on the page. */
    public boolean isSubtotalDisplayed() {
        return isDisplayed(subtotalLabel);
    }

    /** Returns true if the tax label is visible on the page. */
    public boolean isTaxDisplayed() {
        return isDisplayed(taxLabel);
    }

    /** Returns true if the total label is visible on the page. */
    public boolean isTotalDisplayed() {
        return isDisplayed(totalLabel);
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────

    /**
     * Clicks Finish to place the order.
     * Waits for navigation to the order complete page (checkout-complete.html).
     */
    public void clickFinish() {
        wait.waitForVisible(finishButton);
        jsClick(finishButton);
        wait.waitForUrlContains("checkout-complete");
        log.info("Clicked Finish — advanced to order complete page");
    }

    /**
     * Clicks Cancel to abandon checkout.
     * Returns to the products / inventory page (inventory.html).
     */
    public void clickCancel() {
        wait.waitForVisible(cancelButton);
        jsClick(cancelButton);
        wait.waitForUrlContains("inventory.html");
        log.info("Clicked Cancel on step 2 — returned to products page");
    }

    /**
     * Returns the cart badge count displayed in the header on this page.
     * Used to verify that the cart badge persists across checkout steps.
     */
    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText().trim());
    }
}

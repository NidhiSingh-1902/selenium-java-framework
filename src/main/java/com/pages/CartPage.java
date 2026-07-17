package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CartPage — Page Object for the SauceDemo shopping cart page.
 * URL: https://www.saucedemo.com/cart.html
 *
 * This page opens when the user clicks the cart icon (top-right).
 * It lists all items currently in the cart with their name, price, quantity,
 * and a Remove button for each. Two navigation buttons are at the bottom:
 *   - "Continue Shopping" → returns to /inventory.html
 *   - "Checkout"          → advances to /checkout-step-one.html
 *
 * How to reach this page in a test:
 *   productsPage.addToCart(0);
 *   productsPage.goToCart();
 *   CartPage cartPage = new CartPage(getDriver());
 */
public class CartPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────

    /** Page heading — should read "Your Cart" */
    @FindBy(css = ".title")
    private WebElement pageTitle;

    /** Container holding all cart item rows */
    @FindBy(css = ".cart_list")
    private WebElement cartList;

    /** Each row in the cart list — one per added product */
    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    /** Product name elements inside the cart list */
    @FindBy(css = ".cart_item .inventory_item_name")
    private List<WebElement> itemNames;

    /** Price elements inside the cart list */
    @FindBy(css = ".cart_item .inventory_item_price")
    private List<WebElement> itemPrices;

    /** "Remove" buttons — one per cart item */
    @FindBy(css = ".cart_button")
    private List<WebElement> removeButtons;

    /**
     * "Continue Shopping" button at the bottom-left.
     * Navigates back to the products listing page.
     */
    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    /**
     * "Checkout" button at the bottom-right.
     * Proceeds to the first checkout step (customer info form).
     */
    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // Page state methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns true if the cart page has fully loaded.
     * Checks heading visibility and URL.
     */
    public boolean isPageLoaded() {
        return isDisplayed(pageTitle) && driver.getCurrentUrl().contains("cart.html");
    }

    /** Returns the page heading text — should be "Your Cart" */
    public String getPageHeading() {
        return getText(pageTitle);
    }

    // ─────────────────────────────────────────────────────────────
    // Cart item methods
    // ─────────────────────────────────────────────────────────────

    /** Returns the number of item rows in the cart */
    public int getCartItemCount() {
        return cartItems.size();
    }

    /**
     * Returns the name of the cart item at the given position.
     * @param index 0-based position in the cart list
     */
    public String getItemName(int index) {
        return getText(itemNames.get(index));
    }

    /** Returns names of ALL items in the cart as a list */
    public List<String> getAllItemNames() {
        return itemNames.stream().map(this::getText).collect(Collectors.toList());
    }

    /**
     * Returns the price of the cart item at the given position.
     * @param index 0-based position in the cart list
     */
    public String getItemPrice(int index) {
        return getText(itemPrices.get(index));
    }

    /**
     * Returns the cart badge count (same logic as ProductsPage — uses findElements).
     * Returns 0 if the badge is absent (empty cart).
     */
    public int getCartBadgeCount() {
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        if (badges.isEmpty()) return 0;
        return Integer.parseInt(badges.get(0).getText().trim());
    }

    // ─────────────────────────────────────────────────────────────
    // Cart item actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Clicks the "Remove" button for the cart item at the given index.
     * Waits for the cart item count to decrease before returning so that
     * getCartItemCount() reflects the new state immediately after this call.
     *
     * Uses jsClick — React cart pages need JS events, same as inventory buttons.
     *
     * @param index 0-based position in the cart list
     */
    public void removeItem(int index) {
        int expectedCount = cartItems.size() - 1;
        WebElement button = removeButtons.get(index);
        wait.waitForVisible(button);
        jsClick(button);
        // Re-query via By.cssSelector on each poll — stale-safe
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> driver.findElements(By.cssSelector(".cart_item")).size() == expectedCount);
        log.info("Removed cart item at index {} — {} item(s) remain", index, expectedCount);
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Clicks "Continue Shopping" to return to the products listing page.
     * Uses jsClick to ensure React's event handler fires.
     */
    public void continueShopping() {
        wait.waitForVisible(continueShoppingButton);
        jsClick(continueShoppingButton);
        wait.waitForUrlContains("inventory.html");
        log.info("Clicked Continue Shopping — returned to products page");
    }

    /**
     * Clicks "Checkout" to proceed to the customer information form.
     * Uses jsClick to ensure React's event handler fires.
     */
    public void clickCheckout() {
        wait.waitForVisible(checkoutButton);
        jsClick(checkoutButton);
        wait.waitForUrlContains("checkout-step-one");
        log.info("Clicked Checkout — navigated to checkout step one");
    }
}

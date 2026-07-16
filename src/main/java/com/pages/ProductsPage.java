package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductsPage — Page Object for the inventory/products screen after login.
 * URL: https://www.saucedemo.com/inventory.html
 *
 * This page shows all available products and lets the user:
 *   - Sort products by name or price
 *   - Add/remove products from the cart
 *   - Navigate to a product's detail page
 *   - Open the burger menu for logout and other options
 *
 * How to use in a test:
 *   ProductsPage productsPage = new ProductsPage(getDriver());
 *   productsPage.addToCart(0);   // adds first product
 */
public class ProductsPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // Each @FindBy tells Selenium how to find that element on the page.
    // These are found using Chrome DevTools (F12 → inspect element).
    // ─────────────────────────────────────────────────────────────

    /** The "Products" heading at the top of the page */
    @FindBy(className = "title")
    private WebElement pageTitle;

    /** The shopping cart icon in the top-right corner */
    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    /** The red number badge on the cart icon showing item count */
    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    /** The sort dropdown (Name A-Z, Name Z-A, Price low-high, Price high-low) */
    @FindBy(css = ".product_sort_container")
    private WebElement sortDropdown;

    /** All product name links on the page — 6 items on SauceDemo */
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    /** All product price labels — e.g. "$29.99" */
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;

    /**
     * All cart toggle buttons — each starts as "Add to cart".
     * After clicking, the same button becomes "Remove".
     * CSS class ".btn_inventory" is shared by both states.
     */
    @FindBy(css = ".btn_inventory")
    private List<WebElement> cartButtons;

    /** Burger / hamburger menu button (top-left) */
    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    /** Logout link inside the burger menu */
    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // Calls super(driver) which runs PageFactory.initElements — this
    // wires up all the @FindBy fields above automatically.
    // ─────────────────────────────────────────────────────────────

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // Page state methods
    // ─────────────────────────────────────────────────────────────

    /** Returns the page heading text — should be "Products" */
    public String getPageHeading() {
        return getText(pageTitle);
    }

    /**
     * Returns true if the Products page has fully loaded.
     * Checks both the heading element and the URL containing "inventory".
     */
    public boolean isPageLoaded() {
        return isDisplayed(pageTitle) && driver.getCurrentUrl().contains("inventory");
    }

    // ─────────────────────────────────────────────────────────────
    // Product listing methods
    // ─────────────────────────────────────────────────────────────

    /** Returns the total number of products visible on the page (should be 6) */
    public int getProductCount() {
        return productNames.size();
    }

    /**
     * Returns the name of a product at the given position.
     * @param index 0-based position (0 = first product on screen)
     */
    public String getProductName(int index) {
        return getText(productNames.get(index));
    }

    /**
     * Returns all product prices as a list of strings (e.g. ["$29.99", "$9.99", ...]).
     * Useful for verifying that every product has a price displayed.
     */
    public List<String> getAllProductPrices() {
        return productPrices.stream()
                .map(this::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns the price of the first product on the page.
     * Used after sorting to verify the sort order is correct.
     * Example: after sorting "Price (low to high)", first price should be "$7.99".
     */
    public String getFirstProductPrice() {
        return getText(productPrices.get(0));
    }

    // ─────────────────────────────────────────────────────────────
    // Sort methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Selects a sort option from the dropdown.
     * @param visibleText the exact text of the option to select. Valid values:
     *   "Name (A to Z)"       — alphabetical ascending (default)
     *   "Name (Z to A)"       — alphabetical descending
     *   "Price (low to high)" — cheapest first
     *   "Price (high to low)" — most expensive first
     */
    public void selectSortOption(String visibleText) {
        // selectByVisibleText is inherited from BasePage — works on <select> dropdowns
        selectByVisibleText(sortDropdown, visibleText);
        log.info("Sort option selected: {}", visibleText);
    }

    // ─────────────────────────────────────────────────────────────
    // Cart methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Clicks the "Add to cart" button for the product at the given index.
     * After clicking, the button text changes to "Remove".
     * @param index 0-based position matching the product's position on screen
     */
    public void addToCart(int index) {
        click(cartButtons.get(index));
        log.info("Clicked Add to cart for product at index {}", index);
    }

    /**
     * Clicks the "Remove" button for the product at the given index.
     * Only call this after the product has already been added to the cart,
     * otherwise the button still says "Add to cart" and you will add instead of remove.
     * @param index 0-based position matching the product's position on screen
     */
    public void removeFromCart(int index) {
        click(cartButtons.get(index));
        log.info("Clicked Remove for product at index {}", index);
    }

    /**
     * Returns the current text of the cart toggle button at the given index.
     * Returns "Add to cart" if the product is not in the cart.
     * Returns "Remove" if the product is currently in the cart.
     * Used in tests to verify the button state changed after an action.
     */
    public String getButtonText(int index) {
        return getText(cartButtons.get(index));
    }

    /**
     * Returns the number shown on the cart badge (e.g. 3 when 3 items are in cart).
     * Returns 0 when the badge is not visible (cart is empty).
     *
     * Why driver.findElements() instead of the @FindBy field?
     *   The badge element only exists in the DOM when the cart has items.
     *   Using driver.findElements() (plural) is safe — it never throws an exception,
     *   it just returns an empty list if not found. The @FindBy proxy would throw
     *   NoSuchElementException before isDisplayed() could even be checked.
     */
    public int getCartCount() {
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        if (badges.isEmpty()) return 0;
        return Integer.parseInt(badges.get(0).getText().trim());
    }

    /** Clicks the cart icon to navigate to the cart page (/cart.html) */
    public void goToCart() {
        click(cartIcon);
        log.info("Navigated to cart page");
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Opens the burger menu (☰) and clicks Logout.
     * After this call the browser returns to the login page.
     */
    public void logout() {
        click(menuButton);
        // Wait for the slide-in menu animation to finish before clicking logout
        wait.waitForVisible(logoutLink);
        click(logoutLink);
        log.info("Logged out from Products page");
    }
}

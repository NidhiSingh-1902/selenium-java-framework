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

    /** All product name text divs — used for reading the product name text */
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    /** All product price labels — e.g. "$29.99" */
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;

    /**
     * The <a> anchor links that wrap each product name.
     * SauceDemo marks these with data-test="item-4-title-link" (hyphen format).
     * Using data-test avoids the href="#" problem — we extract the product ID
     * from this attribute to construct the navigation URL.
     */
    @FindBy(css = "a[data-test$='-title-link']")
    private List<WebElement> productNameLinks;

    /**
     * The <a> anchor links that wrap each product thumbnail image.
     * SauceDemo marks these with data-test="item-4-img-link".
     */
    @FindBy(css = "a[data-test$='-img-link']")
    private List<WebElement> productImageLinks;

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

    /**
     * Returns the price of the product at a specific index.
     * @param index 0-based position (matches the corresponding product name position)
     * Used in tests to compare a product's price here vs on its detail page.
     */
    public String getProductPrice(int index) {
        return getText(productPrices.get(index));
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation to Product Detail Page
    // ─────────────────────────────────────────────────────────────

    /**
     * Navigates to a product's detail page by constructing the URL from data-test attribute.
     *
     * WHY data-test extraction instead of clicking or href?
     *   SauceDemo's React Router links render with href="#" — both getAttribute("href")
     *   and element.click() fail to navigate. The data-test attribute follows the pattern
     *   "item-4-title-link", so we extract the ID segment and build the absolute URL.
     *
     * @param index 0-based position of the product to open
     */
    public void clickProductName(int index) {
        WebElement link = productNameLinks.get(index);
        wait.waitForVisible(link);
        // data-test="item-4-title-link" → split("-") → [item, 4, title, link] → [1]="4"
        // This bypasses href="#" entirely — we build the absolute URL from the product ID.
        String dataTest = link.getAttribute("data-test");
        String productId = dataTest.split("-")[1];
        String productUrl = "https://www.saucedemo.com/inventory-item.html?id=" + productId;
        driver.get(productUrl);
        wait.waitForUrlContains("inventory-item");
        log.info("Navigated to product id={} at index {}", productId, index);
    }

    /**
     * Navigates to a product's detail page by its thumbnail image link.
     * Uses the same data-test ID extraction strategy as clickProductName.
     *
     * @param index 0-based position of the product image to open
     */
    public void clickProductImage(int index) {
        WebElement link = productImageLinks.get(index);
        wait.waitForVisible(link);
        // data-test="item-4-img-link" → split("-") → [item, 4, img, link] → [1]="4"
        String dataTest = link.getAttribute("data-test");
        String productId = dataTest.split("-")[1];
        String productUrl = "https://www.saucedemo.com/inventory-item.html?id=" + productId;
        driver.get(productUrl);
        wait.waitForUrlContains("inventory-item");
        log.info("Navigated via image to product id={} at index {}", productId, index);
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
     * Clicks "Add to cart" for the product at the given index.
     *
     * WHY jsClick + locator-based text wait?
     *   jsClick fires a native JS event that React's synthetic event system always
     *   handles — WebDriver's element.click() can fail on React pages where event
     *   delegation doesn't propagate as expected.
     *   After clicking, the button DOM node is replaced by React, so the old WebElement
     *   reference becomes stale. waitForTextInElement(By locator, text) re-finds the
     *   element on every poll, avoiding StaleElementReferenceException.
     *
     * @param index 0-based position matching the product's position on screen
     */
    public void addToCart(int index) {
        WebElement button = cartButtons.get(index);
        wait.waitForVisible(button);
        jsClick(button);
        // Target the Nth product's button specifically so the wait is correct even
        // when multiple products are already in the cart (first button already says "Remove")
        By nthButton = By.cssSelector(".inventory_item:nth-child(" + (index + 1) + ") .btn_inventory");
        wait.waitForTextInElement(nthButton, "Remove");
        log.info("Added product at index {} to cart", index);
    }

    /**
     * Clicks "Remove" for the product at the given index.
     * Uses jsClick and waits for the specific product's button to return to "Add to cart".
     * @param index 0-based position matching the product's position on screen
     */
    public void removeFromCart(int index) {
        WebElement button = cartButtons.get(index);
        wait.waitForVisible(button);
        jsClick(button);
        By nthButton = By.cssSelector(".inventory_item:nth-child(" + (index + 1) + ") .btn_inventory");
        wait.waitForTextInElement(nthButton, "Add to cart");
        log.info("Removed product at index {} from cart", index);
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

    /**
     * Clicks the cart icon to navigate to the cart page (/cart.html).
     * Uses jsClick + URL wait — same pattern as all React navigation actions.
     */
    public void goToCart() {
        wait.waitForVisible(cartIcon);
        jsClick(cartIcon);
        wait.waitForUrlContains("cart.html");
        log.info("Navigated to cart page");
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Opens the burger menu (☰) and clicks Logout.
     * After this call the browser returns to the login page.
     *
     * WHY delegate to BurgerMenuPage?
     *   The react-burger-menu requires jsClick() to open and aria-hidden polling
     *   to confirm the animation completed. BurgerMenuPage already encapsulates
     *   all of that logic; reusing it avoids duplicating the React-specific waits.
     */
    public void logout() {
        BurgerMenuPage burgerMenu = new BurgerMenuPage(driver);
        burgerMenu.openMenu();
        burgerMenu.clickLogout();
        log.info("Logged out from Products page");
    }
}

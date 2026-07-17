package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * ProductDetailPage — Page Object for the individual product detail screen.
 * URL: https://www.saucedemo.com/inventory-item.html?id=X
 *
 * This page opens when a user clicks a product name or image on the Products page.
 * It shows the product's full name, description, price, and image,
 * and allows the user to add/remove the product from the cart or go back.
 *
 * How to get here in a test:
 *   ProductsPage productsPage = new ProductsPage(getDriver());
 *   productsPage.clickProductName(0);            // navigate to first product's detail
 *   ProductDetailPage detailPage = new ProductDetailPage(getDriver());
 *   detailPage.addToCart();                      // add from the detail page
 */
public class ProductDetailPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // Found via Chrome DevTools (F12) on /inventory-item.html
    // ─────────────────────────────────────────────────────────────

    /** Full product name shown prominently on the detail page */
    @FindBy(css = ".inventory_details_name")
    private WebElement productName;

    /** Multi-line product description text below the name */
    @FindBy(css = ".inventory_details_desc")
    private WebElement productDescription;

    /** Product price shown on the detail page — format "$XX.XX" */
    @FindBy(css = ".inventory_details_price")
    private WebElement productPrice;

    /** Large product image shown on the detail page */
    @FindBy(css = ".inventory_details_img")
    private WebElement productImage;

    /**
     * The cart toggle button — starts as "Add to cart", changes to "Remove" when clicked.
     * Same behavior as on the Products page but there is only ONE button here
     * (only the current product, not a list).
     */
    @FindBy(css = ".btn_inventory")
    private WebElement cartButton;

    /** The "← Back to products" link at the top-left of the page */
    @FindBy(id = "back-to-products")
    private WebElement backButton;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // Page state methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns true if the Product Detail page has loaded correctly.
     * Uses an explicit wait for the product name element to be visible,
     * then confirms the URL contains "inventory-item".
     *
     * WHY explicit wait here instead of isDisplayed()?
     *   isDisplayed() has no wait — if called immediately after a page transition,
     *   the element might not yet be in the DOM and it returns false too early.
     *   wait.waitForVisible() holds for up to 15 seconds, handling any load delay.
     */
    public boolean isPageLoaded() {
        try {
            wait.waitForVisible(productName);
            return driver.getCurrentUrl().contains("inventory-item");
        } catch (Exception e) {
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Product info methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Returns the product name displayed on this detail page.
     * Used in tests to verify it matches what was shown on the Products listing page.
     */
    public String getProductName() {
        return getText(productName);
    }

    /**
     * Returns the product description text.
     * Each SauceDemo product has a unique description paragraph.
     */
    public String getProductDescription() {
        return getText(productDescription);
    }

    /**
     * Returns the product price shown on the detail page — e.g. "$29.99".
     * Used in tests to verify it matches the price on the Products listing page.
     */
    public String getProductPrice() {
        return getText(productPrice);
    }

    /**
     * Returns true if the large product image is displayed on the page.
     * Used to verify the image loaded correctly after navigating to the detail page.
     */
    public boolean isProductImageDisplayed() {
        return isDisplayed(productImage);
    }

    // ─────────────────────────────────────────────────────────────
    // Cart methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Clicks "Add to cart" on this product's detail page.
     *
     * WHY jsClick instead of click()?
     *   The detail page is loaded via driver.get() (full page reload), so React
     *   re-initializes fresh. In some Chrome/React combinations, WebDriver's
     *   synthetic element.click() does not trigger React's event handlers on a
     *   freshly-loaded page. jsClick() fires a native JS click event which is
     *   reliably processed by React's event system.
     */
    public void addToCart() {
        wait.waitForVisible(cartButton);
        jsClick(cartButton);
        // Wait for React to update the button text — confirms the click was processed
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.textToBePresentInElement(cartButton, "Remove"));
        log.info("Added product to cart from detail page");
    }

    /**
     * Clicks "Remove" on this product's detail page.
     * Only call this after addToCart() — otherwise it will add instead of remove.
     * Uses jsClick for the same reason as addToCart().
     */
    public void removeFromCart() {
        wait.waitForVisible(cartButton);
        jsClick(cartButton);
        // Wait for React to reset the button text back to "Add to cart"
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.textToBePresentInElement(cartButton, "Add to cart"));
        log.info("Removed product from cart on detail page");
    }

    /**
     * Returns the current text of the cart button.
     * Returns "Add to cart" when the product is not in cart.
     * Returns "Remove" when the product has been added.
     * Used to verify the button state changed after add/remove action.
     */
    public String getCartButtonText() {
        return getText(cartButton);
    }

    /**
     * Returns the number shown on the cart badge icon.
     * Returns 0 when the cart is empty (no badge visible).
     *
     * Uses driver.findElements() (plural) — safe approach that never throws.
     * The badge only exists in the DOM when there are items in the cart.
     */
    public int getCartCount() {
        List<WebElement> badges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        if (badges.isEmpty()) return 0;
        return Integer.parseInt(badges.get(0).getText().trim());
    }

    // ─────────────────────────────────────────────────────────────
    // Navigation methods
    // ─────────────────────────────────────────────────────────────

    /**
     * Navigates back to the products listing page by clicking the Back button.
     *
     * WHY jsClick instead of click()?
     *   #back-to-products is a <button> element with a React onClick handler.
     *   The detail page is loaded via driver.get() (full page reload), and on a
     *   freshly-loaded React page WebDriver's synthetic click does not reliably
     *   trigger React event handlers. jsClick() fires a native JS click that React
     *   always handles correctly.
     */
    public void goBackToProducts() {
        wait.waitForVisible(backButton);
        jsClick(backButton);
        wait.waitForUrlContains("inventory.html");
        log.info("Navigated back to products page via back button click");
    }
}

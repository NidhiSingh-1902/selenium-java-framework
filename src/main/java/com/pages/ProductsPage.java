package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * ProductsPage — Page Object for the inventory/products screen after login.
 * URL: https://www.saucedemo.com/inventory.html
 */
public class ProductsPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────
    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;

    @FindBy(css = ".btn_inventory")
    private List<WebElement> addToCartButtons;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    // ── Constructor ───────────────────────────────────────────
    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    // ── Actions ───────────────────────────────────────────────

    /** Returns the page heading text — should be "Products" */
    public String getPageHeading() {
        return getText(pageTitle);
    }

    /** Returns true if the Products page has loaded correctly */
    public boolean isPageLoaded() {
        return isDisplayed(pageTitle) && driver.getCurrentUrl().contains("inventory");
    }

    /** Returns the number of products listed on the page */
    public int getProductCount() {
        return productNames.size();
    }

    /** Returns the name of a product by its position (0-based index) */
    public String getProductName(int index) {
        return getText(productNames.get(index));
    }

    /** Clicks "Add to cart" for the product at the given index */
    public void addToCart(int index) {
        click(addToCartButtons.get(index));
        log.info("Added product at index {} to cart", index);
    }

    /** Returns the number shown on the cart icon badge */
    public int getCartCount() {
        if (!isDisplayed(cartBadge)) return 0;
        return Integer.parseInt(getText(cartBadge));
    }

    /** Clicks the cart icon to go to the cart page */
    public void goToCart() {
        click(cartIcon);
    }

    /** Opens the burger menu and clicks Logout */
    public void logout() {
        click(menuButton);
        wait.waitForVisible(logoutLink);
        click(logoutLink);
        log.info("Logged out successfully");
    }
}

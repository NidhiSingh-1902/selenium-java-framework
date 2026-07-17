package com.pages;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BurgerMenuPage — Page Object for the SauceDemo hamburger navigation menu.
 *
 * The burger menu is a slide-in overlay available on the Products, Cart, and
 * Checkout pages. It is implemented with the react-burger-menu library, which
 * toggles the .bm-menu-wrap container's aria-hidden attribute when opened or
 * closed.
 *
 * Usage:
 *   BurgerMenuPage menu = new BurgerMenuPage(getDriver()); // from any page with the menu
 *   menu.openMenu();
 *   menu.clickLogout();
 *
 * WHY a separate page object?
 *   The menu is a distinct UI component with its own locators and interaction
 *   logic. Keeping it in its own class avoids polluting every page object with
 *   menu-related methods.
 */
public class BurgerMenuPage extends BasePage {

    // ─────────────────────────────────────────────────────────────
    // Locators
    // ─────────────────────────────────────────────────────────────

    /** Hamburger icon button — click to open the side menu */
    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    /**
     * The slide-in menu wrapper.
     * react-burger-menu sets aria-hidden="false" when open, "true" when closed.
     */
    @FindBy(css = ".bm-menu-wrap")
    private WebElement menuWrapper;

    /** X (close) button inside the open menu */
    @FindBy(id = "react-burger-cross-btn")
    private WebElement closeButton;

    /** "All Items" sidebar link — navigates back to the inventory page */
    @FindBy(id = "inventory_sidebar_link")
    private WebElement allItemsLink;

    /** "About" sidebar link — navigates to the Sauce Labs marketing site */
    @FindBy(id = "about_sidebar_link")
    private WebElement aboutLink;

    /** "Logout" sidebar link — ends the session and returns to the login page */
    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    /** "Reset App State" sidebar link — clears cart and resets button states */
    @FindBy(id = "reset_sidebar_link")
    private WebElement resetLink;

    // Locator constant to avoid stale-proxy issues inside lambdas
    private static final By MENU_WRAPPER = By.cssSelector(".bm-menu-wrap");

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────

    public BurgerMenuPage(WebDriver driver) {
        super(driver);
    }

    // ─────────────────────────────────────────────────────────────
    // Menu open / close
    // ─────────────────────────────────────────────────────────────

    /** Returns true if the hamburger button is visible on the current page. */
    public boolean isMenuButtonVisible() {
        return isDisplayed(menuButton);
    }

    /**
     * Returns true if the side menu is currently open.
     * Checks the aria-hidden attribute on .bm-menu-wrap:
     *   "false" → menu is open; "true" or absent → menu is closed.
     */
    public boolean isMenuOpen() {
        try {
            return "false".equals(
                driver.findElement(MENU_WRAPPER).getAttribute("aria-hidden"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks the hamburger icon to open the side menu.
     * Waits until aria-hidden on .bm-menu-wrap becomes "false".
     */
    public void openMenu() {
        wait.waitForVisible(menuButton);
        jsClick(menuButton);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> "false".equals(
                        d.findElement(MENU_WRAPPER).getAttribute("aria-hidden")));
        log.info("Burger menu opened");
    }

    /**
     * Clicks the X button to close the side menu.
     * Waits until aria-hidden on .bm-menu-wrap becomes "true".
     */
    public void closeMenu() {
        wait.waitForVisible(closeButton);
        jsClick(closeButton);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> "true".equals(
                        d.findElement(MENU_WRAPPER).getAttribute("aria-hidden")));
        log.info("Burger menu closed");
    }

    // ─────────────────────────────────────────────────────────────
    // Menu actions
    // ─────────────────────────────────────────────────────────────

    /**
     * Clicks "All Items" — navigates back to the inventory / products page.
     * Menu must be open before calling this method.
     */
    public void clickAllItems() {
        wait.waitForVisible(allItemsLink);
        jsClick(allItemsLink);
        wait.waitForUrlContains("inventory.html");
        log.info("Clicked All Items from burger menu");
    }

    /**
     * Clicks "Logout" — ends the session and returns to the login page.
     * Menu must be open before calling this method.
     * Waits for the login form (username input) to appear after redirect.
     */
    public void clickLogout() {
        wait.waitForVisible(logoutLink);
        jsClick(logoutLink);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("user-name")));
        log.info("Clicked Logout from burger menu — redirected to login page");
    }

    /**
     * Clicks "Reset App State" — clears the cart and resets button labels.
     * Menu must be open before calling this method.
     */
    public void clickResetAppState() {
        wait.waitForVisible(resetLink);
        jsClick(resetLink);
        log.info("Clicked Reset App State from burger menu");
    }
}

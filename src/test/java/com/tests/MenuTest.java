package com.tests;

import com.framework.base.BaseTest;
import com.pages.BurgerMenuPage;
import com.pages.LoginPage;
import com.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * MenuTest — Tests for the SauceDemo hamburger navigation menu.
 *
 * The burger menu is a slide-in sidebar available on authenticated pages
 * (Products, Cart, Checkout). It is implemented with the react-burger-menu
 * library and exposes links for All Items, About, Logout, and Reset App State.
 *
 * Test cases (TC-MN-01 to TC-MN-05):
 *   01  Hamburger icon button is visible on the products page
 *   02  Clicking the hamburger icon opens the side menu
 *   03  Clicking the X button closes the side menu
 *   04  "All Items" link navigates back to the products page
 *   05  "Logout" link ends the session and redirects to the login page
 *
 * Pattern: Arrange → Act → Assert
 */
public class MenuTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs in and returns a BurgerMenuPage ready for menu interaction.
     * After login the driver is on the products (inventory) page, which
     * always has the hamburger button in its DOM.
     */
    private BurgerMenuPage loginAndGetMenu() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");
        return new BurgerMenuPage(getDriver());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-MN-01: Hamburger button is visible
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After logging in and arriving at the products page, the
     * hamburger menu button (#react-burger-menu-btn) should be visible.
     */
    @Test(priority = 1, description = "TC-MN-01: Hamburger menu button should be visible on the products page")
    public void menuButtonVisibleTest() {
        // Arrange + Act
        BurgerMenuPage menu = loginAndGetMenu();

        // Assert
        Assert.assertTrue(
                menu.isMenuButtonVisible(),
                "Hamburger menu button should be visible on the products page"
        );
        log.info("TC-MN-01 PASSED — Hamburger button is visible");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-MN-02: Clicking the hamburger icon opens the menu
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking the hamburger icon should slide open the side menu.
     * The .bm-menu-wrap container should have aria-hidden="false" when open.
     */
    @Test(priority = 2, description = "TC-MN-02: Clicking the hamburger icon should open the side menu")
    public void openMenuTest() {
        // Arrange
        BurgerMenuPage menu = loginAndGetMenu();

        // Pre-condition — menu is closed
        Assert.assertFalse(menu.isMenuOpen(), "Menu should be closed before opening");

        // Act
        menu.openMenu();

        // Assert — menu is now open
        Assert.assertTrue(menu.isMenuOpen(), "Menu should be open after clicking the hamburger icon");
        log.info("TC-MN-02 PASSED — Side menu opened successfully");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-MN-03: X button closes the menu
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After the side menu is open, clicking the X (close) button
     * should slide the menu shut. aria-hidden on .bm-menu-wrap should return
     * to "true".
     */
    @Test(priority = 3, description = "TC-MN-03: X button should close the side menu")
    public void closeMenuTest() {
        // Arrange — open the menu first
        BurgerMenuPage menu = loginAndGetMenu();
        menu.openMenu();
        Assert.assertTrue(menu.isMenuOpen(), "Pre-condition: menu should be open before closing");

        // Act
        menu.closeMenu();

        // Assert — menu is closed
        Assert.assertFalse(menu.isMenuOpen(), "Menu should be closed after clicking the X button");
        log.info("TC-MN-03 PASSED — Side menu closed successfully");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-MN-04: "All Items" navigates to the products page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking the "All Items" link from inside the open menu
     * should navigate to the products / inventory page.
     * This also verifies the link works from the products page itself.
     */
    @Test(priority = 4, description = "TC-MN-04: 'All Items' link should navigate to the products page")
    public void allItemsLinkTest() {
        // Arrange — open the menu from the products page
        BurgerMenuPage menu = loginAndGetMenu();
        menu.openMenu();

        // Act
        menu.clickAllItems();

        // Assert
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("inventory.html"),
                "URL should contain 'inventory.html' after clicking All Items"
        );
        ProductsPage productsPage = new ProductsPage(getDriver());
        Assert.assertTrue(
                productsPage.isPageLoaded(),
                "Products page should load after clicking All Items"
        );
        log.info("TC-MN-04 PASSED — All Items navigated to: {}", getDriver().getCurrentUrl());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-MN-05: "Logout" ends the session and shows the login page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "Logout" in the open menu should end the authenticated
     * session and redirect to the login page where the username input is visible.
     */
    @Test(priority = 5, description = "TC-MN-05: 'Logout' link should end the session and return to the login page")
    public void logoutLinkTest() {
        // Arrange — open the menu
        BurgerMenuPage menu = loginAndGetMenu();
        menu.openMenu();

        // Act
        menu.clickLogout();

        // Assert — login form is visible, authentication was cleared
        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(
                loginPage.isPageLoaded(),
                "Login page should be visible after logging out via burger menu"
        );
        log.info("TC-MN-05 PASSED — Logout redirected to: {}", getDriver().getCurrentUrl());
    }
}

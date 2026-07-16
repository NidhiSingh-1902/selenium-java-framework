package com.tests;

import com.framework.base.BaseTest;
import com.pages.LoginPage;
import com.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * ProductsTest — Tests for the SauceDemo Products / Inventory page.
 * URL: https://www.saucedemo.com/inventory.html
 *
 * This page is reached after a successful login. It shows all 6 products
 * and allows the user to sort, add/remove items from cart, and navigate.
 *
 * Test cases covered (TC-PR-01 to TC-PR-10):
 *   01  Verify page loads with correct heading and URL
 *   02  Verify exactly 6 products are listed
 *   03  Verify all products have a price displayed
 *   04  Sort by Name A to Z — first product should be "Sauce Labs Backpack"
 *   05  Sort by Name Z to A — first product should be "Test.allTheThings()..."
 *   06  Sort by Price Low to High — first price should be $7.99
 *   07  Sort by Price High to Low — first price should be $49.99
 *   08  Add one product — cart badge shows 1, button changes to "Remove"
 *   09  Add three products — cart badge shows 3
 *   10  Remove an added product — badge disappears, button resets to "Add to cart"
 *
 * How this class works:
 *   - extends BaseTest → browser opens before each test, closes after
 *   - loginAndGetProductsPage() is a helper that logs in and returns the page object
 *   - Each @Test method follows: Arrange → Act → Assert pattern
 */
public class ProductsTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────
    // Helper method
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs in as standard_user and returns a ProductsPage object.
     *
     * Why a helper method?
     *   Every test in this class starts from the products page.
     *   Instead of repeating the same 3 lines in every test, we extract
     *   them into one method — keeping the tests clean and easy to read.
     *
     * @return ProductsPage — ready to use, already on /inventory.html
     */
    private ProductsPage loginAndGetProductsPage() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");
        return new ProductsPage(getDriver());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-01: Verify page loads
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After a valid login the products page should load correctly.
     * Checks both the heading text ("Products") and the URL (/inventory.html).
     */
    @Test(priority = 1, description = "TC-PR-01: Products page should load with correct heading and URL after login")
    public void verifyPageLoadsTest() {
        // Arrange + Act — login and land on products page
        ProductsPage productsPage = loginAndGetProductsPage();

        // Assert — check the page is loaded correctly
        Assert.assertTrue(
                productsPage.isPageLoaded(),
                "Products page should be loaded (heading visible + URL contains 'inventory')"
        );
        Assert.assertEquals(
                productsPage.getPageHeading(), "Products",
                "Page heading should display 'Products'"
        );
        log.info("TC-PR-01 PASSED — Products page loaded correctly");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-02: Product count
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: SauceDemo always shows exactly 6 products on the inventory page.
     * If the count changes, something is wrong with the data or the page.
     */
    @Test(priority = 2, description = "TC-PR-02: Exactly 6 products should be visible on the inventory page")
    public void verifyProductCountTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Assert — count the products listed on the page
        Assert.assertEquals(
                productsPage.getProductCount(), 6,
                "There should be exactly 6 products on the inventory page"
        );
        log.info("TC-PR-02 PASSED — 6 products visible on page");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-03: All products have a price
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Every product card must show a price starting with "$".
     * This verifies that no price label is missing or empty on the page.
     */
    @Test(priority = 3, description = "TC-PR-03: All 6 products should display a price starting with $")
    public void verifyAllProductsHavePriceTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act — get all price texts as a list e.g. ["$29.99", "$9.99", ...]
        List<String> prices = productsPage.getAllProductPrices();

        // Assert — check the count and each price format
        Assert.assertEquals(prices.size(), 6, "All 6 products should have a price element");
        for (String price : prices) {
            Assert.assertTrue(
                    price.startsWith("$"),
                    "Each price should start with '$', but found: " + price
            );
        }
        log.info("TC-PR-03 PASSED — all 6 products have a price starting with $");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-04: Sort Name A to Z
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Selecting "Name (A to Z)" from the sort dropdown should
     * reorder the product list alphabetically ascending.
     * "Sauce Labs Backpack" starts with 'S' and is the first item alphabetically
     * among the 6 SauceDemo products.
     */
    @Test(priority = 4, description = "TC-PR-04: Sort A to Z should show 'Sauce Labs Backpack' as first product")
    public void sortByNameAToZTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act — select the sort option from the dropdown
        productsPage.selectSortOption("Name (A to Z)");

        // Assert — first product name after A-Z sort
        Assert.assertEquals(
                productsPage.getProductName(0), "Sauce Labs Backpack",
                "After A-Z sort, first product should be 'Sauce Labs Backpack'"
        );
        log.info("TC-PR-04 PASSED — A-Z sort shows 'Sauce Labs Backpack' first");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-05: Sort Name Z to A
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Selecting "Name (Z to A)" should reorder products alphabetically descending.
     * "Test.allTheThings() T-Shirt (Red)" starts with 'T' and is last alphabetically,
     * so it should appear first when sorted Z to A.
     */
    @Test(priority = 5, description = "TC-PR-05: Sort Z to A should show 'Test.allTheThings() T-Shirt (Red)' first")
    public void sortByNameZToATest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act
        productsPage.selectSortOption("Name (Z to A)");

        // Assert
        Assert.assertEquals(
                productsPage.getProductName(0), "Test.allTheThings() T-Shirt (Red)",
                "After Z-A sort, first product should be 'Test.allTheThings() T-Shirt (Red)'"
        );
        log.info("TC-PR-05 PASSED — Z-A sort shows 'Test.allTheThings() T-Shirt (Red)' first");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-06: Sort Price Low to High
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Selecting "Price (low to high)" should show the cheapest product first.
     * On SauceDemo the cheapest product is "Sauce Labs Onesie" at $7.99.
     */
    @Test(priority = 6, description = "TC-PR-06: Sort Price Low to High — cheapest product ($7.99) should appear first")
    public void sortByPriceLowToHighTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act
        productsPage.selectSortOption("Price (low to high)");

        // Assert — verify the first price shown is the cheapest
        Assert.assertEquals(
                productsPage.getFirstProductPrice(), "$7.99",
                "After price low-high sort, first price should be '$7.99'"
        );
        log.info("TC-PR-06 PASSED — Lowest price $7.99 appears first");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-07: Sort Price High to Low
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Selecting "Price (high to low)" should show the most expensive product first.
     * On SauceDemo the most expensive product is "Sauce Labs Fleece Jacket" at $49.99.
     */
    @Test(priority = 7, description = "TC-PR-07: Sort Price High to Low — most expensive product ($49.99) should appear first")
    public void sortByPriceHighToLowTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act
        productsPage.selectSortOption("Price (high to low)");

        // Assert — verify the first price shown is the most expensive
        Assert.assertEquals(
                productsPage.getFirstProductPrice(), "$49.99",
                "After price high-low sort, first price should be '$49.99'"
        );
        log.info("TC-PR-07 PASSED — Highest price $49.99 appears first");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-08: Add single product to cart
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "Add to cart" on one product should:
     *   1. Show a badge "1" on the cart icon (count of items in cart)
     *   2. Change the button text from "Add to cart" to "Remove"
     *
     * This verifies that the cart state updates correctly on the page.
     */
    @Test(priority = 8, description = "TC-PR-08: Adding one product should show badge '1' and change button text to 'Remove'")
    public void addSingleProductToCartTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act — add the first product (index 0)
        productsPage.addToCart(0);

        // Assert 1 — cart badge should show "1"
        Assert.assertEquals(
                productsPage.getCartCount(), 1,
                "Cart badge should show 1 after adding one product"
        );

        // Assert 2 — the button for that product should now say "Remove"
        Assert.assertEquals(
                productsPage.getButtonText(0), "Remove",
                "Button text should change from 'Add to cart' to 'Remove' after adding"
        );
        log.info("TC-PR-08 PASSED — Cart shows 1, button changed to 'Remove'");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-09: Add multiple products to cart
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Adding 3 different products should make the cart badge show "3".
     * Tests that each click increments the count correctly.
     */
    @Test(priority = 9, description = "TC-PR-09: Adding 3 products should show cart badge count of 3")
    public void addMultipleProductsToCartTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act — add 3 products at different indexes
        productsPage.addToCart(0);  // first product
        productsPage.addToCart(1);  // second product
        productsPage.addToCart(2);  // third product

        // Assert — cart should show total count of 3
        Assert.assertEquals(
                productsPage.getCartCount(), 3,
                "Cart badge should show 3 after adding 3 products"
        );
        log.info("TC-PR-09 PASSED — Cart shows 3 after adding 3 products");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PR-10: Remove product from products page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After adding a product, clicking "Remove" should:
     *   1. Clear the cart badge (back to 0 / no badge shown)
     *   2. Reset the button text back to "Add to cart"
     *
     * This verifies the remove action works correctly from the products page
     * without needing to go to the cart page.
     */
    @Test(priority = 10, description = "TC-PR-10: Removing an added product should clear the badge and reset button to 'Add to cart'")
    public void removeProductFromProductsPageTest() {
        // Arrange + Act
        ProductsPage productsPage = loginAndGetProductsPage();

        // Step 1 — add a product first so we have something to remove
        productsPage.addToCart(0);
        Assert.assertEquals(productsPage.getCartCount(), 1,
                "Cart should have 1 item before removal (pre-condition check)");

        // Step 2 — remove the same product
        productsPage.removeFromCart(0);

        // Assert 1 — cart badge should be gone (count = 0)
        Assert.assertEquals(
                productsPage.getCartCount(), 0,
                "Cart should be empty (count 0) after removing the product"
        );

        // Assert 2 — button should return to its original "Add to cart" text
        Assert.assertEquals(
                productsPage.getButtonText(0), "Add to cart",
                "Button text should reset to 'Add to cart' after removal"
        );
        log.info("TC-PR-10 PASSED — Cart empty, button reset to 'Add to cart'");
    }
}

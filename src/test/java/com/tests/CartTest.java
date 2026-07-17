package com.tests;

import com.framework.base.BaseTest;
import com.pages.CartPage;
import com.pages.LoginPage;
import com.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CartTest — Tests for the SauceDemo shopping cart page.
 * URL: https://www.saucedemo.com/cart.html
 *
 * The cart page is reached by clicking the cart icon after adding products.
 * It shows all added products, their prices, and lets the user remove items,
 * continue shopping, or proceed to checkout.
 *
 * Test cases covered (TC-CT-01 to TC-CT-08):
 *   01  Cart icon click → navigates to /cart.html
 *   02  Item count in cart list matches the badge number
 *   03  Product name in cart matches the name on the products page
 *   04  Product price in cart matches the price on the products page
 *   05  Remove button on cart page → item removed, badge decrements
 *   06  Continue Shopping button → returns to /inventory.html
 *   07  Checkout button → navigates to /checkout-step-one.html
 *   08  Removing all items → cart shows empty state (count = 0, no badge)
 *
 * Pattern: Arrange → Act → Assert
 */
public class CartTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs in as standard_user and returns a ready ProductsPage.
     * All tests start from the products listing page.
     */
    private ProductsPage loginAndGetProductsPage() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");
        return new ProductsPage(getDriver());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-01: Navigate to cart page via cart icon
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking the cart icon (shopping bag) in the top-right corner should
     * navigate to the cart page at /cart.html.
     *
     * Verifies:
     *   - URL contains "cart.html"
     *   - The "Your Cart" heading is visible
     */
    @Test(priority = 1, description = "TC-CT-01: Clicking cart icon should navigate to /cart.html")
    public void navigateToCartTest() {
        // Arrange
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.addToCart(0);

        // Act — click the cart icon
        productsPage.goToCart();

        // Assert
        CartPage cartPage = new CartPage(getDriver());
        Assert.assertTrue(
                cartPage.isPageLoaded(),
                "Cart page should load after clicking cart icon (URL should contain 'cart.html')"
        );
        Assert.assertEquals(
                cartPage.getPageHeading(), "Your Cart",
                "Cart page heading should be 'Your Cart'"
        );
        log.info("TC-CT-01 PASSED — Cart page loaded, heading: '{}'", cartPage.getPageHeading());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-02: Cart item count matches the badge number
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The number of rows in the cart list should match the number shown
     * on the cart badge icon (the red counter on the cart icon).
     *
     * Adds 2 products → expects 2 rows in cart list AND badge = 2.
     */
    @Test(priority = 2, description = "TC-CT-02: Number of items in cart list should match the cart badge count")
    public void cartItemCountMatchesBadgeTest() {
        // Arrange — add 2 products
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.addToCart(0);
        productsPage.addToCart(1);

        // Act — navigate to cart
        productsPage.goToCart();
        CartPage cartPage = new CartPage(getDriver());

        // Assert — list count and badge count both equal 2
        Assert.assertEquals(
                cartPage.getCartItemCount(), 2,
                "Cart list should show 2 items after adding 2 products"
        );
        Assert.assertEquals(
                cartPage.getCartBadgeCount(), 2,
                "Cart badge should show 2 after adding 2 products"
        );
        log.info("TC-CT-02 PASSED — Cart shows {} items, badge = {}",
                cartPage.getCartItemCount(), cartPage.getCartBadgeCount());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-03: Product name in cart matches products page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The product name displayed in the cart should exactly match
     * the name shown on the products listing page.
     *
     * This is a data consistency check — the cart should display the correct product.
     */
    @Test(priority = 3, description = "TC-CT-03: Product name in cart should match the name on the products page")
    public void cartItemNameMatchesTest() {
        // Arrange — record name before adding to cart
        ProductsPage productsPage = loginAndGetProductsPage();
        String nameOnProductsPage = productsPage.getProductName(0);
        log.info("Product name on products page: '{}'", nameOnProductsPage);

        // Act — add to cart and navigate
        productsPage.addToCart(0);
        productsPage.goToCart();

        // Assert — name in cart matches
        CartPage cartPage = new CartPage(getDriver());
        Assert.assertEquals(
                cartPage.getItemName(0), nameOnProductsPage,
                "Product name in cart should match the products listing page"
        );
        log.info("TC-CT-03 PASSED — Cart item name matches: '{}'", nameOnProductsPage);
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-04: Product price in cart matches products page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The product price in the cart must match the price displayed
     * on the products listing page. No price change between listing and cart.
     */
    @Test(priority = 4, description = "TC-CT-04: Product price in cart should match the price on the products page")
    public void cartItemPriceMatchesTest() {
        // Arrange — record price before adding to cart
        ProductsPage productsPage = loginAndGetProductsPage();
        String priceOnProductsPage = productsPage.getProductPrice(0);
        log.info("Product price on products page: '{}'", priceOnProductsPage);

        // Act — add to cart and navigate
        productsPage.addToCart(0);
        productsPage.goToCart();

        // Assert — price in cart matches
        CartPage cartPage = new CartPage(getDriver());
        Assert.assertEquals(
                cartPage.getItemPrice(0), priceOnProductsPage,
                "Product price in cart should match the products listing page"
        );
        log.info("TC-CT-04 PASSED — Cart item price matches: '{}'", priceOnProductsPage);
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-05: Remove item from cart on cart page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "Remove" on a cart item should:
     *   1. Remove that item from the cart list (item count decreases)
     *   2. Decrement the cart badge by 1
     *
     * Start with 2 items, remove 1, expect 1 item remaining and badge = 1.
     */
    @Test(priority = 5, description = "TC-CT-05: Removing an item from cart should decrement count and badge")
    public void removeItemFromCartTest() {
        // Arrange — add 2 products, navigate to cart
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.addToCart(0);
        productsPage.addToCart(1);
        productsPage.goToCart();
        CartPage cartPage = new CartPage(getDriver());

        // Pre-condition: verify 2 items are in cart
        Assert.assertEquals(cartPage.getCartItemCount(), 2,
                "Pre-condition: cart should have 2 items before removal");

        // Act — remove the first item
        cartPage.removeItem(0);

        // Assert — 1 item remains
        Assert.assertEquals(
                cartPage.getCartItemCount(), 1,
                "Cart should have 1 item after removing one"
        );
        Assert.assertEquals(
                cartPage.getCartBadgeCount(), 1,
                "Cart badge should show 1 after removing one item"
        );
        log.info("TC-CT-05 PASSED — 1 item remaining after removal, badge = 1");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-06: Continue Shopping button returns to products page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "Continue Shopping" on the cart page should navigate
     * back to the products listing page (/inventory.html).
     * The cart contents should be preserved (item is still in cart).
     */
    @Test(priority = 6, description = "TC-CT-06: Continue Shopping button should return to /inventory.html")
    public void continueShoppingTest() {
        // Arrange — add a product and navigate to cart
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.addToCart(0);
        productsPage.goToCart();
        CartPage cartPage = new CartPage(getDriver());

        // Act — click Continue Shopping
        cartPage.continueShopping();

        // Assert — back on products page
        ProductsPage backOnProductsPage = new ProductsPage(getDriver());
        Assert.assertTrue(
                backOnProductsPage.isPageLoaded(),
                "Should return to products page after clicking Continue Shopping"
        );
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("inventory.html"),
                "URL should contain 'inventory.html' after Continue Shopping"
        );
        // Verify cart still has 1 item (state preserved)
        Assert.assertEquals(
                backOnProductsPage.getCartCount(), 1,
                "Cart count should remain 1 after continuing shopping"
        );
        log.info("TC-CT-06 PASSED — Returned to products page, cart still has 1 item");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-07: Checkout button navigates to checkout step one
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "Checkout" on the cart page should navigate to the
     * first step of checkout — the customer information form.
     *
     * Verifies the URL changes to /checkout-step-one.html.
     */
    @Test(priority = 7, description = "TC-CT-07: Checkout button should navigate to /checkout-step-one.html")
    public void checkoutButtonTest() {
        // Arrange — add a product and navigate to cart
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.addToCart(0);
        productsPage.goToCart();
        CartPage cartPage = new CartPage(getDriver());

        // Act — click Checkout
        cartPage.clickCheckout();

        // Assert — on checkout step one
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("checkout-step-one"),
                "URL should contain 'checkout-step-one' after clicking Checkout"
        );
        log.info("TC-CT-07 PASSED — Navigated to checkout step one: {}", getDriver().getCurrentUrl());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-CT-08: Empty cart after removing all items
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After removing all items from the cart, the cart should be empty:
     *   1. No item rows are visible in the cart list
     *   2. The cart badge disappears (count = 0)
     *
     * Tests the boundary: cart transitions from having items to being empty.
     */
    @Test(priority = 8, description = "TC-CT-08: Removing all items should show empty cart and clear the badge")
    public void emptyCartAfterRemovingAllTest() {
        // Arrange — add 1 product and navigate to cart
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.addToCart(0);
        productsPage.goToCart();
        CartPage cartPage = new CartPage(getDriver());

        // Pre-condition: 1 item in cart
        Assert.assertEquals(cartPage.getCartItemCount(), 1,
                "Pre-condition: cart should have 1 item");

        // Act — remove the only item
        cartPage.removeItem(0);

        // Assert — cart is empty
        Assert.assertEquals(
                cartPage.getCartItemCount(), 0,
                "Cart should be empty (0 items) after removing the only item"
        );
        Assert.assertEquals(
                cartPage.getCartBadgeCount(), 0,
                "Cart badge should disappear (count = 0) after removing all items"
        );
        log.info("TC-CT-08 PASSED — Cart is empty, badge cleared");
    }
}

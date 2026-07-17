package com.tests;

import com.framework.base.BaseTest;
import com.pages.LoginPage;
import com.pages.ProductDetailPage;
import com.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ProductDetailTest — Tests for the SauceDemo Product Detail page.
 * URL: https://www.saucedemo.com/inventory-item.html?id=X
 *
 * The detail page opens when a user clicks a product's name or image
 * on the Products listing page. It shows the full product information
 * and allows add/remove from cart.
 *
 * Test cases covered (TC-PD-01 to TC-PD-07):
 *   01  Click product name  → detail page loads with correct URL
 *   02  Click product image → same detail page opens
 *   03  Product name on detail page matches the name on products page
 *   04  Product price on detail page matches the price on products page
 *   05  Add to cart from detail page → badge = 1, button = "Remove"
 *   06  Remove from cart on detail page → badge = 0, button = "Add to cart"
 *   07  Back to products button → returns to /inventory.html
 *
 * Pattern used: Arrange → Act → Assert
 *   Arrange = login and set up the page state
 *   Act     = perform the user action
 *   Assert  = verify the expected result
 */
public class ProductDetailTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────
    // Helper method
    // ─────────────────────────────────────────────────────────────

    /**
     * Logs in as standard_user and returns a ProductsPage object.
     * All tests in this class start from the products listing page,
     * so this helper avoids repeating the login steps in every test.
     */
    private ProductsPage loginAndGetProductsPage() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("standard_user", "secret_sauce");
        return new ProductsPage(getDriver());
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PD-01: Navigate to detail page by clicking product name
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking a product's name text on the products page should
     * navigate to the product's detail page.
     *
     * Verifies:
     *   - The URL changes to /inventory-item.html
     *   - The detail page's name element is visible (page loaded)
     */
    @Test(priority = 1, description = "TC-PD-01: Clicking product name should navigate to its detail page")
    public void openDetailByClickingNameTest() {
        // Arrange
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act — click the first product's name link
        productsPage.clickProductName(0);

        // Assert — verify detail page loaded
        ProductDetailPage detailPage = new ProductDetailPage(getDriver());
        Assert.assertTrue(
                detailPage.isPageLoaded(),
                "Detail page should load after clicking product name (URL should contain 'inventory-item')"
        );
        log.info("TC-PD-01 PASSED — Detail page loaded after clicking product name");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PD-02: Navigate to detail page by clicking product image
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking a product's thumbnail image on the products page should
     * also navigate to the same detail page as clicking the name.
     *
     * Both the name and the image are clickable links to the detail page.
     */
    @Test(priority = 2, description = "TC-PD-02: Clicking product image should also navigate to its detail page")
    public void openDetailByClickingImageTest() {
        // Arrange
        ProductsPage productsPage = loginAndGetProductsPage();

        // Act — click the first product's image
        productsPage.clickProductImage(0);

        // Assert — verify detail page loaded
        ProductDetailPage detailPage = new ProductDetailPage(getDriver());
        Assert.assertTrue(
                detailPage.isPageLoaded(),
                "Detail page should load after clicking product image"
        );
        Assert.assertTrue(
                getDriver().getCurrentUrl().contains("inventory-item"),
                "URL should contain 'inventory-item' after clicking product image"
        );
        log.info("TC-PD-02 PASSED — Detail page loaded after clicking product image");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PD-03: Product name matches products listing page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The product name shown on the detail page must exactly match
     * the name shown on the products listing page.
     *
     * This ensures clicking the correct product opens the correct detail page —
     * and that the data is consistent between the two pages.
     */
    @Test(priority = 3, description = "TC-PD-03: Product name on detail page should match the name on products page")
    public void productNameMatchesTest() {
        // Arrange — capture the first product's name from the products page
        ProductsPage productsPage = loginAndGetProductsPage();
        String nameOnProductsPage = productsPage.getProductName(0);
        log.info("Product name on products page: {}", nameOnProductsPage);

        // Act — click the product to navigate to detail page
        productsPage.clickProductName(0);

        // Assert — name on detail page must match
        ProductDetailPage detailPage = new ProductDetailPage(getDriver());
        Assert.assertEquals(
                detailPage.getProductName(), nameOnProductsPage,
                "Product name on detail page should match the name on the products listing page"
        );
        log.info("TC-PD-03 PASSED — Product name matches: '{}'", nameOnProductsPage);
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PD-04: Product price matches products listing page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: The product price shown on the detail page must exactly match
     * the price displayed on the products listing page.
     *
     * This is an important data consistency check — the price must not change
     * between the listing and the detail page.
     */
    @Test(priority = 4, description = "TC-PD-04: Product price on detail page should match the price on products page")
    public void productPriceMatchesTest() {
        // Arrange — capture the first product's price from the products page
        ProductsPage productsPage = loginAndGetProductsPage();
        String priceOnProductsPage = productsPage.getProductPrice(0);
        log.info("Product price on products page: {}", priceOnProductsPage);

        // Act — click the product name to navigate to detail page
        productsPage.clickProductName(0);

        // Assert — price on detail page must match
        ProductDetailPage detailPage = new ProductDetailPage(getDriver());
        Assert.assertEquals(
                detailPage.getProductPrice(), priceOnProductsPage,
                "Product price on detail page should match the price on the products listing page"
        );
        log.info("TC-PD-04 PASSED — Product price matches: '{}'", priceOnProductsPage);
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PD-05: Add to cart from detail page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "Add to cart" on the detail page should:
     *   1. Show cart badge "1" (item count incremented)
     *   2. Change button text from "Add to cart" to "Remove"
     *
     * Users can add products from either the listing page OR the detail page —
     * both should work the same way.
     */
    @Test(priority = 5, description = "TC-PD-05: Adding product from detail page should show badge '1' and change button to 'Remove'")
    public void addToCartFromDetailPageTest() {
        // Arrange — navigate to the first product's detail page
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.clickProductName(0);
        ProductDetailPage detailPage = new ProductDetailPage(getDriver());

        // Sync point — wait for the detail page to fully load before acting.
        // isPageLoaded() uses wait.waitForVisible(productName) internally,
        // which holds up to 15 seconds for the navigation to complete.
        Assert.assertTrue(detailPage.isPageLoaded(),
                "Detail page must be loaded before adding to cart");

        // Act — add to cart from the detail page
        detailPage.addToCart();

        // Assert 1 — cart badge should show 1
        Assert.assertEquals(
                detailPage.getCartCount(), 1,
                "Cart badge should show 1 after adding product from detail page"
        );

        // Assert 2 — button should now say "Remove"
        Assert.assertEquals(
                detailPage.getCartButtonText(), "Remove",
                "Cart button should change to 'Remove' after adding product"
        );
        log.info("TC-PD-05 PASSED — Cart shows 1, button changed to 'Remove'");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PD-06: Remove from cart on detail page
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: After adding a product, clicking "Remove" on the detail page should:
     *   1. Clear the cart badge (count goes back to 0)
     *   2. Reset button text back to "Add to cart"
     *
     * Users can also remove products from the detail page — they don't have
     * to go to the cart page to undo an add.
     */
    @Test(priority = 6, description = "TC-PD-06: Removing product from detail page should clear badge and reset button to 'Add to cart'")
    public void removeFromCartOnDetailPageTest() {
        // Arrange — navigate to detail page and add the product first
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.clickProductName(0);
        ProductDetailPage detailPage = new ProductDetailPage(getDriver());

        // Sync point — ensure detail page has loaded before cart interaction
        Assert.assertTrue(detailPage.isPageLoaded(),
                "Detail page must be loaded before removing from cart");
        detailPage.addToCart();

        // Pre-condition check — verify product was added before attempting remove
        Assert.assertEquals(detailPage.getCartCount(), 1,
                "Pre-condition: cart should have 1 item before removal");

        // Act — remove the product
        detailPage.removeFromCart();

        // Assert 1 — cart should be empty
        Assert.assertEquals(
                detailPage.getCartCount(), 0,
                "Cart should be empty (count 0) after removing product from detail page"
        );

        // Assert 2 — button should return to "Add to cart"
        Assert.assertEquals(
                detailPage.getCartButtonText(), "Add to cart",
                "Cart button should reset to 'Add to cart' after removing product"
        );
        log.info("TC-PD-06 PASSED — Cart cleared, button reset to 'Add to cart'");
    }

    // ─────────────────────────────────────────────────────────────
    // TC-PD-07: Back to products button
    // ─────────────────────────────────────────────────────────────

    /**
     * Scenario: Clicking "← Back to products" on the detail page should
     * navigate back to the products listing page (/inventory.html).
     *
     * The cart state is preserved — if items were added, the badge still shows.
     * This test verifies the navigation works correctly.
     */
    @Test(priority = 7, description = "TC-PD-07: Back to products button should return to the products listing page")
    public void backToProductsButtonTest() {
        // Arrange — navigate to any product's detail page
        ProductsPage productsPage = loginAndGetProductsPage();
        productsPage.clickProductName(0);
        ProductDetailPage detailPage = new ProductDetailPage(getDriver());

        // Verify we're on the detail page before going back (pre-condition)
        Assert.assertTrue(detailPage.isPageLoaded(),
                "Pre-condition: should be on detail page before clicking back");

        // Act — click the Back to products button
        detailPage.goBackToProducts();

        // Assert — should be back on the products listing page
        ProductsPage backOnProductsPage = new ProductsPage(getDriver());
        Assert.assertTrue(
                backOnProductsPage.isPageLoaded(),
                "Should return to products listing page after clicking 'Back to products'"
        );
        Assert.assertFalse(
                getDriver().getCurrentUrl().contains("inventory-item"),
                "URL should not contain 'inventory-item' after returning to products page"
        );
        log.info("TC-PD-07 PASSED — Returned to products listing page");
    }
}

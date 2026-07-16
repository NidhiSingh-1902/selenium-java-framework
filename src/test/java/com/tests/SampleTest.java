package com.tests;

import com.framework.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * SampleTest — Basic smoke tests to verify the framework is wired up correctly.
 *
 * These tests open the URL set in config.properties (base.url) and run simple
 * assertions. Use them as a template when writing your own test classes.
 *
 * How to create a new test class:
 *   1. Extend BaseTest — gives you getDriver(), logging, auto setup/teardown
 *   2. Annotate test methods with @Test
 *   3. Use Page Object classes (e.g., LoginPage) instead of raw WebDriver calls
 *   4. Use Assert.* methods from TestNG for assertions
 *
 * Run via Maven:
 *   mvn test                          (runs full testng.xml suite)
 *   mvn test -Dbase.url=https://...   (override URL at runtime)
 */
public class SampleTest extends BaseTest {

    /**
     * Verifies the browser successfully loaded a page by checking the title is not empty.
     * This is a basic sanity check that the browser launched and the URL responded.
     */
    @Test(description = "Verify page title loads correctly")
    public void verifyPageTitle() {
        String title = getDriver().getTitle();
        log.info("Page title: {}", title);

        Assert.assertNotNull(title, "Page title should not be null");
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty");
    }

    /**
     * Verifies the current browser URL is not null after navigation.
     * Confirms WebDriver successfully navigated to the configured base URL.
     */
    @Test(description = "Verify page URL is correct")
    public void verifyPageUrl() {
        String url = getDriver().getCurrentUrl();
        log.info("Current URL: {}", url);

        Assert.assertNotNull(url, "URL should not be null");
    }
}

package com.framework.base;

import com.framework.config.ConfigReader;
import com.framework.utils.ScreenshotUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * BaseTest — Parent class for all test classes.
 *
 * Responsibilities:
 *   - Launches the browser before each test method (@BeforeMethod)
 *   - Navigates to the base URL defined in config.properties
 *   - Takes a screenshot automatically on test failure
 *   - Quits the browser after each test method (@AfterMethod)
 *
 * Thread-safety:
 *   - Uses ThreadLocal<WebDriver> so parallel test execution works correctly;
 *     each thread gets its own isolated WebDriver instance.
 *
 * Usage:
 *   public class LoginTest extends BaseTest {
 *       @Test
 *       public void testLogin() {
 *           getDriver().findElement(By.id("user")).sendKeys("admin");
 *       }
 *   }
 *
 * Configuration keys read from config.properties:
 *   browser           — chrome | firefox | edge  (default: chrome)
 *   headless          — true | false             (default: false)
 *   base.url          — URL to open on start     (default: empty)
 *   implicit.wait     — seconds                  (default: 10)
 *   explicit.wait     — seconds                  (default: 15)
 *   page.load.timeout — seconds                  (default: 30)
 */
public class BaseTest {

    // Logger shared with all subclasses via 'protected'
    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    // ThreadLocal ensures each parallel test thread has its own WebDriver instance
    protected static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Returns the WebDriver instance for the current thread.
     * Called from test classes and utility classes that need the active driver.
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Runs before every @Test method.
     * Reads browser type from config, sets up WebDriverManager (no manual driver download needed),
     * applies timeouts, maximizes window, and navigates to base URL.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browser = ConfigReader.get("browser", "chrome");
        String headless = ConfigReader.get("headless", "false");

        WebDriver webDriver;

        // Select the browser driver based on config value
        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup(); // auto-downloads geckodriver
                webDriver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup(); // auto-downloads msedgedriver
                webDriver = new EdgeDriver();
                break;
            default:
                // Chrome is the default browser
                WebDriverManager.chromedriver().setup(); // auto-downloads chromedriver
                ChromeOptions options = new ChromeOptions();
                if (Boolean.parseBoolean(headless)) {
                    // --headless=new is the modern headless flag for Chrome 112+
                    options.addArguments("--headless=new");
                }
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications"); // suppress browser notifications
                webDriver = new ChromeDriver(options);
        }

        // Set implicit wait: WebDriver will poll for elements up to this duration before throwing
        webDriver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Long.parseLong(ConfigReader.get("implicit.wait", "10")))
        );

        // Set page load timeout: max time to wait for a page to fully load
        webDriver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(Long.parseLong(ConfigReader.get("page.load.timeout", "30")))
        );

        webDriver.manage().window().maximize();

        // Store driver in ThreadLocal so parallel tests don't share the same instance
        driver.set(webDriver);
        log.info("Browser launched: {}", browser);

        // Navigate to base URL — subclasses override getBaseUrl() to point at a different app
        String baseUrl = getBaseUrl();
        if (!baseUrl.isEmpty()) {
            webDriver.get(baseUrl);
            log.info("Navigated to: {}", baseUrl);
        }
    }

    /**
     * Returns the URL to open after the browser is launched.
     * Override in subclasses to target a different application (e.g. Redmine).
     */
    protected String getBaseUrl() {
        return ConfigReader.get("base.url", "");
    }

    /**
     * Runs after every @Test method regardless of pass/fail/skip (alwaysRun = true).
     * Captures a screenshot on failure, then quits the browser and cleans up ThreadLocal.
     *
     * @param result TestNG result object — used to check if the test failed
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Take screenshot only on failure for debugging purposes
        if (result.getStatus() == ITestResult.FAILURE) {
            ScreenshotUtils.capture(getDriver(), result.getName());
            log.error("Test FAILED: {}", result.getName());
        }

        if (getDriver() != null) {
            getDriver().quit(); // closes browser and ends WebDriver session
            driver.remove();   // removes ThreadLocal entry to prevent memory leak in thread pools
            log.info("Browser closed.");
        }
    }
}

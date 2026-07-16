package com.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtils — Captures and saves browser screenshots on test failure.
 *
 * Called automatically by:
 *   - BaseTest.tearDown() when a test fails
 *   - TestListener.onTestFailure() to attach screenshots to Extent Reports
 *
 * Screenshots are saved to the /screenshots folder with a timestamp in the filename.
 * The folder is created automatically if it doesn't exist.
 *
 * File naming format:  testName_yyyy-MM-dd_HH-mm-ss.png
 * Example:             verifyLogin_2024-03-15_14-30-22.png
 */
public class ScreenshotUtils {

    // Private constructor prevents instantiation — this is a static utility class
    private ScreenshotUtils() {}

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);

    // Folder where screenshots are saved — relative to project root
    private static final String SCREENSHOT_DIR = "screenshots";

    // Timestamp format used in screenshot filenames for uniqueness and readability
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Captures a screenshot of the current browser state and saves it as a PNG file.
     *
     * @param driver   The active WebDriver instance
     * @param testName Name of the failing test — used in the filename
     * @return Absolute path of the saved screenshot, or null if capture failed
     */
    public static String capture(WebDriver driver, String testName) {
        try {
            // Selenium's TakesScreenshot interface captures the visible browser area
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now(ZoneId.systemDefault()).format(FORMATTER);
            String fileName = testName + "_" + timestamp + ".png";
            Path destPath = Paths.get(SCREENSHOT_DIR, fileName);

            // Create the screenshots directory if it doesn't exist yet
            Files.createDirectories(destPath.getParent());

            // Copy the temp screenshot file to our named destination
            Files.copy(srcFile.toPath(), destPath);

            log.info("Screenshot saved: {}", destPath.toAbsolutePath());
            return destPath.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("Failed to save screenshot for test: {}", testName, e);
            return null; // returning null so callers can check and skip attaching to report
        }
    }
}

package com.framework.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.framework.base.BaseTest;
import com.framework.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * TestListener — TestNG listener that generates Extent Reports HTML test reports.
 *
 * Registered in testng.xml under <listeners> so TestNG calls it automatically.
 * No changes needed in test classes — this listener hooks into the test lifecycle.
 *
 * Report file: test-output/ExtentReport_<timestamp>.html
 * Open this file in any browser after a test run to see pass/fail results with screenshots.
 *
 * Lifecycle flow:
 *   onStart()        → called once before the suite starts  → creates report file
 *   onTestStart()    → called before each @Test method      → creates test node in report
 *   onTestSuccess()  → called on @Test pass                 → marks node green
 *   onTestFailure()  → called on @Test fail                 → marks node red + attaches screenshot
 *   onTestSkipped()  → called on @Test skip                 → marks node yellow
 *   onFinish()       → called once after the suite ends     → flushes/writes report to disk
 *
 * Thread-safety:
 *   Uses ThreadLocal<ExtentTest> so parallel tests each write to their own report node.
 */
public class TestListener implements ITestListener {

    // Instance variable (not static) — TestNG creates one listener instance per suite run
    private ExtentReports extent;

    // ThreadLocal ensures each parallel test thread writes to its own ExtentTest node
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    /**
     * Called once before the entire test suite begins.
     * Sets up the HTML report file and system info metadata.
     *
     * @param context TestNG suite context (provides suite name, start time, etc.)
     */
    @Override
    public void onStart(ITestContext context) {
        String timestamp = LocalDateTime.now(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportPath = "test-output/ExtentReport_" + timestamp + ".html";

        // ExtentSparkReporter generates the modern HTML report with charts and filters
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("Automation Test Report");
        spark.config().setReportName(context.getName()); // uses suite name from testng.xml

        extent = new ExtentReports();
        extent.attachReporter(spark);

        // System info appears in the report's dashboard tab
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));
    }

    /**
     * Called before each individual @Test method.
     * Creates a new node in the report for this test and stores it in ThreadLocal.
     *
     * @param result Contains test method name, description, and parameters
     */
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(
                result.getMethod().getMethodName(),      // test name shown in report
                result.getMethod().getDescription()      // @Test(description="...") value
        );
        test.set(extentTest); // store in ThreadLocal so other callbacks can access it
    }

    /**
     * Called when a @Test method passes.
     * Logs a PASS status on the current test's report node.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test PASSED");
        test.remove(); // prevent ThreadLocal memory leak in thread pools
    }

    /**
     * Called when a @Test method throws an exception (test failure).
     * Logs the exception stack trace and attaches a screenshot to the report node.
     *
     * @param result Contains the Throwable that caused the failure
     */
    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, result.getThrowable()); // logs full stack trace in report

        // Capture screenshot at the moment of failure and attach it to the report
        String screenshotPath = ScreenshotUtils.capture(
                BaseTest.getDriver(), result.getName());
        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
        }
        test.remove(); // prevent ThreadLocal memory leak in thread pools
    }

    /**
     * Called when a @Test method is skipped (e.g., a dependency test failed).
     * Logs SKIP status with the reason.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test SKIPPED: " + result.getThrowable());
        test.remove(); // prevent ThreadLocal memory leak in thread pools
    }

    /**
     * Called once after the entire test suite finishes.
     * MUST call extent.flush() — without it, the HTML file is empty/incomplete.
     */
    @Override
    public void onFinish(ITestContext context) {
        extent.flush(); // writes all buffered test data to the HTML report file
    }
}

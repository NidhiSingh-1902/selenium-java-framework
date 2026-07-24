package com.framework.base;

import com.pages.redmine.RedmineLoginPage;
import org.testng.annotations.BeforeMethod;

/**
 * RedmineBaseTest — Parent for all Redmine plugin test classes.
 *
 * Overrides the base URL to point at the local Docker Redmine instance
 * and provides a shared loginAsAdmin() helper so every test starts authenticated.
 *
 * Usage:
 *   public class CrmContactsTest extends RedmineBaseTest {
 *       @BeforeMethod
 *       public void authenticate() { loginAsAdmin(); }
 *   }
 */
public class RedmineBaseTest extends BaseTest {

    protected static final String REDMINE_BASE_URL = "http://localhost:3010";

    @Override
    protected String getBaseUrl() {
        // Point directly at the login page — Redmine allows anonymous access at /,
        // so navigating there would show a home page with no #username field.
        return REDMINE_BASE_URL + "/login";
    }

    protected void loginAsAdmin() {
        // Ensure we're on the login page before filling credentials
        if (!getDriver().getCurrentUrl().contains("/login")) {
            getDriver().get(REDMINE_BASE_URL + "/login");
        }
        RedmineLoginPage loginPage = new RedmineLoginPage(getDriver());
        loginPage.login("admin", "admin123");
        log.info("Authenticated as admin");
    }
}

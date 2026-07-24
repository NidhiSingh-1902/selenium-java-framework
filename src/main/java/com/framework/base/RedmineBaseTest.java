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
        return REDMINE_BASE_URL;
    }

    protected void loginAsAdmin() {
        RedmineLoginPage loginPage = new RedmineLoginPage(getDriver());
        loginPage.login("admin", "admin");
        log.info("Authenticated as admin");
    }
}

package com.framework.base;

import com.pages.redmine.Redmine7LoginPage;

public class Redmine7BaseTest extends BaseTest {

    protected static final String REDMINE7_BASE_URL = "http://localhost:3005";
    protected static final String TEST_PROJECT      = "website-redesign";

    @Override
    protected String getBaseUrl() {
        return REDMINE7_BASE_URL + "/login";
    }

    protected void loginAsAdmin() {
        if (!getDriver().getCurrentUrl().contains("/login")) {
            getDriver().get(REDMINE7_BASE_URL + "/login");
        }
        new Redmine7LoginPage(getDriver()).login("admin", "admin123");
    }
}

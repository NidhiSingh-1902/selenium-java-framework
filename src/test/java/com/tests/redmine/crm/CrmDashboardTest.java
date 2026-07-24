package com.tests.redmine.crm;

import com.framework.base.RedmineBaseTest;
import com.pages.redmine.crm.CrmDashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TC-CRM-DB — Redmine CRM dashboard tests.
 */
public class CrmDashboardTest extends RedmineBaseTest {

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE_BASE_URL + "/crm");
    }

    @Test(description = "TC-CRM-DB-01: CRM dashboard page loads after navigating to /crm")
    public void dashboardPageLoads() {
        CrmDashboardPage dashboard = new CrmDashboardPage(getDriver());

        Assert.assertTrue(dashboard.isDashboardLoaded(),
                "CRM content wrapper should be visible on dashboard");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/crm"),
                "URL should contain /crm");
        log.info("TC-CRM-DB-01 PASSED — dashboard loaded at: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-DB-02: CRM sidebar navigation is visible on dashboard")
    public void crmNavigationSidebarVisible() {
        CrmDashboardPage dashboard = new CrmDashboardPage(getDriver());
        dashboard.isDashboardLoaded();

        Assert.assertTrue(dashboard.isCrmNavigationVisible(),
                "CRM navigation sidebar should be visible");
        log.info("TC-CRM-DB-02 PASSED — CRM navigation sidebar is visible");
    }
}

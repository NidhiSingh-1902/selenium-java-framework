package com.tests.redmine.crm;

import com.framework.base.RedmineBaseTest;
import com.pages.redmine.crm.CrmDealsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TC-CRM-DL — Redmine CRM deals CRUD tests.
 */
public class CrmDealsTest extends RedmineBaseTest {

    private static final String TS = String.valueOf(System.currentTimeMillis() % 100_000);

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE_BASE_URL + "/deals");
    }

    @Test(description = "TC-CRM-DL-01: Deals list page loads at /deals")
    public void dealListPageLoads() {
        CrmDealsPage page = new CrmDealsPage(getDriver());
        Assert.assertTrue(page.isListLoaded(), "Deals list page should load");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/deals"),
                "URL should contain /deals");
        log.info("TC-CRM-DL-01 PASSED — deals list loaded: {}", page.getPageTitle());
    }

    @Test(description = "TC-CRM-DL-02: New deal form loads when clicking New Deal button")
    public void newDealFormLoads() {
        CrmDealsPage page = new CrmDealsPage(getDriver());
        page.clickNewDeal();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/deals/new"),
                "URL should contain /deals/new");
        log.info("TC-CRM-DL-02 PASSED — new deal form loaded");
    }

    @Test(description = "TC-CRM-DL-03: Create deal with name and amount redirects to show page")
    public void createDealWithName() {
        String name = "AutoDeal" + TS;
        String amount = "50000";

        CrmDealsPage page = new CrmDealsPage(getDriver());
        page.clickNewDeal();
        page.createDeal(name, amount);

        Assert.assertFalse(getDriver().getCurrentUrl().contains("/new"),
                "Should redirect away from new deal form after creation");
        Assert.assertFalse(page.isErrorDisplayed(),
                "No validation errors should appear after valid submission");
        log.info("TC-CRM-DL-03 PASSED — deal created, URL: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-DL-04: Created deal appears in the deals list")
    public void createdDealAppearsInList() {
        String name = "ListedDeal" + TS;

        CrmDealsPage page = new CrmDealsPage(getDriver());
        page.clickNewDeal();
        page.createDeal(name, "10000");

        getDriver().get(REDMINE_BASE_URL + "/deals");
        page = new CrmDealsPage(getDriver());

        Assert.assertTrue(page.isDealInList(name),
                "Newly created deal '" + name + "' should appear in list");
        log.info("TC-CRM-DL-04 PASSED — deal '{}' found in list", name);
    }

    @Test(description = "TC-CRM-DL-05: Submitting deal form without name shows validation error")
    public void createDealWithoutNameShowsError() {
        CrmDealsPage page = new CrmDealsPage(getDriver());
        page.clickNewDeal();
        page.createDealExpectingError("", null);

        Assert.assertTrue(page.isErrorDisplayed(),
                "Validation error should appear when deal name is missing");
        log.info("TC-CRM-DL-05 PASSED — missing name shows error: {}", page.getErrorText());
    }

    @Test(description = "TC-CRM-DL-06: Pipeline view loads at /deals/pipeline")
    public void pipelineViewLoads() {
        CrmDealsPage page = new CrmDealsPage(getDriver());
        page.isListLoaded();
        page.goToPipeline();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/pipeline"),
                "URL should contain /pipeline");
        Assert.assertTrue(page.isPipelineBoardVisible(),
                "Pipeline board should be visible");
        log.info("TC-CRM-DL-06 PASSED — pipeline view loaded at: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-DL-07: Clicking deal name in list navigates to deal detail page")
    public void clickingDealNameOpensDetailPage() {
        String name = "DetailDeal" + TS;

        CrmDealsPage page = new CrmDealsPage(getDriver());
        page.clickNewDeal();
        page.createDeal(name, "25000");

        getDriver().get(REDMINE_BASE_URL + "/deals");
        page = new CrmDealsPage(getDriver());
        page.clickDealByName(name);

        Assert.assertTrue(page.isShowPageLoaded(),
                "Clicking deal name should open the deal detail page");
        log.info("TC-CRM-DL-07 PASSED — deal detail page loaded at: {}", getDriver().getCurrentUrl());
    }
}

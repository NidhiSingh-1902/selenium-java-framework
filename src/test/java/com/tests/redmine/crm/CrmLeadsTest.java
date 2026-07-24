package com.tests.redmine.crm;

import com.framework.base.RedmineBaseTest;
import com.pages.redmine.crm.CrmLeadsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TC-CRM-LD — Redmine CRM leads CRUD tests.
 */
public class CrmLeadsTest extends RedmineBaseTest {

    private static final String TS = String.valueOf(System.currentTimeMillis() % 100_000);

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE_BASE_URL + "/leads");
    }

    @Test(description = "TC-CRM-LD-01: Leads list page loads at /leads")
    public void leadListPageLoads() {
        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        Assert.assertTrue(page.isListLoaded(), "Leads list page should load");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/leads"),
                "URL should contain /leads");
        log.info("TC-CRM-LD-01 PASSED — leads list loaded: {}", page.getPageTitle());
    }

    @Test(description = "TC-CRM-LD-02: New lead form loads when clicking New Lead button")
    public void newLeadFormLoads() {
        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        page.clickNewLead();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/leads/new"),
                "URL should contain /leads/new");
        log.info("TC-CRM-LD-02 PASSED — new lead form loaded");
    }

    @Test(description = "TC-CRM-LD-03: Create lead with required fields redirects to show page")
    public void createLeadWithRequiredFields() {
        String firstName = "LeadAuto";
        String lastName = "Test" + TS;
        String email = "leadauto" + TS + "@example.com";

        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        page.clickNewLead();
        page.createLead(firstName, lastName, email);

        Assert.assertFalse(getDriver().getCurrentUrl().contains("/new"),
                "Should redirect away from new lead form after creation");
        Assert.assertFalse(page.isErrorDisplayed(),
                "No validation errors should appear after valid submission");
        log.info("TC-CRM-LD-03 PASSED — lead created, URL: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-LD-04: Created lead appears in the leads list")
    public void createdLeadAppearsInList() {
        String firstName = "ListedLead";
        String lastName = "Person" + TS;
        String email = "listedlead" + TS + "@example.com";

        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        page.clickNewLead();
        page.createLead(firstName, lastName, email);

        getDriver().get(REDMINE_BASE_URL + "/leads");
        page = new CrmLeadsPage(getDriver());

        Assert.assertTrue(page.isLeadInList(firstName + " " + lastName),
                "Newly created lead '" + firstName + " " + lastName + "' should appear in list");
        log.info("TC-CRM-LD-04 PASSED — lead '{}' found in list", firstName + " " + lastName);
    }

    @Test(description = "TC-CRM-LD-05: Submitting lead form without first name shows validation error")
    public void createLeadWithoutFirstNameShowsError() {
        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        page.clickNewLead();
        page.createLeadExpectingError("", null, "nofirstname" + TS + "@example.com");

        Assert.assertTrue(page.isErrorDisplayed(),
                "Validation error should appear when first name is missing");
        log.info("TC-CRM-LD-05 PASSED — missing first name shows error: {}", page.getErrorText());
    }

    @Test(description = "TC-CRM-LD-06: Submitting lead form without email shows validation error")
    public void createLeadWithoutEmailShowsError() {
        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        page.clickNewLead();
        page.createLeadExpectingError("NoEmail", "Lead", null);

        Assert.assertTrue(page.isErrorDisplayed(),
                "Validation error should appear when email is missing");
        log.info("TC-CRM-LD-06 PASSED — missing email shows error: {}", page.getErrorText());
    }

    @Test(description = "TC-CRM-LD-07: Clicking lead name in list navigates to lead detail page")
    public void clickingLeadNameOpensDetailPage() {
        String firstName = "DetailLead";
        String lastName = "View" + TS;
        String email = "detaillead" + TS + "@example.com";

        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        page.clickNewLead();
        page.createLead(firstName, lastName, email);

        getDriver().get(REDMINE_BASE_URL + "/leads");
        page = new CrmLeadsPage(getDriver());
        page.clickLeadByName(firstName + " " + lastName);

        Assert.assertTrue(page.isShowPageLoaded(),
                "Clicking lead name should open the lead detail page");
        log.info("TC-CRM-LD-07 PASSED — lead detail page loaded at: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-LD-08: Convert button is visible on lead detail page")
    public void convertButtonVisibleOnLeadDetail() {
        String firstName = "ConvertLead";
        String lastName = "User" + TS;
        String email = "convertlead" + TS + "@example.com";

        CrmLeadsPage page = new CrmLeadsPage(getDriver());
        page.clickNewLead();
        page.createLead(firstName, lastName, email);

        Assert.assertTrue(page.isConvertButtonVisible(),
                "Convert to contact button should be visible on lead detail page");
        log.info("TC-CRM-LD-08 PASSED — convert button visible on lead detail page");
    }
}

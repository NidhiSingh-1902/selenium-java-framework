package com.tests.redmine.crm;

import com.framework.base.RedmineBaseTest;
import com.pages.redmine.crm.CrmCompaniesPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TC-CRM-CO — Redmine CRM companies CRUD tests.
 */
public class CrmCompaniesTest extends RedmineBaseTest {

    private static final String TS = String.valueOf(System.currentTimeMillis() % 100_000);

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE_BASE_URL + "/companies");
    }

    @Test(description = "TC-CRM-CO-01: Companies list page loads at /companies")
    public void companyListPageLoads() {
        CrmCompaniesPage page = new CrmCompaniesPage(getDriver());
        Assert.assertTrue(page.isListLoaded(), "Companies list page should load");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/companies"),
                "URL should contain /companies");
        log.info("TC-CRM-CO-01 PASSED — companies list loaded: {}", page.getPageTitle());
    }

    @Test(description = "TC-CRM-CO-02: New company form loads when clicking New Company button")
    public void newCompanyFormLoads() {
        CrmCompaniesPage page = new CrmCompaniesPage(getDriver());
        page.clickNewCompany();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/companies/new"),
                "URL should contain /companies/new");
        log.info("TC-CRM-CO-02 PASSED — new company form loaded");
    }

    @Test(description = "TC-CRM-CO-03: Create company with name redirects to show page")
    public void createCompanyWithName() {
        String name = "AutoCorp" + TS;
        String email = "corp" + TS + "@example.com";

        CrmCompaniesPage page = new CrmCompaniesPage(getDriver());
        page.clickNewCompany();
        page.createCompany(name, email);

        Assert.assertFalse(getDriver().getCurrentUrl().contains("/new"),
                "Should redirect away from new company form after creation");
        Assert.assertFalse(page.isErrorDisplayed(),
                "No validation errors should appear after valid submission");
        log.info("TC-CRM-CO-03 PASSED — company created, URL: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-CO-04: Created company appears in the companies list")
    public void createdCompanyAppearsInList() {
        String name = "ListedCorp" + TS;

        CrmCompaniesPage page = new CrmCompaniesPage(getDriver());
        page.clickNewCompany();
        page.createCompany(name, null);

        getDriver().get(REDMINE_BASE_URL + "/companies");
        page = new CrmCompaniesPage(getDriver());

        Assert.assertTrue(page.isCompanyInList(name),
                "Newly created company '" + name + "' should appear in list");
        log.info("TC-CRM-CO-04 PASSED — company '{}' found in list", name);
    }

    @Test(description = "TC-CRM-CO-05: Submitting company form without name shows validation error")
    public void createCompanyWithoutNameShowsError() {
        CrmCompaniesPage page = new CrmCompaniesPage(getDriver());
        page.clickNewCompany();
        page.createCompanyExpectingError("", null);

        Assert.assertTrue(page.isErrorDisplayed(),
                "Validation error should appear when company name is missing");
        log.info("TC-CRM-CO-05 PASSED — missing name shows error: {}", page.getErrorText());
    }

    @Test(description = "TC-CRM-CO-06: Clicking company name in list navigates to company detail page")
    public void clickingCompanyNameOpensDetailPage() {
        String name = "DetailCorp" + TS;

        CrmCompaniesPage page = new CrmCompaniesPage(getDriver());
        page.clickNewCompany();
        page.createCompany(name, null);

        getDriver().get(REDMINE_BASE_URL + "/companies");
        page = new CrmCompaniesPage(getDriver());
        page.clickCompanyByName(name);

        Assert.assertTrue(page.isShowPageLoaded(),
                "Clicking company name should open the company detail page");
        log.info("TC-CRM-CO-06 PASSED — company detail page loaded at: {}", getDriver().getCurrentUrl());
    }
}

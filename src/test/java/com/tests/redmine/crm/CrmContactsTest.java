package com.tests.redmine.crm;

import com.framework.base.RedmineBaseTest;
import com.pages.redmine.crm.CrmContactsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * TC-CRM-CT — Redmine CRM contacts CRUD tests.
 */
public class CrmContactsTest extends RedmineBaseTest {

    private static final String TS = String.valueOf(System.currentTimeMillis() % 100_000);

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE_BASE_URL + "/contacts");
    }

    @Test(description = "TC-CRM-CT-01: Contacts list page loads at /contacts")
    public void contactListPageLoads() {
        CrmContactsPage page = new CrmContactsPage(getDriver());
        Assert.assertTrue(page.isListLoaded(), "Contacts list page should load");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/contacts"),
                "URL should contain /contacts");
        log.info("TC-CRM-CT-01 PASSED — contacts list loaded: {}", page.getPageTitle());
    }

    @Test(description = "TC-CRM-CT-02: New contact form loads when clicking New Contact button")
    public void newContactFormLoads() {
        CrmContactsPage page = new CrmContactsPage(getDriver());
        page.clickNewContact();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/contacts/new"),
                "URL should contain /contacts/new");
        log.info("TC-CRM-CT-02 PASSED — new contact form loaded");
    }

    @Test(description = "TC-CRM-CT-03: Create contact with required fields redirects to show page")
    public void createContactWithRequiredFields() {
        String firstName = "AutoTest";
        String lastName = "User" + TS;
        String email = "autotest" + TS + "@example.com";

        CrmContactsPage page = new CrmContactsPage(getDriver());
        page.clickNewContact();
        page.createContact(firstName, lastName, email);

        Assert.assertFalse(getDriver().getCurrentUrl().contains("/new"),
                "Should redirect away from new contact form after creation");
        Assert.assertFalse(page.isErrorDisplayed(),
                "No validation errors should appear after valid submission");
        log.info("TC-CRM-CT-03 PASSED — contact created, URL: {}", getDriver().getCurrentUrl());
    }

    @Test(description = "TC-CRM-CT-04: Created contact appears in the contacts list")
    public void createdContactAppearsInList() {
        String firstName = "Listed";
        String lastName = "Contact" + TS;
        String email = "listed" + TS + "@example.com";

        CrmContactsPage page = new CrmContactsPage(getDriver());
        page.clickNewContact();
        page.createContact(firstName, lastName, email);

        getDriver().get(REDMINE_BASE_URL + "/contacts");
        page = new CrmContactsPage(getDriver());

        Assert.assertTrue(page.isContactInList(firstName + " " + lastName),
                "Newly created contact '" + firstName + " " + lastName + "' should appear in list");
        log.info("TC-CRM-CT-04 PASSED — contact '{}' found in list", firstName + " " + lastName);
    }

    @Test(description = "TC-CRM-CT-05: Submitting form without first name shows validation error")
    public void createContactWithoutFirstNameShowsError() {
        CrmContactsPage page = new CrmContactsPage(getDriver());
        page.clickNewContact();
        page.createContactExpectingError("", null, "nofirstname" + TS + "@example.com");

        Assert.assertTrue(page.isErrorDisplayed(),
                "Validation error should appear when first name is missing");
        log.info("TC-CRM-CT-05 PASSED — missing first name shows error: {}", page.getErrorText());
    }

    @Test(description = "TC-CRM-CT-06: Submitting form without email shows validation error")
    public void createContactWithoutEmailShowsError() {
        CrmContactsPage page = new CrmContactsPage(getDriver());
        page.clickNewContact();
        page.createContactExpectingError("NoEmail", "Test", null);

        Assert.assertTrue(page.isErrorDisplayed(),
                "Validation error should appear when email is missing");
        log.info("TC-CRM-CT-06 PASSED — missing email shows error: {}", page.getErrorText());
    }

    @Test(description = "TC-CRM-CT-07: Clicking contact name in list navigates to contact detail page")
    public void clickingContactNameOpensDetailPage() {
        String firstName = "Detail";
        String lastName = "Viewer" + TS;
        String email = "detail" + TS + "@example.com";

        CrmContactsPage page = new CrmContactsPage(getDriver());
        page.clickNewContact();
        page.createContact(firstName, lastName, email);

        getDriver().get(REDMINE_BASE_URL + "/contacts");
        page = new CrmContactsPage(getDriver());
        page.clickContactByName(firstName + " " + lastName);

        Assert.assertTrue(page.isShowPageLoaded(),
                "Clicking contact name should open the contact detail page");
        log.info("TC-CRM-CT-07 PASSED — contact detail page loaded at: {}", getDriver().getCurrentUrl());
    }
}

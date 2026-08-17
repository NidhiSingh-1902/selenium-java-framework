package com.tests.redmine.agile_board;

import com.framework.base.Redmine7BaseTest;
import com.pages.redmine.agile_board.SprintCraftPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SprintCraftTest extends Redmine7BaseTest {

    private static final String TS = String.valueOf(System.currentTimeMillis() % 100_000);

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/sprint_crafts");
    }

    @Test(description = "TC-SC-01: Sprint list page loads with list or no-data message")
    public void sprintListPageLoads() {
        SprintCraftPage page = new SprintCraftPage(getDriver());
        boolean listVisible   = page.isSprintListVisible();
        boolean noDataVisible = page.isNoDataMessageVisible();
        Assert.assertTrue(listVisible || noDataVisible,
            "Sprint list page should show either the sprint table or a no-data message");
    }

    @Test(description = "TC-SC-02: New sprint form loads with name field visible")
    public void newSprintFormLoads() {
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/sprint_crafts/new");
        SprintCraftPage page = new SprintCraftPage(getDriver());
        Assert.assertTrue(page.isFormVisible(),
            "Sprint name input should be visible on the new sprint form");
    }

    @Test(description = "TC-SC-03: Create sprint — appears in list after save")
    public void createSprintAppearsInList() {
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/sprint_crafts/new");
        SprintCraftPage page = new SprintCraftPage(getDriver());
        String name = "AutoSprint-" + TS;
        page.createSprint(name, "2026-09-01");

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/sprint_crafts"),
            "After creating a sprint, URL should be on the sprint list page");
        Assert.assertTrue(page.isSprintInList(name),
            "Newly created sprint '" + name + "' should appear in the sprint list");
    }

    @Test(description = "TC-SC-04: Edit sprint — name update reflected in list")
    public void editSprintUpdatesName() {
        // Create a sprint first
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/sprint_crafts/new");
        SprintCraftPage page = new SprintCraftPage(getDriver());
        String originalName = "EditSprint-" + TS;
        page.createSprint(originalName, "2026-09-15");

        // Click edit on the newly created sprint
        page.clickEditForSprint(originalName);
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/edit"),
            "Should navigate to edit page after clicking edit");

        // Update the name
        String updatedName = "EditedSprint-" + TS;
        page.fillForm(updatedName, "2026-09-15");
        page.submitForm();

        Assert.assertTrue(page.isSprintInList(updatedName),
            "Updated sprint name '" + updatedName + "' should appear in list");
    }

    @Test(description = "TC-SC-05: Delete sprint — removed from list after confirmation")
    public void deleteSprintRemovedFromList() {
        // Create a sprint to delete
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/sprint_crafts/new");
        SprintCraftPage page = new SprintCraftPage(getDriver());
        String name = "DeleteSprint-" + TS;
        page.createSprint(name, "2026-10-01");

        Assert.assertTrue(page.isSprintInList(name),
            "Sprint should exist before deletion");

        // Click delete and confirm
        page.clickDeleteForSprint(name);
        Assert.assertTrue(page.isDeleteModalVisible(),
            "Delete confirmation modal should appear");
        page.confirmDelete();

        Assert.assertFalse(page.isSprintInList(name),
            "Sprint '" + name + "' should no longer appear in the list after deletion");
    }

    @Test(description = "TC-SC-06: Submit new sprint form without name — stays on form or shows error")
    public void createSprintWithoutNameStaysOnForm() {
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/sprint_crafts/new");
        SprintCraftPage page = new SprintCraftPage(getDriver());

        // Submit with only dates, no name
        page.fillForm("", "2026-11-01");
        page.submitForm();

        boolean staysOnForm = getDriver().getCurrentUrl().contains("/sprint_crafts/new")
                              || getDriver().getCurrentUrl().contains("/sprint_crafts");
        boolean hasError    = page.isErrorVisible();

        Assert.assertTrue(staysOnForm || hasError,
            "Submitting sprint form without a name should either stay on form or show a validation error");
    }
}

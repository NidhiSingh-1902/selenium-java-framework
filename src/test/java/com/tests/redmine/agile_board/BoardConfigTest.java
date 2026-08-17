package com.tests.redmine.agile_board;

import com.framework.base.Redmine7BaseTest;
import com.pages.redmine.agile_board.BoardConfigPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BoardConfigTest extends Redmine7BaseTest {

    private static final String TS = String.valueOf(System.currentTimeMillis() % 100_000);

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/agile_board");
    }

    @Test(description = "TC-BC-01: New board config form loads with name field visible")
    public void newBoardConfigFormLoads() {
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/board_configs/new");
        BoardConfigPage page = new BoardConfigPage(getDriver());
        Assert.assertTrue(page.isNameFieldVisible(),
            "Board config name field (id=query_name) should be visible on new form");
    }

    @Test(description = "TC-BC-02: Create Kanban board config — navigates away from new form")
    public void createKanbanBoardConfig() {
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/board_configs/new");
        BoardConfigPage page = new BoardConfigPage(getDriver());
        page.createBoardConfig("KanbanConfig-" + TS, "kanban");

        Assert.assertFalse(getDriver().getCurrentUrl().contains("/board_configs/new"),
            "After saving a board config, should redirect away from the new form");
    }

    @Test(description = "TC-BC-03: Create Scrum board config — navigates away from new form")
    public void createScrumBoardConfig() {
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/board_configs/new");
        BoardConfigPage page = new BoardConfigPage(getDriver());
        page.createBoardConfig("ScrumConfig-" + TS, "scrum");

        Assert.assertFalse(getDriver().getCurrentUrl().contains("/board_configs/new"),
            "After saving a scrum board config, should redirect away from the new form");
    }

    @Test(description = "TC-BC-04: Cancel board config creation — returns to agile board")
    public void cancelBoardConfigCreationReturnsToBoard() {
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/board_configs/new");
        BoardConfigPage page = new BoardConfigPage(getDriver());
        page.clickCancel();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("/agile_board"),
            "Cancelling board config creation should return to the agile board page");
    }
}

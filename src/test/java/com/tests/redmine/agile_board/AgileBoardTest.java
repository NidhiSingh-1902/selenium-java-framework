package com.tests.redmine.agile_board;

import com.framework.base.Redmine7BaseTest;
import com.pages.redmine.agile_board.AgileBoardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AgileBoardTest extends Redmine7BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void authenticate() {
        loginAsAdmin();
        getDriver().get(REDMINE7_BASE_URL + "/projects/" + TEST_PROJECT + "/agile_board");
    }

    @Test(description = "TC-AB-01: Agile board page loads with kanban container")
    public void boardLoadsWithKanbanContainer() {
        AgileBoardPage page = new AgileBoardPage(getDriver());
        Assert.assertTrue(page.isBoardVisible(),
            "rf-kanban-board container should be visible on the board page");
    }

    @Test(description = "TC-AB-02: Agile board nav item is marked as selected")
    public void boardNavItemIsSelected() {
        AgileBoardPage page = new AgileBoardPage(getDriver());
        Assert.assertTrue(page.isBoardNavSelected(),
            "The agile-board nav item should carry the 'selected' CSS class");
    }

    @Test(description = "TC-AB-03: Totals bar is displayed on board page")
    public void totalBarIsVisible() {
        AgileBoardPage page = new AgileBoardPage(getDriver());
        Assert.assertTrue(page.isTotalsBarVisible(),
            "rf-board-totals-bar should be present on the board page");
    }

    @Test(description = "TC-AB-04: Filter panel element exists in the DOM")
    public void filterPanelExistsInDom() {
        AgileBoardPage page = new AgileBoardPage(getDriver());
        Assert.assertTrue(page.isFilterPanelPresent(),
            "rf-filter-panel element should be present in the DOM");
    }

    @Test(description = "TC-AB-05: Backlog page loads when navigating from board")
    public void backlogPageLoads() {
        AgileBoardPage page = new AgileBoardPage(getDriver());
        page.navigateToBacklog();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/backlog"),
            "URL should contain '/backlog' after clicking the backlog link");
    }

    @Test(description = "TC-AB-06: Global agile board page loads")
    public void globalBoardPageLoads() {
        AgileBoardPage page = new AgileBoardPage(getDriver());
        page.navigateToGlobalBoard();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/agile_board/global"),
            "URL should contain '/agile_board/global'");
        Assert.assertFalse(getDriver().getTitle().isEmpty(),
            "Global board page should have a title");
    }
}

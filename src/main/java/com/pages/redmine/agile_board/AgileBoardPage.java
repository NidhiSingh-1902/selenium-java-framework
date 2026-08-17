package com.pages.redmine.agile_board;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AgileBoardPage extends BasePage {

    @FindBy(id = "rf-kanban-board")
    private WebElement kanbanBoard;

    @FindBy(id = "rf-filter-toggle")
    private WebElement filterToggle;

    @FindBy(id = "rf-filter-panel")
    private WebElement filterPanel;

    @FindBy(css = ".agile-board.selected")
    private WebElement selectedBoardNavItem;

    @FindBy(css = ".rf-board-totals-bar")
    private WebElement totalsBar;

    @FindBy(css = "a[href*='/backlog']")
    private WebElement backlogLink;

    @FindBy(css = "a[href='/agile_board/global']")
    private WebElement globalBoardLink;

    public AgileBoardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isBoardVisible() {
        return isDisplayed(kanbanBoard);
    }

    public boolean isBoardNavSelected() {
        return isDisplayed(selectedBoardNavItem);
    }

    public boolean isTotalsBarVisible() {
        return isDisplayed(totalsBar);
    }

    public void clickFilterToggle() {
        jsClick(filterToggle);
    }

    public boolean isFilterPanelPresent() {
        return !driver.findElements(By.id("rf-filter-panel")).isEmpty();
    }

    public void navigateToBacklog() {
        click(backlogLink);
    }

    public void navigateToGlobalBoard() {
        click(globalBoardLink);
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}

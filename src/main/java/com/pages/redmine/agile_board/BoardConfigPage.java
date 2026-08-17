package com.pages.redmine.agile_board;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class BoardConfigPage extends BasePage {

    // ── New / Edit form ──────────────────────────────────────────────────────
    // Rails text_field 'query', 'name' → id="query_name"
    @FindBy(id = "query_name")
    private WebElement nameField;

    // Board type select: 0=Kanban, 1=Scrum
    @FindBy(id = "sprints_enabled")
    private WebElement boardTypeSelect;

    @FindBy(css = ".rf-btn.rf-btn-primary")
    private WebElement saveButton;

    @FindBy(css = ".rf-btn.rf-btn-secondary")
    private WebElement cancelButton;

    public BoardConfigPage(WebDriver driver) {
        super(driver);
    }

    public boolean isFormVisible() {
        return isDisplayed(nameField);
    }

    public void createBoardConfig(String name, String boardType) {
        type(nameField, name);
        new Select(boardTypeSelect).selectByValue(boardType.equals("scrum") ? "1" : "0");
        click(saveButton);
    }

    public void clickCancel() {
        click(cancelButton);
    }

    public boolean isNameFieldVisible() {
        return isDisplayed(nameField);
    }

    public boolean isBoardConfigLinkPresent(String configName) {
        return !driver.findElements(
            By.xpath("//a[contains(@href,'board_config_id') and normalize-space()='" + configName + "']")
        ).isEmpty();
    }
}

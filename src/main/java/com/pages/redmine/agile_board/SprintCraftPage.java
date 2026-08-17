package com.pages.redmine.agile_board;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SprintCraftPage extends BasePage {

    // ── List page ────────────────────────────────────────────────────────────
    @FindBy(css = ".list.sprints")
    private WebElement sprintList;

    @FindBy(css = ".nodata")
    private WebElement noDataMessage;

    // ── New / Edit form ──────────────────────────────────────────────────────
    @FindBy(id = "sprint_craft_name")
    private WebElement nameField;

    @FindBy(id = "sprint_start_date")
    private WebElement startDateField;

    @FindBy(id = "sprint_end_date")
    private WebElement endDateField;

    @FindBy(id = "sprint_craft_status")
    private WebElement statusSelect;

    @FindBy(id = "sprint_craft_description")
    private WebElement descriptionField;

    @FindBy(css = "input[name='commit']")
    private WebElement submitButton;

    // ── Delete modal ─────────────────────────────────────────────────────────
    @FindBy(id = "sprint-delete-modal")
    private WebElement deleteModal;

    @FindBy(css = ".sprint-btn-delete")
    private WebElement confirmDeleteButton;

    public SprintCraftPage(WebDriver driver) {
        super(driver);
    }

    // ── List queries ──────────────────────────────────────────────────────────

    public boolean isSprintListVisible() {
        return isDisplayed(sprintList);
    }

    public boolean isNoDataMessageVisible() {
        return isDisplayed(noDataMessage);
    }

    public boolean isSprintInList(String sprintName) {
        return !driver.findElements(
            By.xpath("//table[contains(@class,'sprints')]//td/a[normalize-space()='" + sprintName + "']")
        ).isEmpty();
    }

    // ── Form interactions ─────────────────────────────────────────────────────

    public boolean isFormVisible() {
        return isDisplayed(nameField);
    }

    public void fillForm(String name, String startDate) {
        if (!name.isEmpty()) {
            type(nameField, name);
        }
        // end_date is readonly (auto-calculated from start + duration via JS)
        // Use JS to set the date value and fire the change event so updateEndDate() runs
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
            startDateField, startDate);
    }

    public void submitForm() {
        click(submitButton);
    }

    public void createSprint(String name, String startDate) {
        fillForm(name, startDate);
        submitForm();
    }

    public String getNameFieldValue() {
        return nameField.getAttribute("value");
    }

    // ── List actions ──────────────────────────────────────────────────────────

    public void clickEditForSprint(String sprintName) {
        WebElement editLink = driver.findElement(By.xpath(
            "//table[contains(@class,'sprints')]//td/a[normalize-space()='" + sprintName + "']"
        ));
        click(editLink);
    }

    public void clickDeleteForSprint(String sprintName) {
        WebElement deleteBtn = driver.findElement(By.xpath(
            "//table[contains(@class,'sprints')]//td/a[normalize-space()='" + sprintName + "']" +
            "/ancestor::tr//a[contains(@class,'sprint-delete-btn')]"
        ));
        jsClick(deleteBtn);
    }

    // ── Delete modal ──────────────────────────────────────────────────────────

    public boolean isDeleteModalVisible() {
        try {
            wait.waitForVisible(deleteModal);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void confirmDelete() {
        wait.waitForVisible(confirmDeleteButton);
        click(confirmDeleteButton);
    }

    // ── Error detection ───────────────────────────────────────────────────────

    public boolean isErrorVisible() {
        return !driver.findElements(By.cssSelector("#errorExplanation, .flash.error")).isEmpty();
    }
}

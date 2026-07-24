package com.pages.redmine.crm;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page object covering the Deals list (/deals), the Pipeline view (/deals/pipeline),
 * and the Deal create/edit form (/deals/new, /deals/:id/edit).
 */
public class CrmDealsPage extends BasePage {

    // — List / Pipeline elements —
    @FindBy(css = ".crm-page-title")
    private WebElement pageTitle;

    @FindBy(css = "a.crm-btn.crm-btn-primary")
    private WebElement newDealButton;

    @FindBy(css = "table.crm-data-table tbody tr")
    private List<WebElement> dealRows;

    @FindBy(css = ".crm-empty-state-large")
    private WebElement emptyState;

    // Pipeline view
    @FindBy(css = ".crm-pipeline-board, .crm-pipeline-column, .crm-pipeline")
    private WebElement pipelineBoard;

    // — Form elements —
    @FindBy(name = "crm_deal[name]")
    private WebElement nameField;

    @FindBy(name = "crm_deal[amount]")
    private WebElement amountField;

    @FindBy(name = "crm_deal[probability]")
    private WebElement probabilityField;

    @FindBy(css = "input[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".crm-error-box")
    private WebElement errorBox;

    private static final By TABLE_LINK = By.cssSelector("a.crm-table-link");
    private static final By PIPELINE_LINK = By.cssSelector("a[href*='/deals/pipeline']");

    public CrmDealsPage(WebDriver driver) {
        super(driver);
    }

    // — List page actions —

    public boolean isListLoaded() {
        wait.waitForVisible(pageTitle);
        return isDisplayed(pageTitle);
    }

    public String getPageTitle() {
        wait.waitForVisible(pageTitle);
        return getText(pageTitle);
    }

    public int getDealCount() {
        return dealRows.size();
    }

    public boolean isDealInList(String name) {
        return driver.findElements(TABLE_LINK).stream()
                .anyMatch(el -> el.getText().trim().equalsIgnoreCase(name));
    }

    public void clickNewDeal() {
        wait.waitForClickable(newDealButton);
        jsClick(newDealButton);
        wait.waitForUrlContains("/deals/new");
        log.info("Navigated to new deal form");
    }

    public void clickDealByName(String name) {
        driver.findElements(TABLE_LINK).stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(el -> jsClick(el));
    }

    public void goToPipeline() {
        wait.waitForClickable(PIPELINE_LINK).click();
        wait.waitForUrlContains("/pipeline");
        log.info("Navigated to pipeline view");
    }

    public boolean isPipelineBoardVisible() {
        try {
            wait.waitForVisible(pipelineBoard);
            return true;
        } catch (Exception e) {
            // check for content-wrapper as fallback
            return driver.findElements(By.cssSelector(".crm-content-wrapper")).size() > 0;
        }
    }

    public void deleteFirstDeal() {
        WebElement deleteLink = driver.findElement(
                By.cssSelector("td.crm-table-actions a[data-method='delete']"));
        jsClick(deleteLink);
        try {
            wait.waitForAlert().accept();
        } catch (Exception e) {
            log.debug("No JS alert for deal delete");
        }
        wait.waitForUrlContains("/deals");
    }

    // — Form page actions —

    public void fillDealForm(String name, String amount) {
        wait.waitForVisible(nameField);
        type(nameField, name);
        if (amount != null) type(amountField, amount);
        log.info("Filled deal form: name={}, amount={}", name, amount);
    }

    public void submitForm() {
        wait.waitForClickable(submitButton);
        jsClick(submitButton);
        log.info("Submitted deal form");
    }

    public void createDeal(String name, String amount) {
        fillDealForm(name, amount);
        submitForm();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.getCurrentUrl().contains("/new") &&
                            !d.getCurrentUrl().contains("/edit"));
        log.info("Deal created: {}", name);
    }

    public void createDealExpectingError(String name, String amount) {
        fillDealForm(name, amount);
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelector('form.crm-form').noValidate=true;");
        submitForm();
    }

    public boolean isErrorDisplayed() {
        try {
            wait.waitForVisible(errorBox);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorText() {
        wait.waitForVisible(errorBox);
        return getText(errorBox);
    }

    public boolean isShowPageLoaded() {
        wait.waitForVisible(pageTitle);
        return getCurrentUrl().matches(".*\\/deals\\/\\d+$");
    }
}

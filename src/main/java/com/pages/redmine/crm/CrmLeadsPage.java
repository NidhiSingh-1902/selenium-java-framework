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
 * Page object covering the Leads list (/leads) and
 * the Lead create/edit form (/leads/new, /leads/:id/edit).
 */
public class CrmLeadsPage extends BasePage {

    // — List page elements —
    @FindBy(css = ".crm-page-title")
    private WebElement pageTitle;

    @FindBy(css = "a.crm-btn.crm-btn-primary")
    private WebElement newLeadButton;

    @FindBy(css = "table.crm-data-table tbody tr")
    private List<WebElement> leadRows;

    @FindBy(css = ".crm-empty-state-large")
    private WebElement emptyState;

    // — Form elements —
    @FindBy(name = "crm_lead[first_name]")
    private WebElement firstNameField;

    @FindBy(name = "crm_lead[last_name]")
    private WebElement lastNameField;

    @FindBy(name = "crm_lead[email]")
    private WebElement emailField;

    @FindBy(name = "crm_lead[phone]")
    private WebElement phoneField;

    @FindBy(name = "crm_lead[company_name]")
    private WebElement companyNameField;

    @FindBy(css = "input[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".crm-error-box")
    private WebElement errorBox;

    // Converted status label on the lead show page (shows "No" for unconverted leads)
    private static final By CONVERTED_STATUS_LABEL = By.xpath("//label[normalize-space()='Converted']");

    private static final By TABLE_LINK = By.cssSelector("a.crm-table-link");

    public CrmLeadsPage(WebDriver driver) {
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

    public int getLeadCount() {
        return leadRows.size();
    }

    public boolean isLeadInList(String name) {
        return driver.findElements(TABLE_LINK).stream()
                .anyMatch(el -> el.getText().trim().equalsIgnoreCase(name));
    }

    public void clickNewLead() {
        wait.waitForClickable(newLeadButton);
        jsClick(newLeadButton);
        wait.waitForUrlContains("/leads/new");
        log.info("Navigated to new lead form");
    }

    public void clickLeadByName(String name) {
        driver.findElements(TABLE_LINK).stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(el -> jsClick(el));
    }

    public void deleteFirstLead() {
        WebElement deleteLink = driver.findElement(
                By.cssSelector("td.crm-table-actions a[data-method='delete']"));
        jsClick(deleteLink);
        try {
            wait.waitForAlert().accept();
        } catch (Exception e) {
            log.debug("No JS alert for lead delete");
        }
        wait.waitForUrlContains("/leads");
    }

    // — Form page actions —

    public void fillLeadForm(String firstName, String lastName, String email) {
        wait.waitForVisible(firstNameField);
        type(firstNameField, firstName);
        if (lastName != null) type(lastNameField, lastName);
        if (email != null) type(emailField, email);
        log.info("Filled lead form: {} {} <{}>", firstName, lastName, email);
    }

    public void fillLeadForm(String firstName, String lastName, String email,
                              String phone, String companyName) {
        fillLeadForm(firstName, lastName, email);
        if (phone != null) type(phoneField, phone);
        if (companyName != null) type(companyNameField, companyName);
    }

    public void submitForm() {
        wait.waitForClickable(submitButton);
        jsClick(submitButton);
        log.info("Submitted lead form");
    }

    public void createLead(String firstName, String lastName, String email) {
        fillLeadForm(firstName, lastName, email);
        submitForm();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.getCurrentUrl().contains("/new") &&
                            !d.getCurrentUrl().contains("/edit"));
        log.info("Lead created: {} {}", firstName, lastName);
    }

    public void createLeadExpectingError(String firstName, String lastName, String email) {
        fillLeadForm(firstName, lastName, email);
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
        return getCurrentUrl().matches(".*\\/leads\\/\\d+$");
    }

    public boolean isConvertedStatusVisible() {
        return !driver.findElements(CONVERTED_STATUS_LABEL).isEmpty();
    }
}

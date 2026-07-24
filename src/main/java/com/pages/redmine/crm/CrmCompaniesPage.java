package com.pages.redmine.crm;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page object covering the Companies list (/companies) and
 * the Company create/edit form (/companies/new, /companies/:id/edit).
 */
public class CrmCompaniesPage extends BasePage {

    // — List page elements —
    @FindBy(css = ".crm-page-title")
    private WebElement pageTitle;

    @FindBy(css = "a.crm-btn.crm-btn-primary")
    private WebElement newCompanyButton;

    @FindBy(css = "table.crm-data-table tbody tr")
    private List<WebElement> companyRows;

    @FindBy(css = ".crm-empty-state-large")
    private WebElement emptyState;

    // — Form page elements —
    @FindBy(name = "company[name]")
    private WebElement nameField;

    @FindBy(name = "company[email]")
    private WebElement emailField;

    @FindBy(name = "company[phone]")
    private WebElement phoneField;

    @FindBy(name = "company[website]")
    private WebElement websiteField;

    @FindBy(name = "company[industry]")
    private WebElement industryField;

    @FindBy(css = "input[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".crm-error-box")
    private WebElement errorBox;

    private static final By TABLE_LINK = By.cssSelector("a.crm-table-link");

    public CrmCompaniesPage(WebDriver driver) {
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

    public int getCompanyCount() {
        return companyRows.size();
    }

    public boolean isEmptyStateVisible() {
        return isDisplayed(emptyState);
    }

    public boolean isCompanyInList(String name) {
        return driver.findElements(TABLE_LINK).stream()
                .anyMatch(el -> el.getText().trim().equalsIgnoreCase(name));
    }

    public void clickNewCompany() {
        wait.waitForClickable(newCompanyButton);
        jsClick(newCompanyButton);
        wait.waitForUrlContains("/companies/new");
        log.info("Navigated to new company form");
    }

    public void clickCompanyByName(String name) {
        driver.findElements(TABLE_LINK).stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(el -> jsClick(el));
    }

    public void deleteFirstCompany() {
        WebElement deleteLink = driver.findElement(
                By.cssSelector("td.crm-table-actions a[data-method='delete']"));
        jsClick(deleteLink);
        try {
            wait.waitForAlert().accept();
        } catch (Exception e) {
            log.debug("No JS alert for company delete");
        }
        wait.waitForUrlContains("/companies");
    }

    // — Form page actions —

    public void fillCompanyForm(String name, String email) {
        wait.waitForVisible(nameField);
        type(nameField, name);
        if (email != null) type(emailField, email);
        log.info("Filled company form: name={}", name);
    }

    public void fillCompanyForm(String name, String email, String phone, String website) {
        fillCompanyForm(name, email);
        if (phone != null) type(phoneField, phone);
        if (website != null) type(websiteField, website);
    }

    public void submitForm() {
        wait.waitForClickable(submitButton);
        jsClick(submitButton);
        log.info("Submitted company form");
    }

    public void createCompany(String name, String email) {
        fillCompanyForm(name, email);
        submitForm();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.getCurrentUrl().contains("/new") &&
                            !d.getCurrentUrl().contains("/edit"));
        log.info("Company created: {}", name);
    }

    public void createCompanyExpectingError(String name, String email) {
        fillCompanyForm(name, email);
        submitForm();
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorBox);
    }

    public String getErrorText() {
        wait.waitForVisible(errorBox);
        return getText(errorBox);
    }

    public boolean isShowPageLoaded() {
        wait.waitForVisible(pageTitle);
        return getCurrentUrl().matches(".*\\/companies\\/\\d+$");
    }
}

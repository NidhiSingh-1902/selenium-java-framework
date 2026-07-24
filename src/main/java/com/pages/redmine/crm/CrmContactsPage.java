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
 * Page object covering both the Contacts list (/contacts) and the
 * Contact create/edit form (/contacts/new, /contacts/:id/edit).
 */
public class CrmContactsPage extends BasePage {

    // — List page elements —
    @FindBy(css = ".crm-page-title")
    private WebElement pageTitle;

    @FindBy(css = "a.crm-btn.crm-btn-primary")
    private WebElement newContactButton;

    @FindBy(css = "table.crm-data-table tbody tr")
    private List<WebElement> contactRows;

    @FindBy(css = ".crm-empty-state-large")
    private WebElement emptyState;

    // — Form page elements —
    @FindBy(name = "contact[first_name]")
    private WebElement firstNameField;

    @FindBy(name = "contact[last_name]")
    private WebElement lastNameField;

    @FindBy(name = "contact[email]")
    private WebElement emailField;

    @FindBy(name = "contact[phone]")
    private WebElement phoneField;

    @FindBy(name = "contact[job_title]")
    private WebElement jobTitleField;

    @FindBy(css = "input[type='submit']")
    private WebElement submitButton;

    @FindBy(css = ".crm-error-box")
    private WebElement errorBox;

    private static final By TABLE_LINK = By.cssSelector("a.crm-table-link");
    private static final By EDIT_ICON = By.cssSelector("a.crm-action-icon i.fa-pencil");
    private static final By DELETE_ICON = By.cssSelector("a.crm-action-icon i.fa-trash");

    public CrmContactsPage(WebDriver driver) {
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

    public int getContactCount() {
        return contactRows.size();
    }

    public boolean isEmptyStateVisible() {
        return isDisplayed(emptyState);
    }

    public boolean isContactInList(String name) {
        return driver.findElements(TABLE_LINK).stream()
                .anyMatch(el -> el.getText().trim().equalsIgnoreCase(name));
    }

    public void clickNewContact() {
        wait.waitForClickable(newContactButton);
        jsClick(newContactButton);
        wait.waitForUrlContains("/contacts/new");
        log.info("Navigated to new contact form");
    }

    public void clickContactByName(String name) {
        driver.findElements(TABLE_LINK).stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(el -> {
                    jsClick(el);
                    log.info("Clicked contact: {}", name);
                });
    }

    public void deleteFirstContact() {
        WebElement deleteLink = driver.findElement(
                By.cssSelector("td.crm-table-actions a[data-method='delete']"));
        jsClick(deleteLink);
        try {
            wait.waitForAlert().accept();
            log.info("Accepted delete confirmation dialog");
        } catch (Exception e) {
            log.debug("No JS alert shown — Rails Turbo may have handled delete inline");
        }
        wait.waitForUrlContains("/contacts");
    }

    // — Form page actions —

    public void fillContactForm(String firstName, String lastName, String email) {
        wait.waitForVisible(firstNameField);
        type(firstNameField, firstName);
        if (lastName != null) type(lastNameField, lastName);
        if (email != null) type(emailField, email);
        log.info("Filled contact form: {} {} <{}>", firstName, lastName, email);
    }

    public void fillContactForm(String firstName, String lastName, String email,
                                 String phone, String jobTitle) {
        fillContactForm(firstName, lastName, email);
        if (phone != null) type(phoneField, phone);
        if (jobTitle != null) type(jobTitleField, jobTitle);
    }

    public void submitForm() {
        wait.waitForClickable(submitButton);
        jsClick(submitButton);
        log.info("Submitted contact form");
    }

    public void createContact(String firstName, String lastName, String email) {
        fillContactForm(firstName, lastName, email);
        submitForm();
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> !d.getCurrentUrl().contains("/new") &&
                            !d.getCurrentUrl().contains("/edit"));
        log.info("Contact created: {} {}", firstName, lastName);
    }

    public void createContactExpectingError(String firstName, String lastName, String email) {
        fillContactForm(firstName, lastName, email);
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
        return getCurrentUrl().matches(".*\\/contacts\\/\\d+$");
    }
}

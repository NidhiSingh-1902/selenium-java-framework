package com.pages.redmine.crm;

import com.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CrmDashboardPage extends BasePage {

    @FindBy(css = ".crm-content-wrapper")
    private WebElement crmContentWrapper;

    @FindBy(css = ".crm-navigation")
    private WebElement crmNavigation;

    @FindBy(css = ".crm-page-title")
    private WebElement pageTitle;

    private static final By CONTACTS_NAV_LINK = By.cssSelector("a[href*='/contacts']");
    private static final By COMPANIES_NAV_LINK = By.cssSelector("a[href*='/companies']");
    private static final By DEALS_NAV_LINK = By.cssSelector("a[href*='/deals']");
    private static final By LEADS_NAV_LINK = By.cssSelector("a[href*='/leads']");

    public CrmDashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDashboardLoaded() {
        wait.waitForVisible(crmContentWrapper);
        return isDisplayed(crmContentWrapper);
    }

    public boolean isCrmNavigationVisible() {
        return isDisplayed(crmNavigation);
    }

    public String getPageTitleText() {
        wait.waitForVisible(pageTitle);
        return getText(pageTitle);
    }

    public CrmContactsPage goToContacts() {
        wait.waitForClickable(CONTACTS_NAV_LINK).click();
        wait.waitForUrlContains("/contacts");
        return new CrmContactsPage(driver);
    }

    public CrmDealsPage goToDeals() {
        wait.waitForClickable(DEALS_NAV_LINK).click();
        wait.waitForUrlContains("/deals");
        return new CrmDealsPage(driver);
    }
}

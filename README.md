# Selenium Java Automation Framework

A production-ready Selenium 4 + Java + TestNG framework with Page Object Model, data-driven testing, Extent Reports, and parallel execution support.

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 11+ | Language |
| Selenium WebDriver | 4.18.1 | Browser automation |
| TestNG | 7.9.0 | Test runner |
| WebDriverManager | 5.8.0 | Auto browser driver setup |
| Extent Reports | 5.1.1 | HTML test reports |
| Apache POI | 5.2.5 | Excel data-driven testing |
| Log4j2 | 2.23.1 | Logging |
| Maven | 3.8+ | Build & dependency management |

## Project Structure

```
selenium-java-framework/
├── src/
│   ├── main/
│   │   ├── java/com/framework/
│   │   │   ├── base/        BaseTest.java        — driver setup/teardown
│   │   │   ├── config/      ConfigReader.java     — reads config.properties
│   │   │   ├── pages/       BasePage.java         — reusable page actions
│   │   │   ├── utils/
│   │   │   │   ├── WaitUtils.java                 — explicit waits
│   │   │   │   ├── ScreenshotUtils.java           — auto screenshot on fail
│   │   │   │   └── ExcelUtils.java                — read Excel test data
│   │   │   └── listeners/   TestListener.java     — Extent Reports wiring
│   │   └── resources/       log4j2.xml
│   └── test/
│       ├── java/com/tests/  SampleTest.java
│       └── resources/
│           ├── config.properties                   — browser, URL, timeouts
│           └── testng.xml                          — test suite config
├── screenshots/             — captured on test failure
├── test-output/             — HTML reports + logs
└── pom.xml
```

## Setup

### Prerequisites
- Java 11 or higher
- Maven 3.8+
- Chrome / Firefox / Edge browser installed

### Run Tests

```bash
# Run all tests (uses testng.xml)
mvn test

# Override browser at runtime
mvn test -Dbrowser=firefox

# Run headless
mvn test -Dheadless=true

# Override base URL
mvn test -Dbase.url=https://your-app.com
```

## How to Add a New Page

1. Create `src/main/java/com/pages/LoginPage.java` extending `BasePage`
2. Add `@FindBy` locators and action methods

```java
public class LoginPage extends BasePage {
    @FindBy(id = "username") private WebElement usernameField;
    @FindBy(id = "password") private WebElement passwordField;
    @FindBy(id = "loginBtn")  private WebElement loginButton;

    public LoginPage(WebDriver driver) { super(driver); }

    public void login(String user, String pass) {
        type(usernameField, user);
        type(passwordField, pass);
        click(loginButton);
    }
}
```

## How to Add a New Test

```java
public class LoginTest extends BaseTest {
    @Test
    public void validLogin() {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("admin", "password");
        Assert.assertTrue(getDriver().getTitle().contains("Dashboard"));
    }
}
```

## Data-Driven Testing

```java
@DataProvider(name = "loginData")
public Object[][] getData() {
    return ExcelUtils.toDataProvider("src/test/resources/testdata/login.xlsx", "LoginData");
}

@Test(dataProvider = "loginData")
public void loginWithMultipleUsers(Map<String, String> row) {
    new LoginPage(getDriver()).login(row.get("username"), row.get("password"));
}
```

## Reports
After a test run, open `test-output/ExtentReport_<timestamp>.html` in a browser for a detailed HTML report with pass/fail status and screenshots for failures.

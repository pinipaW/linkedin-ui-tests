# LinkedIn UI Test Suite (IT3153 Assignment 04)

Selenium WebDriver + TestNG (Java) tests for LinkedIn: login (valid and invalid), search, and opening a search result. Uses the Page Object Model and explicit waits.

## Prerequisites
- JDK 17 or later (developed with JDK 23)
- Maven 3.9+
- Google Chrome (ChromeDriver is downloaded automatically by Selenium Manager)

## Configure credentials
1. Copy `config.properties.example` to `config.properties`.
2. Fill in a dedicated LinkedIn test account:

       linkedin.username=your_test_email
       linkedin.password=your_test_password
       search.term=software testing

   Alternatively set the environment variables LINKEDIN_USERNAME, LINKEDIN_PASSWORD and SEARCH_TERM.
3. `config.properties` is in `.gitignore`. Never commit real credentials.

## Run

    mvn clean test

Results: `target/surefire-reports/index.html`. Screenshots: `screenshots/`.

## CAPTCHA / security checkpoints
The tests never try to bypass a CAPTCHA or verification step. If LinkedIn shows one, the test pauses for up to 3 minutes so you can complete it manually, then continues.

## Structure
- `src/main/java/com/sqa/pages`: LoginPage, FeedPage, SearchResultsPage
- `src/test/java/com/sqa/base`: BaseTest
- `src/test/java/com/sqa/utils`: ConfigReader, ScreenshotListener
- `src/test/java/com/sqa/tests`: LoginTest, SearchTest, SearchResultClickTest
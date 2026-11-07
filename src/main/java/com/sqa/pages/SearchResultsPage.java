package com.sqa.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

public class SearchResultsPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Scoped to main content; matches on href path, not volatile class names
    private final By resultLinks = By.cssSelector(
            "main a[href*='/in/'], main a[href*='/company/'], main a[href*='/school/'], "
          + "[role='main'] a[href*='/in/'], [role='main'] a[href*='/company/'], [role='main'] a[href*='/school/']");

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private WebElement firstResult() {
        return wait.until(d -> d.findElements(resultLinks).stream()
                .filter(WebElement::isDisplayed)
                .filter(e -> {
                    String href = e.getAttribute("href");
                    return href != null && !href.isBlank();
                })
                .findFirst()
                .orElse(null));
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("/search/results"));
            return firstResult().isDisplayed();
        } catch (TimeoutException e) {
            saveDiagnostics();
            throw e;
        }
    }

    /** Clicks the first result and returns the href that was clicked. Retries if the list re-renders. */
    public String clickFirstResult() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement first = firstResult();          // re-find on every attempt
                String href = first.getAttribute("href");
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", first);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
                return href;
            } catch (StaleElementReferenceException e) {
                System.out.println(">>> Stale result element, retrying (" + attempt + "/3)");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new StaleElementReferenceException("First result kept going stale after 3 attempts");
    }

    private void saveDiagnostics() {
        try {
            System.out.println(">>> SearchResultsPage: no result link. URL: " + driver.getCurrentUrl()
                    + " | Title: " + driver.getTitle());
            List<WebElement> anchors = driver.findElements(By.tagName("a"));
            System.out.println(">>> total <a>: " + anchors.size());
            anchors.stream().limit(25).forEach(a -> System.out.println(">>> A href=" + a.getAttribute("href")
                    + " displayed=" + a.isDisplayed()));
            File shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = Path.of("screenshots", "results_not_found.png");
            Files.createDirectories(dest.getParent());
            Files.copy(shot.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> Screenshot saved: " + dest);
        } catch (Exception ex) {
            System.out.println(">>> Could not save diagnostics: " + ex.getMessage());
        }
    }
}
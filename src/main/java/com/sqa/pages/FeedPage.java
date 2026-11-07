package com.sqa.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

public class FeedPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // id is auto-generated, so locate by the stable placeholder text
    private final By searchBox = By.cssSelector("input[placeholder*='Search' i]");
    // collapsed layout shows only a magnifier button
    private final By searchIcon = By.cssSelector(
            "header button[aria-label*='Search' i], header a[aria-label*='Search' i], "
          + "header [role='button'][aria-label*='Search' i]");

    public FeedPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private WebElement presentBox() {
        return wait.until(d -> {
            List<WebElement> l = d.findElements(searchBox);
            return l.isEmpty() ? null : l.get(0);
        });
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.urlContains("/feed"));
            return presentBox() != null;
        } catch (TimeoutException e) {
            saveDiagnostics("feed_not_loaded");
            throw e;
        }
    }

    private void activate(WebElement box) {
        if (!box.isDisplayed()) {
            for (WebElement icon : driver.findElements(searchIcon)) {
                if (icon.isDisplayed()) {
                    try { icon.click(); } catch (WebDriverException ignored) { }
                    break;
                }
            }
        }
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click(); arguments[0].focus();", box);
    }

    public SearchResultsPage search(String term) {
        WebElement box = presentBox();
        activate(box);
        new Actions(driver).sendKeys(term).perform();
        new Actions(driver).sendKeys(Keys.ENTER).perform();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.urlContains("/search/results"));
        } catch (TimeoutException e) {
            saveDiagnostics("search_not_submitted");
            throw e;
        }
        return new SearchResultsPage(driver);
    }

    private void saveDiagnostics(String name) {
        try {
            System.out.println(">>> FeedPage " + name + ". URL: " + driver.getCurrentUrl()
                    + " | Window: " + driver.manage().window().getSize());
            driver.findElements(By.tagName("input")).forEach(i -> System.out.println(">>> INPUT type="
                    + i.getAttribute("type") + " placeholder=" + i.getAttribute("placeholder")
                    + " displayed=" + i.isDisplayed() + " size=" + i.getSize()));
            File shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = Path.of("screenshots", name + ".png");
            Files.createDirectories(dest.getParent());
            Files.copy(shot.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> Screenshot saved: " + dest);
        } catch (Exception ex) {
            System.out.println(">>> Could not save diagnostics: " + ex.getMessage());
        }
    }
}
package com.sqa.tests;

import com.sqa.base.BaseTest;
import com.sqa.pages.*;
import com.sqa.utils.ConfigReader;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class SearchResultClickTest extends BaseTest {

    @Test(description = "TC04 - Clicking first result opens the right page")
    public void openFirstResult() {
        LoginPage login = new LoginPage(driver).open();
        login.login(ConfigReader.get("linkedin.username"), ConfigReader.get("linkedin.password"));
        login.waitForManualVerificationIfNeeded();

        FeedPage feed = new FeedPage(driver);
        Assert.assertTrue(feed.isLoaded(), "Feed should load after login");

        SearchResultsPage results = feed.search(ConfigReader.get("search.term"));
        Assert.assertTrue(results.isLoaded(), "Results should be visible");

        String clickedHref = results.clickFirstResult();
        String expectedPath = java.net.URI.create(clickedHref).getPath().replaceAll("/$", "");

        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !d.getCurrentUrl().contains("/search/results"));

        Assert.assertTrue(driver.getCurrentUrl().contains(expectedPath),
                "Expected URL to contain " + expectedPath + " but was " + driver.getCurrentUrl());
    }
}
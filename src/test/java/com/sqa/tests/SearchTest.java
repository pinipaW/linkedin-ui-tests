package com.sqa.tests;

import com.sqa.base.BaseTest;
import com.sqa.pages.*;
import com.sqa.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    @Test(description = "TC03 - Search returns a results page")
    public void searchShowsResults() {
        LoginPage login = new LoginPage(driver).open();
        login.login(ConfigReader.get("linkedin.username"), ConfigReader.get("linkedin.password"));
        login.waitForManualVerificationIfNeeded();

        FeedPage feed = new FeedPage(driver);
        Assert.assertTrue(feed.isLoaded(), "Feed should load after login");

        SearchResultsPage results = feed.search(ConfigReader.get("search.term"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/search/results"), "URL should be a search results URL");
        Assert.assertTrue(results.isLoaded(), "At least one result should be visible");
    }
}
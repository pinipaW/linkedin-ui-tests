package com.sqa.tests;

import com.sqa.base.BaseTest;
import com.sqa.pages.FeedPage;
import com.sqa.pages.LoginPage;
import com.sqa.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "TC01 - Valid login")
    public void validLogin() {
        LoginPage login = new LoginPage(driver).open();
        login.login(ConfigReader.get("linkedin.username"), ConfigReader.get("linkedin.password"));
        login.waitForManualVerificationIfNeeded();

        FeedPage feed = new FeedPage(driver);
        Assert.assertTrue(feed.isLoaded(), "Search box should be visible after login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/feed"), "URL should contain /feed");
    }

    @Test(description = "TC02 - Invalid password shows error")
    public void invalidPasswordLogin() {
        LoginPage login = new LoginPage(driver).open();
        login.login(ConfigReader.get("linkedin.username"), "WrongPassword!123");

        Assert.assertTrue(login.isErrorDisplayed(), "An error message should be displayed");
        Assert.assertFalse(driver.getCurrentUrl().contains("/feed"), "Should not reach the feed");
    }
}
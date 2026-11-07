package com.sqa.base;

import com.sqa.utils.ConfigReader;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        // LinkedIn hides the search box on narrow windows
        if (driver.manage().window().getSize().getWidth() < 1200) {
            driver.manage().window().setSize(new Dimension(1920, 1080));
        }
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Demo pause only: keeps the final page visible before the browser closes.
        // It is not used for synchronisation (all test waits are explicit waits).
        pauseForViewing();
        if (driver != null) {
            driver.quit();
        }
    }

    private void pauseForViewing() {
        int seconds = 6;
        try {
            String v = ConfigReader.get("demo.pause.seconds");
            if (v != null && !v.isBlank()) seconds = Integer.parseInt(v.trim());
        } catch (NumberFormatException ignored) { }
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public WebDriver getDriver() { return driver; }
}
package com.sqa.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // LinkedIn's IDs are auto-generated, so we use stable attributes instead.
    private final By usernameField = By.cssSelector("input[autocomplete*='username'], input[type='email']");
    private final By passwordField = By.cssSelector("input[type='password']");
    private final By signInButton  = By.xpath("//button[normalize-space(.)='Sign in']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    private WebElement firstDisplayed(By by) {
        return wait.until(d -> d.findElements(by).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElse(null));
    }

    private boolean isDisplayedNow(WebDriver d, By by) {
        return d.findElements(by).stream().anyMatch(WebElement::isDisplayed);
    }

    public LoginPage open() {
        driver.get("https://www.linkedin.com/login");
        try {
            firstDisplayed(usernameField);
        } catch (TimeoutException e) {
            saveScreenshot("login_form_not_found");
            System.out.println(">>> Login form not shown. Complete any step MANUALLY in the browser (up to 3 min)...");
            new WebDriverWait(driver, Duration.ofMinutes(3))
                    .until(d -> isDisplayedNow(d, usernameField) || d.getCurrentUrl().contains("/feed"));
        }
        return this;
    }

    public void login(String user, String pass) {
        if (driver.getCurrentUrl().contains("/feed")) {
            return;
        }
        try {
            WebElement userBox = firstDisplayed(usernameField);
            userBox.clear();
            userBox.sendKeys(user);

            WebElement passBox = firstDisplayed(passwordField);
            passBox.clear();
            passBox.sendKeys(pass);

            firstDisplayed(signInButton).click();
        } catch (TimeoutException e) {
            saveScreenshot("login_failed");
            throw e;
        }
    }

    /** If LinkedIn shows a checkpoint/CAPTCHA we do NOT bypass it.
     *  We pause and give the human up to 3 minutes to complete it manually. */
    public void waitForManualVerificationIfNeeded() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                    d.getCurrentUrl().contains("/feed")
                            || d.getCurrentUrl().contains("checkpoint")
                            || d.getCurrentUrl().contains("challenge"));
        } catch (TimeoutException ignored) { }

        String url = driver.getCurrentUrl();
        if (url.contains("checkpoint") || url.contains("challenge")) {
            System.out.println(">>> Security checkpoint detected. Complete it manually in the browser (3 min)...");
            new WebDriverWait(driver, Duration.ofMinutes(3))
                    .until(ExpectedConditions.urlContains("/feed"));
        }
    }

    /** Negative test helper: true if we stayed on the login flow and the page shows an error. */
    public boolean isErrorDisplayed() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
                if (d.getCurrentUrl().contains("/feed")) return false;
                boolean styled = d.findElements(By.cssSelector(
                        "[role='alert'], [aria-live], [id*='error' i], [class*='error' i], [data-error]"))
                        .stream().anyMatch(e -> e.isDisplayed() && !e.getText().isBlank());
                if (styled) return true;
                String body = d.findElement(By.tagName("body")).getText().toLowerCase();
                return body.contains("wrong password") || body.contains("incorrect password")
                        || body.contains("not the right password") || body.contains("try again")
                        || body.contains("password you provided");
            });
        } catch (TimeoutException e) {
            String body = driver.findElement(By.tagName("body")).getText();
            System.out.println(">>> Error check failed. URL: " + driver.getCurrentUrl()
                    + " | Page text: " + body.substring(0, Math.min(400, body.length())).replace("\n", " "));
            saveScreenshot("invalid_login_no_error");
            return false;
        }
    }

    private void saveScreenshot(String name) {
        try {
            File shot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = Path.of("screenshots", name + ".png");
            Files.createDirectories(dest.getParent());
            Files.copy(shot.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> Screenshot saved: " + dest);
        } catch (Exception ex) {
            System.out.println(">>> Could not save screenshot: " + ex.getMessage());
        }
    }
}
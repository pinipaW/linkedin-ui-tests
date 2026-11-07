package com.sqa.utils;

import com.sqa.base.BaseTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) { capture(result, "FAIL"); }

    @Override
    public void onTestSuccess(ITestResult result) { capture(result, "PASS"); }

    private void capture(ITestResult result, String status) {
        Object instance = result.getInstance();
        if (!(instance instanceof BaseTest)) return;
        WebDriver driver = ((BaseTest) instance).getDriver();
        if (driver == null) return;
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = Path.of("screenshots", status + "_" + result.getName() + ".png");
            Files.createDirectories(dest.getParent());
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> Auto screenshot: " + dest);
        } catch (Exception e) {
            System.out.println(">>> Screenshot failed: " + e.getMessage());
        }
    }
}
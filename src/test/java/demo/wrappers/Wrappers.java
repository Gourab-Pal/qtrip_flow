package demo.wrappers;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import demo.utils.Setup;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class Wrappers {

    public static boolean clickOnElement(WebDriver driver, By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement webelem = wait.until(ExpectedConditions.elementToBeClickable(locator));
            webelem.click();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void enterText(WebDriver driver, By locator, String text) {
        try {
            WebElement elem = driver.findElement(locator);
            elem.clear();
            elem.sendKeys(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String capture(String filename, WebDriver driver) throws IOException {
        String folderPath = Setup.screenshotsFolderPath;
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destination = new File(folderPath + filename);
        FileUtils.copyFile(screenshot, destination);
        return destination.getAbsolutePath();
    }
}

package demo.listeners;

import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.relevantcodes.extentreports.LogStatus;

import demo.utils.Setup;
import demo.wrappers.Wrappers;

public class MyTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Setup.getTest().log(LogStatus.INFO, "Executing: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Setup.getTest().log(LogStatus.PASS, "Passed: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = Setup.getDriver();
        long timestamp = System.currentTimeMillis();
        String fileName = result.getName() + "_" + timestamp + ".jpg";
        try {
            Setup.getTest().log(LogStatus.FAIL,
                    Setup.getTest().addScreenCapture(Wrappers.capture(fileName, driver)) + "Failed: " + result.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Setup.getTest().log(LogStatus.SKIP, "Skipped: " + result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}
}



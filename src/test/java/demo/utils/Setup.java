package demo.utils;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import demo.wrappers.DriverSingleton;

public class Setup {
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    public static ExtentReports reports;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private String resourcesFolderPath = System.getProperty("user.dir") + "\\src\\test\\resources\\";
    private String extentReportFileName = resourcesFolderPath + "qtrip_reports.html";
    public static String screenshotsFolderPath = System.getProperty("user.dir") + "\\src\\test\\screenshots\\";

    @Parameters("browser")
    @BeforeMethod
    public void init(String browser) {
        WebDriver webDriver = DriverSingleton.getDriverInstance(browser);
        driver.set(webDriver);
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (reports == null) {
            reports = new ExtentReports(extentReportFileName, true);
        }

        ExtentTest extentTest = reports.startTest("Tests for Qtrip [" + browser + "]");
        test.set(extentTest);
    }

    @AfterMethod
    public void tearDown() {
        reports.endTest(test.get());
        reports.flush();
        DriverSingleton.closeDriverInstance();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static ExtentTest getTest() {
        return test.get();
    }
}


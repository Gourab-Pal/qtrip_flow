package demo;

import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import demo.pages.Home;
import demo.utils.Setup;

public class QtripTests extends Setup {

    @Test(description = "Verify if the homepage url is valid")
    public void qtrip_tc_001() throws IOException {
        WebDriver driver = getDriver();
        Home home = new Home(driver);
        home.navigateToHome();
        Assert.assertTrue(driver.getCurrentUrl().contains("qtrip"), "Target url is invalid");
    }
}





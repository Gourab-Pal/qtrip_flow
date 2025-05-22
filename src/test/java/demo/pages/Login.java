package demo.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class Login {
    ChromeDriver driver;
    public String pageUrl = "https://qtripdynamic-qa-frontend.vercel.app/pages/login/";

    public Login(ChromeDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 10), this);
    }

    @FindBy(css = "input[type='email']")
    WebElement inputEmailField;

    @FindBy(css = "input[type='password']")
    WebElement inputPasswordField;

    @FindBy(css = "button[type='submit']")
    WebElement performLoginButton;

    public void navigateToLogin() {
        if (driver.getCurrentUrl() != pageUrl) {
            driver.get(pageUrl);
        }
    }
}

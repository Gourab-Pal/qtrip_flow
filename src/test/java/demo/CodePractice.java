package demo;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CodePractice {

        static ChromeDriver driver;
        static ExtentReports reports;
        static ExtentTest test;

        public static String capture(ChromeDriver driver) throws IOException {
                File screenshots = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String pathName = System.getProperty("user.dir") + "\\src\\test\\resources\\" + UUID.randomUUID()
                                + ".jpg";
                File destination = new File(pathName);
                FileUtils.copyFile(screenshots, destination);
                return destination.getAbsolutePath();
        }

        @BeforeSuite(alwaysRun = true)
        public void init() {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito");
                options.addArguments("--disable-notifications");
                driver = new ChromeDriver(options);
                driver.manage().window().maximize();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
                reports = new ExtentReports(
                                System.getProperty("user.dir") + "\\src\\test\\resources\\" + "practiceReports.html",
                                true);
                test = reports.startTest("Practice code extent report test");
        }

        @AfterSuite
        public void tearDown() {
                driver.quit();
                reports.endTest(test);
                reports.flush();
        }

        public static void goHome() {
                driver.get("https://www.easemytrip.com/");
        }

        @Test
        public void practice01() throws InterruptedException, IOException {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
                driver.get("https://www.easemytrip.com/");
                test.log(LogStatus.INFO, "Clicking on hotel link");
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@class='hotels mainMenu']"))).click();

                FluentWait<ChromeDriver> fWait = new FluentWait<ChromeDriver>(driver)
                                .withTimeout(Duration.ofSeconds(20))
                                .withMessage("Fluent wait expired").pollingEvery(Duration.ofMillis(100))
                                .ignoring(NoSuchElementException.class);
                String mainWindowHandle = driver.getWindowHandle();
                fWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@class='list-property-n2']")))
                                .click();
                Set<String> windowHandlesSet = driver.getWindowHandles();
                for (String handle : windowHandlesSet) {
                        if (!handle.equals(mainWindowHandle)) {
                                driver.switchTo().window(handle);
                                break;
                        }
                }
                Assert.assertTrue(driver.getCurrentUrl().contains("/hotels/appinventry/index"));
                test.log(LogStatus.PASS, "new listing hotel page url is valid");
                driver.close();
                test.log(LogStatus.INFO, "going back to main window");
                driver.switchTo().window(mainWindowHandle);
                driver.get("https://www.easemytrip.com/");
                /*
                 * search for flight blr to ccu on 30th april for 5 passengers. sort the search
                 * results with cheapest first.
                 * extract airline name, pnr, departure time, arrival time, duration, number of
                 * stops, total price in a json file.
                 */

                // select source city
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@class='flights mainMenu']")))
                                .click();
                wait.until(ExpectedConditions.elementToBeClickable(By.id("frmcity"))).click();
                WebElement fromCitySuggestion = wait
                                .until(ExpectedConditions.visibilityOfElementLocated(By.id("fromautoFill_in")));
                WebElement fromCityInput = fromCitySuggestion.findElement(By.xpath(".//input"));
                fromCityInput.clear();
                fromCityInput.click();
                fromCityInput.sendKeys("blr");
                wait.until(ExpectedConditions
                                .presenceOfElementLocated(By.xpath(
                                                "//div[@id='fromautoFill_in']//div[text()='Suggestion Cities']")));
                fromCitySuggestion.findElement(By.xpath(".//ul/li[1]//span")).click();

                // select destination city
                WebElement toCitySuggestion = wait
                                .until(ExpectedConditions.visibilityOfElementLocated(By.id("toautoFill_in")));
                WebElement toCityInput = toCitySuggestion.findElement(By.xpath(".//input"));
                toCityInput.clear();
                toCityInput.click();
                toCityInput.sendKeys("ccu");
                wait.until(ExpectedConditions
                                .presenceOfElementLocated(By
                                                .xpath("//div[@id='toautoFill_in']//div[text()='Suggestion Cities']")));
                toCitySuggestion.findElement(By.xpath(".//ul/li[1]//span")).click();

                // select journey date (current month 29th)
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='box']")));
                driver.findElement(By.xpath("//div[@class='box']//div[@class='days']//li[text()='29']")).click();

                // select number of traveller to 6
                driver.findElement(By.xpath("//div[@id='myFunction4']")).click();
                int target = 6;
                WebElement adultNumberBlock = driver.findElement(
                                By.xpath("//div[@class='dropdown-travelr' and @style='display: block;']//div[@id='field1']"));
                int current = Integer.parseInt(adultNumberBlock.findElement(By.xpath("./input")).getAttribute("value"));
                if (current < target) {
                        int diff = target - current;
                        while (diff > 0) {
                                wait.until(ExpectedConditions
                                                .elementToBeClickable(adultNumberBlock
                                                                .findElement(By.xpath("./button[@id='add']"))))
                                                .click();
                                diff--;
                        }
                } else {
                        int diff = current - target;
                        while (diff > 0) {
                                wait.until(ExpectedConditions
                                                .elementToBeClickable(adultNumberBlock
                                                                .findElement(By.xpath("./button[@id='asub']"))))
                                                .click();
                                diff--;
                        }
                }
                driver.findElement(By.xpath("//a[@id='traveLer']")).click();
                driver.findElement(By.xpath("//button[@class='srchBtnSe']")).click();

                // wait for the page load
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='Loader']")));
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='chepestbt gr-bdr']")))
                                .click();

                // flight cards
                List<WebElement> flightCards = new ArrayList<>();
                for (int i = 1; i <= 5; i++) {
                        String xpath = "//div[@class='top_bar_flgt_1']/div[contains(@class, 'fltResult')][" + i + "]";
                        flightCards.add(driver.findElement(By.xpath(xpath)));
                }

                ArrayList<HashMap<String, String>> flightDataList = new ArrayList<>();

                for (WebElement flightCard : flightCards) {
                        HashMap<String, String> flightDetails = new HashMap<>();
                        String airlineName = flightCard
                                        .findElement(By.xpath(
                                                        ".//div[@class='row']//div[@class='row']//span[@class='txt-r4 ng-binding']"))
                                        .getText();
                        String airlinePNR = flightCard
                                        .findElement(By.xpath(
                                                        ".//div[@class='row']//div[@class='row']//span[@class='txt-r5']"))
                                        .getText();
                        String departureTime = flightCard
                                        .findElement(
                                                        By.xpath(".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-4 top5 ']/span[1]"))
                                        .getText();
                        String flightSource = flightCard
                                        .findElement(
                                                        By.xpath(".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-4 top5 ']/span[2]"))
                                        .getText();

                        String duration = flightCard
                                        .findElement(
                                                        By.xpath(
                                                                        ".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-5 non-st wd14c']/span[1]"))
                                        .getText();

                        String stops = flightCard
                                        .findElement(
                                                        By.xpath(
                                                                        ".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-5 non-st wd14c']/span[2]"))
                                        .getText();

                        String arrivalTime = flightCard
                                        .findElement(
                                                        By.xpath(
                                                                        ".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-3 top5 txdir']/span[1]"))
                                        .getText();
                        String flightDestination = flightCard
                                        .findElement(
                                                        By.xpath(
                                                                        ".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-3 top5 txdir']/span[2]"))
                                        .getText();

                        String rawPrice = flightCard
                                        .findElement(
                                                        By.xpath(
                                                                        ".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-5 mr5 cle']//span[contains(@id, 'divCut')]"))
                                        .getText().replace(",", "");

                        String discountedPrice = flightCard
                                        .findElement(
                                                        By.xpath(
                                                                        ".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-5 mr5 cle']//div[@class='txt-r6-n ng-scope']//span[contains(@id, 'spnPrice')]"))
                                        .getText().replace(",", "");

                        String discountAmount = flightCard
                                        .findElement(
                                                        By.xpath(
                                                                        ".//div[@class='row']/div[@class='col-md-2 col-sm-2 col-xs-5 mr5 cle']/div[@class='cou-text-app ng-binding ng-scope']"))
                                        .getText().replace("Rs.", "").replace(" Discount Applied", "");

                        flightDetails.put("Origin", flightSource);
                        flightDetails.put("Destination", flightDestination);
                        flightDetails.put("Airline name", airlineName);
                        flightDetails.put("Airline PNR", airlinePNR);
                        flightDetails.put("Departure time", departureTime);
                        flightDetails.put("Arrival time", arrivalTime);
                        flightDetails.put("Duration", duration);
                        flightDetails.put("Number of stops", stops);
                        flightDetails.put("Fare", rawPrice);
                        flightDetails.put("Discount applied", discountAmount);
                        flightDetails.put("Discounted fare", discountedPrice);

                        flightDataList.add(flightDetails);
                }

                ObjectMapper mapper = new ObjectMapper();
                File mmtFlightDetails = new File(
                                System.getProperty("user.dir") + "//src//test//resources//mmtCheapestFlights.json");
                mapper.writerWithDefaultPrettyPrinter().writeValue(mmtFlightDetails, flightDataList);

                /*
                 * Naviagte the homepage. scoll to trustpilot review section.
                 * Print the second available review text.
                 */

                driver.get("https://www.easemytrip.com/");
                JavascriptExecutor js = (JavascriptExecutor) driver;
                WebElement trustPilotFrame = wait.until(ExpectedConditions
                                .visibilityOfElementLocated(
                                                By.xpath("(//iframe[contains(@title,  'Trustpilot')])[2]")));

                js.executeScript("arguments[0].scrollIntoView(true)", trustPilotFrame);
                driver.switchTo().frame(trustPilotFrame);

                System.out.println(
                                driver.findElement(By.xpath("//div[@class='tp-widget-review'][2]//div[@class='text']"))
                                                .getText());

                driver.switchTo().defaultContent();

                /*
                 * in the homepage, hover over "Login or Signup", click on "Customer Login",
                 * use "gourabpal@email.com", then select all, then cut the email
                 */

                WebElement loginSignupBtn = driver.findElement(By.xpath("//span[text()='Login or Signup']"));
                js.executeScript("arguments[0].scrollIntoView(true)", loginSignupBtn);
                Actions actions = new Actions(driver);
                actions.moveToElement(loginSignupBtn).perform();
                driver.findElement(By.xpath("//span[text()='Customer Login']")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='_newbglog']")));
                WebElement emailMobField = driver.findElement(By.id("txtEmail"));
                emailMobField.clear();
                actions.click(emailMobField).sendKeys("gourabpal@email.com").keyDown(Keys.CONTROL).sendKeys("a")
                                .keyUp(Keys.CONTROL).keyDown(Keys.CONTROL).sendKeys("x").keyUp(Keys.CONTROL).perform();

                /*
                 * verify all 10 Famous Tourist Attraction links are broken or not
                 */
                goHome();
                for (int i = 1; i <= 10; i++) {
                        String xpathDest = "//a[@class='_des_bx'][" + i + "]";
                        WebElement destElem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathDest)));
                        String baseURL = destElem.getAttribute("href");
                        RestAssured.baseURI = baseURL;
                        RestAssured.basePath = "";
                        RequestSpecification http = RestAssured.given();
                        Response resp = http.when().get();
                        int statusCode = resp.getStatusCode();
                        if(statusCode == 200) {
                                System.out.println("Link is valid");
                        } else {
                                System.out.println("Link is broken");
                        }
                }

                Thread.sleep(2000);
        }
}

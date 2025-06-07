package Common.Common;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.UUID;

public class Utilities {
    public static String getProjectPath() {
        return System.getProperty("user.dir");
    }
//
//    public static String generateRandomString() {
//        return UUID.randomUUID().toString().substring(0, 8);
//    }

//    public static String generateRandomEmail() {
//        return "test" + generateRandomString() + "@mailinator.com";
//    }

    public static void scrollToElement(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}

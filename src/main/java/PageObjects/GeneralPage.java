//package PageObjects;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//
//public class GeneralPage {
//    protected WebDriver driver;
//
//    public GeneralPage(WebDriver driver) {
//        this.driver = driver;
//    }
//
//    protected WebElement find(By locator) {
//        return driver.findElement(locator);
//    }
//
//    protected void click(By locator) {
//        find(locator).click();
//    }
//
//    protected void type(By locator, String text) {
//        find(locator).clear();
//        find(locator).sendKeys(text);
//    }
//
//    protected boolean isDisplayed(By locator) {
//        try {
//            return find(locator).isDisplayed();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    protected boolean isEnabled(By locator) {
//        try {
//            return find(locator).isEnabled();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public void switchToFrame(By locator) {
//        driver.switchTo().frame(driver.findElement(locator));
//    }
//
//    public void switchToDefaultContent() {
//        driver.switchTo().defaultContent();
//    }
//
//}

package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GeneralPage {
    protected WebDriver driver;

    public GeneralPage(WebDriver driver) {
        this.driver = driver;
    }

    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    protected boolean isDisplayed(By locator) {
        try {
            return find(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isEnabled(By locator) {
        try {
            return find(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    protected void type(By locator, String text) {
        WebElement element = find(locator);
        if (isDisplayed(locator) && isEnabled(locator)) {
            try {
                element.clear();
                element.sendKeys(text);
            } catch (Exception e) {
                // Tái tìm phần tử nếu lỗi
                element = find(locator);
                if (isDisplayed(locator) && isEnabled(locator)) {
                    element.clear();
                    element.sendKeys(text);
                } else {
                    throw new RuntimeException("Element not interactable after retry: " + e.getMessage(), e);
                }
            }
        } else {
            throw new RuntimeException("Element not displayed or enabled");
        }
    }

    protected void click(By locator) {
        WebElement element = find(locator);
        if (isDisplayed(locator) && isEnabled(locator)) {
            element.click();
        } else {
            throw new RuntimeException("Element not displayed or enabled for click");
        }
    }
}

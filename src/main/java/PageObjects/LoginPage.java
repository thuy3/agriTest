//package PageObjects;
//
//import Common.Constant.Constant;
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebDriver;
//import java.util.logging.Logger;
//
//public class LoginPage extends GeneralPage {
//    private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());
//    private final By txtUsername = By.id("inp_Username");
//    private final By txtPassword = By.id("inp_Password");
//    private final By btnLogin = By.id("btn_Submit");
//    private final By popupCloseButton = By.xpath("//div[contains(@class, 'guard-popup__close')]");
//
//    public LoginPage(WebDriver driver) {
//        super(driver);
//    }
//
//    public void login() {
//        try {
//            // Kiểm tra và đóng guard-popup
//            LOGGER.info("Checking for guard-popup...");
//            if (isDisplayed(popupCloseButton)) {
//                LOGGER.info("Guard-popup found, closing it.");
//                click(popupCloseButton);
//                Thread.sleep(4000); // Đợi 4 giây sau khi đóng popup để DOM ổn định
//            } else {
//                LOGGER.info("No guard-popup found.");
//            }
//
//            // Chờ và kiểm tra username field
//            LOGGER.info("Waiting for username field...");
//            int maxAttempts = 20; // Tối đa 20 giây
//            boolean usernameReady = false;
//            for (int i = 0; i < maxAttempts; i++) {
//                if (isDisplayed(txtUsername) && isEnabled(txtUsername)) {
//                    usernameReady = true;
//                    break;
//                }
//                Thread.sleep(1000); // Chờ 1 giây mỗi lần
//            }
//            if (usernameReady) {
//                LOGGER.info("Username field found and enabled.");
//                // Đảm bảo phần tử hiển thị và tương tác được
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", find(txtUsername));
//                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block'; arguments[0].removeAttribute('disabled');", find(txtUsername));
//                type(txtUsername, Constant.USERNAME);
//            } else {
//                throw new RuntimeException("Username field not found, not displayed, or not enabled after 20 seconds");
//            }
//
//            // Chờ và kiểm tra password field
//            LOGGER.info("Waiting for password field...");
//            Thread.sleep(1000); // Đợi thêm 1 giây
//            if (isDisplayed(txtPassword) && isEnabled(txtPassword)) {
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", find(txtPassword));
//                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block'; arguments[0].removeAttribute('disabled');", find(txtPassword));
//                type(txtPassword, Constant.PASSWORD);
//            } else {
//                throw new RuntimeException("Password field not found, not displayed, or not enabled");
//            }
//
//            // Chờ và nhấn login button
//            LOGGER.info("Waiting for login button...");
//            Thread.sleep(1000); // Đợi thêm 1 giây
//            if (isDisplayed(btnLogin) && isEnabled(btnLogin)) {
//                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", find(btnLogin));
//                click(btnLogin);
//            } else {
//                throw new RuntimeException("Login button not found, not displayed, or not enabled");
//            }
//
//            Thread.sleep(2000); // Chờ sau đăng nhập
//            LOGGER.info("Login completed.");
//        } catch (Exception e) {
//            LOGGER.severe("Login failed: " + e.getMessage());
//            throw new RuntimeException("Login failed: " + e.getMessage(), e);
//        }
//    }
//}

package PageObjects;

import Common.Constant.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.logging.Logger;

public class LoginPage extends GeneralPage {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());
    private final By txtUsername = By.id("inp_Username");
    private final By txtPassword = By.id("inp_Password");
    private final By btnLogin = By.id("btn_Submit");
    private final By popupCloseButton = By.xpath("//div[contains(@class, 'guard-popup__close')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login() {
        try {
            // Kiểm tra và đóng guard-popup
            LOGGER.info("Checking for guard-popup...");
            if (isDisplayed(popupCloseButton)) {
                LOGGER.info("Guard-popup found, closing it.");
                click(popupCloseButton);
                Thread.sleep(4000); // Đợi DOM ổn định sau khi đóng popup
            } else {
                LOGGER.info("No guard-popup found.");
            }

            // Chờ và nhập username
            LOGGER.info("Waiting for username field...");
            waitForElement(txtUsername, 20);
            if (isDisplayed(txtUsername) && isEnabled(txtUsername)) {
                type(txtUsername, Constant.USERNAME);
            } else {
                throw new RuntimeException("Username field not interactable");
            }

            // Chờ và nhập password
            LOGGER.info("Waiting for password field...");
            waitForElement(txtPassword, 10);
            if (isDisplayed(txtPassword) && isEnabled(txtPassword)) {
                type(txtPassword, Constant.PASSWORD);
            } else {
                throw new RuntimeException("Password field not interactable");
            }

            // Chờ và nhấn login
            LOGGER.info("Waiting for login button...");
            waitForElement(btnLogin, 10);
            if (isDisplayed(btnLogin) && isEnabled(btnLogin)) {
                click(btnLogin);
            } else {
                throw new RuntimeException("Login button not interactable");
            }

            Thread.sleep(2000); // Chờ sau đăng nhập
            LOGGER.info("Login completed.");
        } catch (Exception e) {
            LOGGER.severe("Login failed: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    private void waitForElement(By locator, int maxSeconds) {
        int attempts = 0;
        while (attempts < maxSeconds) {
            try {
                if (isDisplayed(locator) && isEnabled(locator)) {
                    return;
                }
            } catch (Exception e) {
               
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            attempts++;
        }
    }
}
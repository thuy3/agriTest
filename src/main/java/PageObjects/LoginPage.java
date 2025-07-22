package PageObjects;

import Common.Constant.Constant;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class LoginPage extends GeneralPage {
    private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());

    private final By txtUsername = By.id("inp_Username");
    private final By txtPassword = By.id("inp_Password");
    private final By btnLogin = By.id("btn_Submit");
    private final By loadingOverlay = By.className("loading");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        try {
            waitForOverlayToDisappear(wait);

            if (!isPageStable(wait)) {
                LOGGER.warning("Trang không ổn định, thử đợi thêm...");
                Thread.sleep(1000); // Đợi 1s để trang ổn định
            }
            fillInputField(wait, txtUsername, Constant.USERNAME, "Username");
            fillInputField(wait, txtPassword, Constant.PASSWORD, "Password");
            Thread.sleep(1000);
            waitForOverlayToDisappear(wait);
            clickButton(wait, btnLogin, "Login");

            // Xác nhận đăng nhập bằng URL
            LOGGER.info("Đang đợi URL chứa 'man/'...");
            wait.until(ExpectedConditions.urlContains("man/"));
            LOGGER.info("Login completed successfully! Navigated to https://agri.inotev.net/man/");
        } catch (Exception e) {
            LOGGER.severe("Login failed: " + e.getMessage());
            capturePageSource("src/test/resources/error_page_login.html");
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    private void waitForOverlayToDisappear(WebDriverWait wait) {
        try {
            LOGGER.info("Waiting for loading overlay to disappear...");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));
            LOGGER.info("Loading overlay disappeared");
        } catch (TimeoutException e) {
            LOGGER.warning("Loading overlay still visible after timeout: " + e.getMessage());
        }
    }

    private void fillInputField(WebDriverWait wait, By locator, String value, String fieldName) {
        int maxRetries = 3;
        int attempt = 1;
        while (attempt <= maxRetries) {
            try {
                LOGGER.info("Lần thử " + attempt + ": Đợi trường " + fieldName + "...");
                WebElement field = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                wait.until(ExpectedConditions.elementToBeClickable(locator));

                if (isInteractable(field)) {
                    field.clear();
                    field.sendKeys(value);
                    LOGGER.info(fieldName + " đã điền giá trị.");
                    return;
                } else {
                    logElementError(locator, fieldName);
                    throw new RuntimeException(fieldName + " không thể tương tác");
                }
            } catch (StaleElementReferenceException e) {
                LOGGER.warning("Element stale ở lần thử " + attempt + " cho " + fieldName + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    LOGGER.severe("Lỗi khi tương tác với " + fieldName + " sau " + maxRetries + " lần thử: " + e.getMessage());
                    throw new RuntimeException(fieldName + " không thể tương tác sau " + maxRetries + " lần thử", e);
                }
                attempt++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    LOGGER.severe("Bị gián đoạn khi retry: " + ie.getMessage());
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                LOGGER.severe("Lỗi khi tương tác với " + fieldName + ": " + e.getMessage());
                throw e;
            }
        }
    }

    private void clickButton(WebDriverWait wait, By locator, String buttonName) {
        try {
            LOGGER.info("Waiting for " + buttonName + " button...");
            WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            wait.until(ExpectedConditions.elementToBeClickable(locator));

            if (isInteractable(button)) {
                button.click();
                LOGGER.info(buttonName + " button clicked.");
                waitForOverlayToDisappear(wait);
            } else {
                logElementError(locator, buttonName);
                throw new RuntimeException(buttonName + " button not interactable");
            }
        } catch (Exception e) {
            LOGGER.severe("Error clicking " + buttonName + " button: " + e.getMessage());
            throw e;
        }
    }

    private boolean isInteractable(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (StaleElementReferenceException e) {
            LOGGER.warning("Element bị stale khi kiểm tra tương tác: " + e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.warning("Lỗi khi kiểm tra tương tác: " + e.getMessage());
            return false;
        }
    }

    private boolean isPageStable(WebDriverWait wait) {
        try {
            String initialPageSource = driver.getPageSource();
            wait.until(driver -> driver.getPageSource().equals(initialPageSource));
            LOGGER.info("Trang ổn định, không phát hiện refresh.");
            return true;
        } catch (TimeoutException e) {
            LOGGER.warning("Trang không ổn định, có thể đã refresh: " + e.getMessage());
            return false;
        }
    }

    private void logElementError(By locator, String name) {
        try {
            WebElement el = driver.findElement(locator);
            LOGGER.severe(name + " attributes: disabled=" + el.getAttribute("disabled") +
                    ", readonly=" + el.getAttribute("readonly") +
                    ", displayed=" + el.isDisplayed() +
                    ", enabled=" + el.isEnabled());
        } catch (Exception ex) {
            LOGGER.severe("Could not log element state for " + name + ": " + ex.getMessage());
        }
    }

    private void capturePageSource(String filePath) {
        try {
            // Kiểm tra thư mục đích
            String dirPath = "src/test/resources";
            Files.createDirectories(Paths.get(dirPath));
            String pageSource = driver.getPageSource();
            Files.write(
                    Paths.get(filePath),
                    pageSource.getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
            LOGGER.info("Đã lưu HTML vào: " + filePath);
        } catch (Exception e) {
            LOGGER.severe("Không thể lưu HTML vào " + filePath + ": " + e.getMessage());
        }
    }
}
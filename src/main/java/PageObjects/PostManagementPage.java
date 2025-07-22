package PageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Logger;

public abstract class PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(PostManagementPage.class.getName());
    protected WebDriver driver;
    protected WebDriverWait wait;

    // Locator for parent menu
    private final By postManagementMenu = By.xpath("//span[text()='Quản lý bài viết']/parent::a");
    private final By subMenuContainer = By.xpath("//a[@data-route='VI_MAIN/nso_post_forum']");

    public PostManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToPostManagement() {
        try {
            LOGGER.info("Đang điều hướng đến menu Quản lý bài viết...");
            int retries = 5;
            for (int i = 1; i <= retries; i++) {
                try {
                    LOGGER.info("Lần thử " + i + ": Tìm menu Quản lý bài viết...");
                    WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(postManagementMenu));
                    String menuHtml = menu.getAttribute("outerHTML");
                    LOGGER.info("HTML của menu Quản lý bài viết: " + menuHtml);
                    String ariaExpanded = menu.getAttribute("aria-expanded");
                    LOGGER.info("Trạng thái aria-expanded: " + ariaExpanded);

                    LOGGER.info("Menu Quản lý bài viết hiển thị, đang click...");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", menu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);

                    LOGGER.info("Đợi sub-menu Diễn đàn hiển thị...");
                    WebElement subMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(subMenuContainer));
                    LOGGER.info("HTML của sub-menu: " + subMenu.getAttribute("outerHTML"));
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + i + ": Lỗi khi mở menu: " + e.getMessage());
                    if (i == retries) {
                        throw new RuntimeException("Không thể mở menu Quản lý bài viết sau " + retries + " lần thử: " + e.getMessage(), e);
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        LOGGER.warning("Lỗi khi chờ retry: " + ie.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Không thể điều hướng đến menu Quản lý bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_page_post_management_" + System.currentTimeMillis() + ".html");
            throw new RuntimeException("Không thể điều hướng đến menu Quản lý bài viết", e);
        }
    }

    protected WebElement waitForElementNotStale(By locator) {
        int retries = 3;
        for (int i = 1; i <= retries; i++) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                LOGGER.info("Tìm thấy phần tử: " + locator);
                return element;
            } catch (StaleElementReferenceException | TimeoutException e) {
                LOGGER.warning("Lần thử " + i + ": Lỗi khi tìm " + locator + ": " + e.getMessage());
                if (i == retries) {
                    capturePageSource("C:\\test\\resources\\error_stale_element_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".html");
                    throw new RuntimeException("Không tìm thấy phần tử sau " + retries + " lần thử: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    LOGGER.warning("Lỗi khi chờ retry: " + ie.getMessage());
                }
            }
        }
        return null;
    }

    public void capturePageSource(String filePath) {
        try {
            String directoryPath = "C:\\test\\resources";
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(directoryPath));
            String pageSource = driver.getPageSource();
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filePath),
                    pageSource.getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
            LOGGER.info("Đã lưu HTML vào: " + filePath);
        } catch (Exception e) {
            LOGGER.severe("Không thể lưu HTML vào " + filePath + ": " + e.getMessage());
        }
    }

    protected void logElementError(By locator, String elementName) {
        try {
            WebElement el = driver.findElement(locator);
            LOGGER.severe(elementName + " attributes: disabled=" + el.getAttribute("disabled") +
                    ", readonly=" + el.getAttribute("readonly") +
                    ", displayed=" + el.isDisplayed() +
                    ", enabled=" + el.isEnabled());
        } catch (Exception ex) {
            LOGGER.severe("Could not log element state for " + elementName + ": " + ex.getMessage());
        }
    }
}
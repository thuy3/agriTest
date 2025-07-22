package PageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ForumPage extends PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(ForumPage.class.getName());

    // Locators dựa trên HTML của danh sách và form thêm mới diễn đàn
    private final By forumSubMenu = By.xpath("//a[@data-route='VI_MAIN/nso_post_forum']");
    private final By newsPostSubMenu = By.xpath("//span[text()='Bài viết/Tin tức']/parent::a");
    private final By addNewButton = By.id("btn_new_entity");
    private final By addFileButton = By.id("btn_add_doc");
    private final By titleField = By.id("title");
    private final By typeSelect = By.id("typ02");
    private final By languageSelect = By.id("typ03");
    private final By statusSelect = By.id("stat01");
    private final By audienceSelect = By.id("stat02");
    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
    private final By searchInput = By.id("inp_search");
    private final By clearSearchIcon = By.id("clear_icon");
    private final By filterIcon = By.id("filter_icon");
    private final By statusFilterOptions = By.xpath("//ul[@class='dropdown-menu dropdown-menu-right']//div[@class='dropdown-item cursor-pointer font-size-18 user-typ-select']");
    private final By fileUploadInput = By.xpath("//input[@data-name='files']");
    private final By removeFileButton = By.xpath("//button[contains(text(), 'Remove file')]");
    private final By confirmButton = By.xpath("//button[contains(text(), 'Đồng ý')]");
    //private final By alertMessage = By.xpath("//div[contains(@class, 'alert')]");
    private final By alertMessage = By.xpath("//div[contains(@class, 'notifyjs-bootstrap-success')]//span[@data-notify-text]");

    public ForumPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(90));
    }

    public WebElement waitForElementNotStale(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void navigateToForum() {
        try {
            LOGGER.info("Điều hướng đến Diễn đàn...");
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(forumSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(addNewButton));
                    LOGGER.info("Đã điều hướng đến Diễn đàn thành công.");
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi click menu Diễn đàn: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể click menu Diễn đàn sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến Diễn đàn: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_forum_" + System.currentTimeMillis() + ".html");
            throw new RuntimeException("Lỗi điều hướng đến Diễn đàn", e);
        }
    }
    public void navigateToAnotherMenu(String menuName) {
        try {
            LOGGER.info("Điều hướng đến menu: " + menuName);
            By menuLocator = By.xpath("//span[text()='" + menuName + "']/parent::a");
            WebElement menu = waitForElementNotStale(menuLocator);
            if (menu.isDisplayed() && menu.isEnabled()) {
                LOGGER.info("Click vào menu: " + menuName);
                Actions actions = new Actions(driver);
                actions.moveToElement(menu).pause(Duration.ofSeconds(2)).click().perform();
                LOGGER.info("Đã điều hướng đến menu " + menuName + " thành công.");
            } else {
                throw new RuntimeException("Không thể tương tác với menu: " + menuName);
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến menu " + menuName + ": " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_navigate_menu_" + menuName.replaceAll("[^a-zA-Z0-9]", "_") + ".html");
            throw e;
        }
    }

    public void clickAddNew() {
        try {
            WebElement addButton = waitForElementNotStale(addNewButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
            addButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
            LOGGER.info("Đã nhấn nút Thêm mới.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Thêm mới: " + e.getMessage());
            throw e;
        }
    }

    public void clickAddFile() {
        try {
            WebElement addFileBtn = waitForElementNotStale(addFileButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addFileBtn);
            addFileBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(fileUploadInput));
            LOGGER.info("Đã nhấn nút Thêm tệp.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Thêm tệp: " + e.getMessage());
            throw e;
        }
    }

    public void fillForumInfo(Map<String, String> forumData) {
        try {
            if (forumData.containsKey("title")) {
                WebElement title = waitForElementNotStale(titleField);
                title.clear();
                title.sendKeys(forumData.get("title"));
            }
            if (forumData.containsKey("type")) {
                WebElement type = waitForElementNotStale(typeSelect);
                Select typeDropdown = new Select(type);
                typeDropdown.selectByValue(forumData.get("type"));
            }
            if (forumData.containsKey("language")) {
                WebElement language = waitForElementNotStale(languageSelect);
                Select languageDropdown = new Select(language);
                languageDropdown.selectByValue(forumData.get("language"));
            }
            if (forumData.containsKey("status")) {
                WebElement status = waitForElementNotStale(statusSelect);
                Select statusDropdown = new Select(status);
                statusDropdown.selectByValue(forumData.get("status"));
            }
            if (forumData.containsKey("audience")) {
                WebElement audience = waitForElementNotStale(audienceSelect);
                Select audienceDropdown = new Select(audience);
                audienceDropdown.selectByValue(forumData.get("audience"));
            }
            LOGGER.info("Đã điền thông tin diễn đàn.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điền thông tin: " + e.getMessage());
            throw e;
        }
    }

    public String clickSave() {
        try {
            WebElement saveBtn = waitForElementNotStale(saveButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveBtn);
            saveBtn.click();
            try {
                WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
                String alertText = alert.getText();
                LOGGER.info("Thông báo sau khi lưu: " + alertText);
                return alertText;
            } catch (TimeoutException e) {
                LOGGER.info("Không có thông báo xuất hiện sau khi lưu.");
                return "";
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Lưu: " + e.getMessage());
            throw e;
        }
    }

    public boolean isRequiredFieldAlertDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Thông tin bắt buộc!')]")));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo bắt buộc.");
            return false;
        }
    }

    public boolean isExitWarningPopupDisplayed() {
        try {
            WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Thoát sẽ mất dữ liệu, bạn chắc không?')]")));
            return popup.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy popup cảnh báo (Bug_09).");
            return false;
        }
    }

    public boolean checkFilterButtonDisplayed() {
        try {
            WebElement filterBtn = waitForElementNotStale(filterIcon);
            filterBtn.click();
            List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(statusFilterOptions));
            String[] expectedStatuses = {"Nháp", "Chờ duyệt", "Duyệt nội dung", "Được hiển thị", "Không duyệt", "Ẩn", "Tất cả"};
            for (String status : expectedStatuses) {
                boolean found = options.stream().anyMatch(opt -> opt.getText().equals(status));
                if (!found) {
                    LOGGER.severe("Trạng thái không hiển thị: " + status);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra nút lọc: " + e.getMessage());
            return false;
        }
    }

    public boolean searchForum(String keyword) {
        try {
            WebElement search = waitForElementNotStale(searchInput);
            search.clear();
            search.sendKeys(keyword);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='ul-list']//h6[contains(text(), '" + keyword + "')]")));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm: " + e.getMessage());
            return false;
        }
    }

    public void openForumDetails(String title) {
        WebElement forumItem = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[@id='ul-list']//h6[contains(text(), '" + title + "')]")));
        forumItem.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='card-body']")));
    }

    public String uploadFile(String filePath) {
        try {
            clickAddFile();
            WebElement uploadInput = waitForElementNotStale(fileUploadInput);
            uploadInput.sendKeys(filePath);
            WebElement confirmBtn = waitForElementNotStale(confirmButton);
            confirmBtn.click();
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Cập nhật thành công!')]")));
            String alertText = alert.getText();
            LOGGER.info("Thông báo sau khi upload: " + alertText);
            return alertText;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi upload file: " + e.getMessage());
            return "";
        }
    }

    public String removeUploadedFile() {
        try {
            WebElement removeBtn = waitForElementNotStale(removeFileButton);
            removeBtn.click();
            WebElement confirmBtn = waitForElementNotStale(confirmButton);
            confirmBtn.click();
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Các tệp tin đã được xóa thành công')]")));
            String alertText = alert.getText();
            LOGGER.info("Thông báo sau khi xóa file: " + alertText);
            return alertText;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi xóa file: " + e.getMessage());
            return "";
        }
    }

    public String uploadLargeFile(String filePath) {
        try {
            clickAddFile();
            WebElement uploadInput = waitForElementNotStale(fileUploadInput);
            uploadInput.sendKeys(filePath);
            WebElement confirmBtn = waitForElementNotStale(confirmButton);
            confirmBtn.click();
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Dung lượng file quá lớn')]")));
            String alertText = alert.getText();
            LOGGER.info("Thông báo sau khi upload file lớn: " + alertText);
            return alertText;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi upload file lớn: " + e.getMessage());
            return "";
        }
    }

    public boolean verifyDateValidation(String publishDate) {
        try {
            WebElement title = waitForElementNotStale(titleField);
            title.sendKeys("Test Forum");
            WebElement type = waitForElementNotStale(typeSelect);
            Select typeDropdown = new Select(type);
            typeDropdown.selectByValue("105100");
            WebElement audience = waitForElementNotStale(audienceSelect);
            Select audienceDropdown = new Select(audience);
            audienceDropdown.selectByValue("100");
            ((JavascriptExecutor) driver).executeScript("document.getElementById('publishDate').value = '" + publishDate + "';");
            WebElement saveBtn = waitForElementNotStale(saveButton);
            saveBtn.click();
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra ngày đăng: " + e.getMessage());
            return false;
        }
    }
}

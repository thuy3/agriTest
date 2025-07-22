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

public class ForumGroupPage extends PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(ForumGroupPage.class.getName());

    // Locators dựa trên HTML của trang Nhóm diễn đàn và trang chi tiết
    private final By forumGroupSubMenu = By.xpath("//span[text()='Nhóm diễn đàn']/parent::a");
    private final By newsPostSubMenu = By.xpath("//span[text()='Bài viết/Tin tức']/parent::a");
    private final By addNewButton = By.id("btn_new_entity");
    private final By addFileButton = By.id("btn_add_doc");
    private final By codeField = By.xpath("//input[@data-name='code01']");
    private final By groupNameField = By.xpath("//input[@data-name='name']");
    private final By typeSelect = By.xpath("//select[@data-name='typ02']");
    private final By summaryField = By.xpath("//textarea[@data-name='introduce']");
    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
    private final By searchInput = By.id("inp_search");
    private final By clearSearchIcon = By.id("clear_icon");
    private final By groupList = By.xpath("//ul[@id='ul-list']//li[@class='entity-item']");
    private final By groupDetailButton = By.xpath(".//span[contains(@class, 'badge-info') and text()='Xem chi tiết']");
    private final By dropdownToggle = By.xpath("//a[contains(@class, 'dropdown-toggle card-drop action-item-duplicate')]");
    private final By editButton = By.id("btn_edit");
    private final By deleteButton = By.id("btn_del");
    private final By fileUploadInput = By.xpath("//input[@data-name='files']");
    private final By removeFileButton = By.xpath("//button[contains(text(), 'Remove file')]"); // Giả định
    private final By confirmButton = By.xpath("//button[contains(text(), 'Đồng ý')]");
    private final By paginationLinks = By.xpath("//div[@id='div_group_pagination']//li[contains(@class, 'paginationjs-page')]/a");
    private final By alertMessage = By.xpath("//div[contains(@class, 'alert')]");

    public ForumGroupPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(90));
    }

    // Phương thức waitForElementNotStale để tránh lỗi StaleElementReferenceException
    protected WebElement waitForElementNotStale(By locator) {
        int retries = 5;
        WebElement element = null;
        for (int i = 1; i <= retries; i++) {
            try {
                element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                wait.until(ExpectedConditions.visibilityOf(element));
                wait.until(ExpectedConditions.elementToBeClickable(element));
                element.isDisplayed();
                LOGGER.info("Tìm thấy phần tử: " + locator);
                return element;
            } catch (StaleElementReferenceException | TimeoutException e) {
                LOGGER.warning("Lần thử " + i + ": Lỗi khi tìm " + locator + ": " + e.getMessage());
                if (i == retries) {
                    capturePageSource("C:\\test\\resources\\error_stale_element_" + locator.toString().replaceAll("[^a-zA-Z0-9]", "_") + ".html");
                    throw new RuntimeException("Không tìm thấy phần tử sau " + retries + " lần thử: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    LOGGER.warning("Lỗi khi chờ retry: " + ie.getMessage());
                }
            }
        }
        return element;
    }

    // Điều hướng đến Nhóm diễn đàn (QLBV_NDD-20 đến QLBV_NDD-38)
    public void navigateToForumGroup() {
        try {
            LOGGER.info("Điều hướng đến Nhóm diễn đàn...");
            navigateToPostManagement();
            WebElement subMenu = waitForElementNotStale(forumGroupSubMenu);
            if (subMenu.isDisplayed() && subMenu.isEnabled()) {
                LOGGER.info("Click vào Nhóm diễn đàn...");
                Actions actions = new Actions(driver);
                actions.moveToElement(subMenu).pause(Duration.ofSeconds(2)).click().perform();
                wait.until(ExpectedConditions.visibilityOfElementLocated(addNewButton));
                LOGGER.info("Đã điều hướng đến Nhóm diễn đàn thành công.");
            } else {
                throw new RuntimeException("Không thể tương tác với menu Nhóm diễn đàn.");
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến Nhóm diễn đàn: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_forum_group.html");
            throw e;
        }
    }

    // Điều hướng đến menu khác (QLBV_NDD-25)
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

    // Nhấn nút Thêm mới (QLBV_NDD-20, QLBV_NDD-25, QLBV_NDD-32 đến QLBV_NDD-38)
    public void clickAddNew() {
        try {
            WebElement addButton = waitForElementNotStale(addNewButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
            addButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(codeField));
            LOGGER.info("Đã nhấn nút Thêm mới.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Thêm mới: " + e.getMessage());
            throw e;
        }
    }

    // Nhấn nút Thêm tệp (QLBV_NDD-32)
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

    // Điền thông tin nhóm diễn đàn (QLBV_NDD-20, QLBV_NDD-21)
    public void fillGroupInfo(Map<String, String> groupData) {
        try {
            if (groupData.containsKey("code")) {
                WebElement code = waitForElementNotStale(codeField);
                code.clear();
                code.sendKeys(groupData.get("code"));
            }
            if (groupData.containsKey("group_name")) {
                WebElement nameField = waitForElementNotStale(groupNameField);
                nameField.clear();
                nameField.sendKeys(groupData.get("group_name"));
            }
            if (groupData.containsKey("type")) {
                WebElement type = waitForElementNotStale(typeSelect);
                Select typeDropdown = new Select(type);
                typeDropdown.selectByValue(groupData.get("type"));
            }
            if (groupData.containsKey("summary")) {
                WebElement summary = waitForElementNotStale(summaryField);
                summary.clear();
                summary.sendKeys(groupData.get("summary"));
            }
            LOGGER.info("Đã điền thông tin nhóm diễn đàn.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điền thông tin: " + e.getMessage());
            throw e;
        }
    }

    // Kiểm tra mã quản lý duy nhất (QLBV_NDD-20)
    public boolean isUniqueCodeErrorDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Mã quản lý đã tồn tại')]")));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo mã quản lý không duy nhất.");
            return false;
        }
    }

    // Nhấn nút Lưu (QLBV_NDD-20, QLBV_NDD-21, QLBV_NDD-32 đến QLBV_NDD-38)
    public String clickSave() {
        try {
            WebElement saveBtn = waitForElementNotStale(saveButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveBtn);
            saveBtn.click();
            wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click(); // Nhấn lần 2
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
            LOGGER.severe("Lỗi khi nhấn Lưu (Bug_05): " + e.getMessage());
            throw e;
        }
    }

    // Kiểm tra thông báo bắt buộc (QLBV_NDD-21)
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

    // Chọn nhóm diễn đàn (QLBV_NDD-22, QLBV_NDD-23, QLBV_NDD-24)
    public void selectGroup(String groupName) {
        try {
            WebElement group = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='ul-list']//h5[contains(text(), '" + groupName + "')]")));
            WebElement detailButton = group.findElement(By.xpath("./ancestor::li//span[contains(@class, 'badge-info') and text()='Xem chi tiết']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", detailButton);
            detailButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'group-detail')]")));
            LOGGER.info("Đã chọn nhóm diễn đàn: " + groupName);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi chọn nhóm diễn đàn (Bug_08): " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_select_group.html");
            throw e;
        }
    }

    // Nhấn nút Chỉnh sửa (QLBV_NDD-22)
    public void clickEdit() {
        try {
            WebElement dropdown = waitForElementNotStale(dropdownToggle);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);
            dropdown.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(editButton));
            WebElement editBtn = waitForElementNotStale(editButton);
            editBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(summaryField));
            LOGGER.info("Đã nhấn nút Chỉnh sửa.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Chỉnh sửa (Bug_06): " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_edit_button.html");
            throw e;
        }
    }

    // Nhấn nút Xóa (QLBV_NDD-23)
    public String clickDelete() {
        try {
            WebElement dropdown = waitForElementNotStale(dropdownToggle);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);
            dropdown.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(deleteButton));
            WebElement deleteBtn = waitForElementNotStale(deleteButton);
            deleteBtn.click();
            WebElement confirmBtn = waitForElementNotStale(confirmButton);
            confirmBtn.click(); // Nhấn Đồng ý để xác nhận xóa
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Xóa nhóm diễn đàn thành công!')]")));
            String alertText = alert.getText();
            LOGGER.info("Thông báo sau khi xóa: " + alertText);
            return alertText;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Xóa (Bug_07): " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_delete_button.html");
            throw e;
        }
    }

    // Xem chi tiết nhóm diễn đàn (QLBV_NDD-24)
    public boolean isGroupDetailDisplayed() {
        try {
            WebElement detailPage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'group-detail')]")));
            return detailPage.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy trang chi tiết nhóm diễn đàn (Bug_08).");
            return false;
        }
    }

    // Kiểm tra popup cảnh báo khi thoát (QLBV_NDD-25)
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

    // Kiểm tra phân trang (QLBV_NDD-26)
    public boolean verifyPagination() {
        try {
            List<WebElement> pages = driver.findElements(paginationLinks);
            if (pages.size() < 2) {
                LOGGER.info("Không đủ trang để kiểm tra phân trang.");
                return false;
            }
            WebElement page2 = pages.get(1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", page2);
            page2.click();
            wait.until(ExpectedConditions.urlContains("page=2"));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra phân trang: " + e.getMessage());
            return false;
        }
    }

    // Tìm kiếm nhóm diễn đàn (QLBV_NDD-27)
    public boolean searchGroup(String keyword) {
        try {
            WebElement search = waitForElementNotStale(searchInput);
            search.clear();
            search.sendKeys(keyword);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='ul-list']//h5[contains(text(), '" + keyword + "')]")));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm: " + e.getMessage());
            return false;
        }
    }

    // Upload file hợp lệ (QLBV_NDD-32, QLBV_NDD-35, QLBV_NDD-36)
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

    // Xóa file đã upload (QLBV_NDD-33)
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

    // Upload ảnh hoặc file dung lượng lớn (QLBV_NDD-34, QLBV_NDD-37)
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
            LOGGER.severe("Lỗi khi upload file lớn (Bug_10, Bug_11): " + e.getMessage());
            return "";
        }
    }

    // Nhấn Đồng ý mà không chọn file (QLBV_NDD-31)
    public String clickConfirmWithoutFile() {
        try {
            clickAddFile();
            WebElement confirmBtn = waitForElementNotStale(confirmButton);
            confirmBtn.click();
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Có lỗi xảy ra. Không thể truy xuất dữ liệu!')]")));
            String alertText = alert.getText();
            LOGGER.info("Thông báo khi nhấn Đồng ý mà không chọn file: " + alertText);
            return alertText;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Đồng ý mà không chọn file: " + e.getMessage());
            return "";
        }
    }
}
package PageObjects;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LegalDocumentPage extends PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(LegalDocumentPage.class.getName());

    // Locators dựa trên HTML tương tự và giả định thêm trường ngày
    private final By legalDocumentSubMenu = By.xpath("//span[text()='Văn bản pháp luật/hành chính']/parent::a");
    private final By newsPostSubMenu = By.xpath("//a[contains(@data-route, 'nso_post_news')]");
    private final By addNewButton = By.id("btn_new_entity");
    private final By addFileButton = By.id("btn_add_doc");
    private final By titleField = By.id("title");
    private final By statusSelect = By.id("stat01");
    private final By reasonField = By.xpath("//textarea[@data-name='reason']");
    private final By publishDateField = By.id("publishDate");
    private final By effectiveDateField = By.id("effectiveDate");
    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
    private final By searchInput = By.id("inp_search");
    private final By clearSearchIcon = By.id("clear_icon");
    private final By filterIcon = By.id("filter_icon");
    private final By statusFilterOptions = By.xpath("//ul[@class='dropdown-menu dropdown-menu-right']//div[@class='dropdown-item cursor-pointer font-size-18 user-typ-select']");
    private final By documentList = By.xpath("//ul[@id='ul-list']//li[@class='entity-item']");
    private final By documentDetailButton = By.xpath(".//span[contains(@class, 'badge-info') and text()='Xem chi tiết']");
    private final By dropdownToggle = By.xpath("//a[contains(@class, 'dropdown-toggle card-drop action-item-duplicate')]");
    private final By editButton = By.id("btn_edit");
    private final By deleteButton = By.id("btn_del");
    private final By fileUploadInput = By.xpath("//input[@data-name='files']");
    private final By removeFileButton = By.xpath("//button[contains(text(), 'Remove file')]");
    private final By confirmButton = By.xpath("//button[contains(text(), 'Đồng ý')]");
    private final By paginationLinks = By.xpath("//div[@id='div_group_pagination']//li[contains(@class, 'paginationjs-page')]/a");
    private final By alertMessage = By.xpath("//div[contains(@class, 'alert')]");

    public LegalDocumentPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(90));
    }
    public void capturePageSource(String filePath) {
        try {
            // Kiểm tra thư mục đích có tồn tại không
            File file = new File(filePath).getParentFile();
            if (!file.exists()) {
                file.mkdirs(); // Tạo thư mục nếu chưa tồn tại
                LOGGER.info("Đã tạo thư mục: " + file.getAbsolutePath());
            }
            String pageSource = driver.getPageSource();
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), pageSource.getBytes());
            LOGGER.info("Đã lưu page source vào: " + filePath);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi lưu page source: " + e.getMessage());
            // Không gọi captureScreenshot trong catch để tránh vòng lặp lỗi
        }
    }

    // Chụp ảnh màn hình khi test fail
    private void captureScreenshot(String filePath) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(source, new File(filePath));
            LOGGER.info("Đã lưu screenshot vào: " + filePath);
        } catch (IOException e) {
            LOGGER.severe("Lỗi khi chụp screenshot: " + e.getMessage());
        }
    }

    public void navigateToLegalDocument() {
        try {
            LOGGER.info("Điều hướng đến Văn bản pháp luật/hành chính...");
            navigateToPostManagement();
            WebElement subMenu = waitForElementNotStale(legalDocumentSubMenu);
            if (subMenu.isDisplayed() && subMenu.isEnabled()) {
                LOGGER.info("Click vào Văn bản pháp luật/hành chính...");
                Actions actions = new Actions(driver);
                actions.moveToElement(subMenu).pause(Duration.ofSeconds(2)).click().perform();
                wait.until(ExpectedConditions.visibilityOfElementLocated(addNewButton));
                LOGGER.info("Đã điều hướng đến Văn bản pháp luật/hành chính thành công.");
            } else {
                throw new RuntimeException("Không thể tương tác với menu Văn bản pháp luật/hành chính.");
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến Văn bản pháp luật/hành chính: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_legal_document.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_legal_document.png");
            throw e;
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
            captureScreenshot("C:\\test\\resources\\screenshots\\error_navigate_menu_" + menuName.replaceAll("[^a-zA-Z0-9]", "_") + ".png");
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
            captureScreenshot("C:\\test\\resources\\screenshots\\error_add_new.png");
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
            captureScreenshot("C:\\test\\resources\\screenshots\\error_add_file.png");
            throw e;
        }
    }

    public void fillDocumentInfo(Map<String, String> documentData) {
        try {
            if (documentData.containsKey("title")) {
                WebElement title = waitForElementNotStale(titleField);
                title.clear();
                title.sendKeys(documentData.get("title"));
            }
            if (documentData.containsKey("status")) {
                WebElement status = waitForElementNotStale(statusSelect);
                String dataCode;
                switch (documentData.get("status")) {
                    case "Tất cả":
                        dataCode = "a";
                        break;
                    case "Nháp":
                        dataCode = "0";
                        break;
                    case "Đang chờ duyệt":
                        dataCode = "5";
                        break;
                    case "Đã duyệt":
                        dataCode = "1";
                        break;
                    case "Không duyệt":
                        dataCode = "-1";
                        break;
                    case "Xóa":
                        dataCode = "10";
                        break;
                    default:
                        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + documentData.get("status"));
                }
                By statusLocator = By.xpath("//div[@class='dropdown-item cursor-pointer font-size-18 typ-select' and @data-code='" + dataCode + "']");
                WebElement statusOption = waitForElementNotStale(statusLocator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", statusOption);
            }
            if (documentData.containsKey("reason")) {
                WebElement reason = waitForElementNotStale(reasonField);
                reason.clear();
                reason.sendKeys(documentData.get("reason"));
            }
            if (documentData.containsKey("publishDate")) {
                ((JavascriptExecutor) driver).executeScript("document.getElementById('publishDate').value = '" + documentData.get("publishDate") + "';");
            }
            if (documentData.containsKey("effectiveDate")) {
                ((JavascriptExecutor) driver).executeScript("document.getElementById('effectiveDate').value = '" + documentData.get("effectiveDate") + "';");
            }
            LOGGER.info("Đã điền thông tin văn bản.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điền thông tin: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_fill_document.png");
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
            captureScreenshot("C:\\test\\resources\\screenshots\\error_save_button.png");
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
            LOGGER.info("Không tìm thấy popup cảnh báo (Bug_13).");
            return false;
        }
    }

    public boolean checkFilterButtonDisplayed() {
        try {
            LOGGER.info("Đang kiểm tra nút lọc trạng thái...");
            navigateToLegalDocument();
            WebElement filterBtn = waitForElementNotStale(filterIcon);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", filterBtn);
            // Sửa CSS để tránh Bug_01
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelector('.dropdown-menu-right').style.zIndex = '10000'; " +
                            "document.querySelector('.dropdown-menu-right').style.maxHeight = 'none'; " +
                            "document.querySelector('.dropdown-menu-right').style.overflow = 'visible';"
            );
            filterBtn.click();
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(statusFilterOptions));
            List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statusFilterOptions));
            List<String> expectedStatuses = Arrays.asList("Tất cả", "Nháp", "Đang chờ duyệt", "Đã duyệt", "Không duyệt", "Xóa");
            boolean allPresent = true;
            LOGGER.info("Số lượng trạng thái tìm thấy: " + options.size());
            for (WebElement option : options) {
                String text = option.getText();
                String dataCode = option.getAttribute("data-code");
                boolean isVisible = option.isDisplayed();
                Rectangle rect = option.getRect();
                LOGGER.info("Trạng thái: " + text + ", data-code: " + dataCode + ", Hiển thị: " + isVisible + ", Vị trí: " + rect);
                if (!isVisible || rect.getHeight() == 0 || rect.getWidth() == 0) {
                    LOGGER.warning("Trạng thái " + text + " có thể bị che (Bug_01)");
                    allPresent = false;
                }
                if (!expectedStatuses.contains(text)) {
                    LOGGER.info("Trạng thái không mong đợi: " + text);
                    allPresent = false;
                }
            }
            if (!allPresent || options.size() != expectedStatuses.size()) {
                LOGGER.warning("Danh sách trạng thái không đầy đủ hoặc có lỗi hiển thị (Bug_01)");
                captureScreenshot("C:\\test\\resources\\screenshots\\filter_button_error.png");
                capturePageSource("C:\\test\\resources\\error_filter_button.html");
            }
            LOGGER.info("Kiểm tra nút lọc trạng thái: " + (allPresent ? "Thành công" : "Thất bại"));
            return allPresent;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra nút lọc trạng thái: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\filter_button_exception.png");
            capturePageSource("C:\\test\\resources\\error_filter_button.html");
            return false;
        }
    }

    public boolean filterByStatus(String status) {
        try {
            LOGGER.info("Đang lọc văn bản theo trạng thái: " + status);
            navigateToLegalDocument();
            int retries = 3;
            boolean success = false;
            for (int i = 1; i <= retries; i++) {
                try {
                    WebElement filterBtn = waitForElementNotStale(filterIcon);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", filterBtn);
                    // Sửa CSS để tránh Bug_01
                    ((JavascriptExecutor) driver).executeScript(
                            "document.querySelector('.dropdown-menu-right').style.zIndex = '10000'; " +
                                    "document.querySelector('.dropdown-menu-right').style.maxHeight = 'none'; " +
                                    "document.querySelector('.dropdown-menu-right').style.overflow = 'visible';"
                    );
                    filterBtn.click();
                    wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(statusFilterOptions));
                    String dataCode;
                    switch (status) {
                        case "Tất cả":
                            dataCode = "a";
                            break;
                        case "Nháp":
                            dataCode = "0";
                            break;
                        case "Đang chờ duyệt":
                            dataCode = "5";
                            break;
                        case "Đã duyệt":
                            dataCode = "1";
                            break;
                        case "Không duyệt":
                            dataCode = "-1";
                            break;
                        case "Xóa":
                            dataCode = "10";
                            break;
                        default:
                            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
                    }
                    By statusLocator = By.xpath("//div[@class='dropdown-item cursor-pointer font-size-18 typ-select' and @data-code='" + dataCode + "']");
                    WebElement statusOption = waitForElementNotStale(statusLocator);
                    Rectangle rect = statusOption.getRect();
                    if (!statusOption.isDisplayed() || rect.getHeight() == 0 || rect.getWidth() == 0) {
                        LOGGER.warning("Trạng thái " + status + " không hiển thị hoặc bị che (Bug_01)");
                        captureScreenshot("C:\\test\\resources\\screenshots\\filter_status_" + status + "_error.png");
                        continue;
                    }
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", statusOption);
                    By statusColumn = By.xpath("//ul[@id='ul-list']//li[@class='entity-item']//span[contains(@class, 'status')]");
                    List<WebElement> statusCells = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statusColumn));
                    boolean allMatch = statusCells.stream().allMatch(cell -> cell.getText().equals(status) || status.equals("Tất cả"));
                    if (!allMatch) {
                        LOGGER.warning("Danh sách văn bản không khớp trạng thái: " + status);
                        captureScreenshot("C:\\test\\resources\\screenshots\\filter_status_" + status + "_mismatch.png");
                    }
                    success = allMatch;
                    break;
                } catch (StaleElementReferenceException e) {
                    LOGGER.warning("Lần thử " + i + ": StaleElementReferenceException khi lọc trạng thái " + status + ": " + e.getMessage());
                    if (i == retries) {
                        captureScreenshot("C:\\test\\resources\\screenshots\\filter_status_" + status + "_stale.png");
                        throw e;
                    }
                    Thread.sleep(1000);
                }
            }
            if (!success) {
                capturePageSource("C:\\test\\resources\\error_filter_status.html");
            }
            LOGGER.info("Kết quả lọc trạng thái " + status + ": " + (success ? "Thành công" : "Thất bại"));
            return success;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi lọc trạng thái: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_filter_status.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_filter_status_" + status + "_exception.png");
            return false;
        }
    }

    public void selectDocument(String title) {
        try {
            WebElement document = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='ul-list']//h5[contains(text(), '" + title + "')]")));
            WebElement detailButton = document.findElement(By.xpath("./ancestor::li//span[contains(@class, 'badge-info') and text()='Xem chi tiết']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", detailButton);
            detailButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'document-detail')]")));
            LOGGER.info("Đã chọn văn bản: " + title);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi chọn văn bản: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_select_document.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_select_document.png");
            throw e;
        }
    }

    public void clickEdit() {
        try {
            WebElement dropdown = waitForElementNotStale(dropdownToggle);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);
            dropdown.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(editButton));
            WebElement editBtn = waitForElementNotStale(editButton);
            editBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
            LOGGER.info("Đã nhấn nút Chỉnh sửa.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Chỉnh sửa: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_edit_button.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_edit_button.png");
            throw e;
        }
    }

    public String clickDelete() {
        try {
            WebElement dropdown = waitForElementNotStale(dropdownToggle);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);
            dropdown.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(deleteButton));
            WebElement deleteBtn = waitForElementNotStale(deleteButton);
            deleteBtn.click();
            WebElement confirmBtn = waitForElementNotStale(confirmButton);
            confirmBtn.click();
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Xóa văn bản thành công!')]")));
            String alertText = alert.getText();
            LOGGER.info("Thông báo sau khi xóa: " + alertText);
            return alertText;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Xóa: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_delete_button.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_delete_button.png");
            throw e;
        }
    }

    public boolean isDocumentDetailDisplayed() {
        try {
            WebElement detailPage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'document-detail')]")));
            return detailPage.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy trang chi tiết văn bản.");
            return false;
        }
    }

    public boolean searchDocument(String keyword) {
        try {
            WebElement search = waitForElementNotStale(searchInput);
            search.clear();
            search.sendKeys(keyword);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='ul-list']//h5[contains(text(), '" + keyword + "')]")));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_search_document.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_search_document.png");
            return false;
        }
    }

    public boolean searchDocumentNoResult(String keyword) {
        try {
            WebElement search = waitForElementNotStale(searchInput);
            search.clear();
            search.sendKeys(keyword);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//li[contains(text(), 'Chưa có dữ liệu')]")));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm không có kết quả: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_search_no_result.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_search_no_result.png");
            return false;
        }
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
            captureScreenshot("C:\\test\\resources\\screenshots\\error_upload_file.png");
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
            captureScreenshot("C:\\test\\resources\\screenshots\\error_remove_file.png");
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
            LOGGER.severe("Lỗi khi upload file lớn (Bug_15, Bug_16): " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_upload_large_file.png");
            return "";
        }
    }

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
            captureScreenshot("C:\\test\\resources\\screenshots\\error_pagination.png");
            return false;
        }
    }

    public boolean changeDocumentStatus(String title, String status) {
        try {
            LOGGER.info("Đang thay đổi trạng thái văn bản: " + title + " sang " + status);
            selectDocument(title);
            clickEdit();
            int retries = 3;
            for (int i = 1; i <= retries; i++) {
                try {
                    WebElement filterBtn = waitForElementNotStale(filterIcon);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", filterBtn);
                    // Sửa CSS để tránh Bug_01
                    ((JavascriptExecutor) driver).executeScript(
                            "document.querySelector('.dropdown-menu-right').style.zIndex = '10000'; " +
                                    "document.querySelector('.dropdown-menu-right').style.maxHeight = 'none'; " +
                                    "document.querySelector('.dropdown-menu-right').style.overflow = 'visible';"
                    );
                    filterBtn.click();
                    wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(statusSelect));
                    String dataCode;
                    switch (status) {
                        case "Tất cả":
                            dataCode = "a";
                            break;
                        case "Nháp":
                            dataCode = "0";
                            break;
                        case "Đang chờ duyệt":
                            dataCode = "5";
                            break;
                        case "Đã duyệt":
                            dataCode = "1";
                            break;
                        case "Không duyệt":
                            dataCode = "-1";
                            break;
                        case "Xóa":
                            dataCode = "10";
                            break;
                        default:
                            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
                    }
                    By statusLocator = By.xpath("//div[@class='dropdown-item cursor-pointer font-size-18 typ-select' and @data-code='" + dataCode + "']");
                    WebElement statusOption = waitForElementNotStale(statusLocator);
                    Rectangle rect = statusOption.getRect();
                    if (!statusOption.isDisplayed() || rect.getHeight() == 0 || rect.getWidth() == 0) {
                        LOGGER.warning("Trạng thái " + status + " không hiển thị hoặc bị che (Bug_01)");
                        captureScreenshot("C:\\test\\resources\\screenshots\\status_dropdown_error_" + status + ".png");
                        continue;
                    }
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", statusOption);
                    clickSave();
                    WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[contains(@class, 'alert') and contains(text(), 'Cập nhật thành công!')]")));
                    LOGGER.info("Thông báo sau khi thay đổi trạng thái: " + alert.getText());
                    return true;
                } catch (StaleElementReferenceException e) {
                    LOGGER.warning("Lần thử " + i + ": StaleElementReferenceException khi chọn trạng thái " + status + ": " + e.getMessage());
                    if (i == retries) {
                        captureScreenshot("C:\\test\\resources\\screenshots\\status_dropdown_stale_" + status + ".png");
                        throw e;
                    }
                    Thread.sleep(1000);
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi thay đổi trạng thái: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_change_document_status.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_change_document_status.png");
            return false;
        }
    }
}
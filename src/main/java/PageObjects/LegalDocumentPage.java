package PageObjects;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LegalDocumentPage extends PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(LegalDocumentPage.class.getName());

    // Locators
    private final By legalDocumentSubMenu = By.xpath("//a[contains(text(), 'Văn bản pháp luật/hành chính')]");
    private final By newsPostSubMenu = By.xpath("//a[@data-route='VI_MAIN/nso_post_news']");
    private final By addNewButton = By.id("btn_new_entity");
    private final By titleField = By.xpath("//input[@data-name='title']");
    private final By typeField = By.xpath("//input[@data-name='type']");
    private final By refField = By.xpath("//input[@data-name='ref']");
    private final By officeField = By.xpath("//input[@data-name='office']");
    private final By fieldField = By.xpath("//input[@data-name='field']");
    private final By locationField = By.xpath("//input[@data-name='location']");
    private final By signatureField = By.xpath("//input[@data-name='signature']");
    private final By issueDateField = By.xpath("//input[@data-name='dt05']");
    private final By effectiveDateField = By.xpath("//input[@data-name='dt03']");
    private final By summaryField = By.xpath("//textarea[@data-name='inf02']");
    private final By statusSelect = By.id("stat01");
    private final By reasonField = By.xpath("//div[@class='note-editable' and @contenteditable='true']");
    private final By modalSaveButton = By.id("btn_msgbox_OK");
    private final By modalCancelButton = By.id("btn_msgbox_NO");
    private final By saveButton = By.id("btn_save_entity");
    private final By searchInput = By.id("inp-search");
    private final By clearSearchIcon = By.id("clear_icon");
    private final By filterIcon = By.xpath("//a[.//i[contains(@class, 'mdi-filter-outline')]]");
    private final By statusFilterOptions = By.xpath("//ul[contains(@class, 'dropdown-menu dropdown-menu-right')]//div[contains(@class, 'dropdown-item cursor-pointer font-size-18 typ-select')]");
    private final By documentList = By.xpath("//ul[@id='ul-list']//li[@class='entity-item']");
    private final By documentDetailButton = By.xpath(".//span[contains(@class, 'badge-info') and text()='Xem chi tiết']");
    private final By dropdownToggle = By.xpath("//a[contains(@class, 'dropdown-toggle card-drop action-item-duplicate')]");
    private final By editButton = By.id("btn_edit");
    private final By deleteButton = By.id("btn_del");
    private final By detailPage = By.xpath("//div[contains(@class, 'd-flex align-items-center justify-content-between') and .//h5[text()='Thông tin văn bản']]");
    private final By confirmButton = By.id("btn_msgbox_OK");
    private final By alertMessage = By.xpath("//div[contains(@class, 'notifyjs-bootstrap-success')]//span[@data-notify-text]");
    private final By confirmModal = By.xpath("//div[@class='modal-content' and .//h4[text()='Xác nhận']]");
    private final By paginationLinks = By.xpath("//div[@class='paginationjs-pages']//li[contains(@class, 'paginationjs-page J-paginationjs-page')]");
    private final By prevButton = By.xpath("//div[@class='paginationjs-pages']//li[contains(@class, 'paginationjs-prev')]");
    private final By nextButton = By.xpath("//div[@class='paginationjs-pages']//li[contains(@class, 'paginationjs-next J-paginationjs-next')]");
    private final By groupList = By.xpath("//ul[@id='ul-list']//li[@class='entity-item cursor-pointer']");

    public LegalDocumentPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void capturePageSource(String filePath) {
        try {
            File file = new File(filePath).getParentFile();
            if (!file.exists()) {
                file.mkdirs();
                LOGGER.info("Đã tạo thư mục: " + file.getAbsolutePath());
            }
            String pageSource = driver.getPageSource();
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), pageSource.getBytes());
            LOGGER.info("Đã lưu page source vào: " + filePath);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi lưu page source: " + e.getMessage());
        }
    }

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
            int retries = 3;
            for (int i = 1; i <= retries; i++) {
                try {
                    LOGGER.info("Lần thử " + i + ": Tìm menu con Văn bản pháp luật/hành chính...");
                    wait.until(ExpectedConditions.visibilityOfElementLocated(legalDocumentSubMenu));
                    WebElement subMenu = waitForElementNotStale(legalDocumentSubMenu);
                    LOGGER.info("HTML của menu con: " + subMenu.getAttribute("outerHTML"));
                    Rectangle rect = subMenu.getRect();
                    LOGGER.info("Kích thước menu con: width=" + rect.getWidth() + ", height=" + rect.getHeight());
                    if (subMenu.isDisplayed() && subMenu.isEnabled() && rect.getHeight() > 0 && rect.getWidth() > 0) {
                        LOGGER.info("Click vào Văn bản pháp luật/hành chính...");
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", subMenu);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                        wait.until(ExpectedConditions.visibilityOfElementLocated(addNewButton));
                        LOGGER.info("Đã điều hướng đến Văn bản pháp luật/hành chính thành công.");
                        return;
                    } else {
                        LOGGER.warning("Menu con không hiển thị hoặc không thể tương tác: displayed=" + subMenu.isDisplayed() + ", enabled=" + subMenu.isEnabled());
                        if (i == retries) {
                            throw new RuntimeException("Không thể tương tác với menu Văn bản pháp luật/hành chính sau " + retries + " lần thử.");
                        }
                        Thread.sleep(100);
                    }
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + i + ": Lỗi khi tương tác với menu con: " + e.getMessage());
                    if (i == retries) {
                        throw new RuntimeException("Không thể tương tác với menu Văn bản pháp luật/hành chính sau " + retries + " lần thử: " + e.getMessage(), e);
                    }
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến Văn bản pháp luật/hành chính: " + e.getMessage());
            throw new RuntimeException("Lỗi khi điều hướng tới Văn bản pháp luật/hành chính", e);
        }
    }

    public void clickAddNew() {
        try {
            LOGGER.info("Click nút Thêm mới...");
            WebElement addButton = waitForElementNotStale(addNewButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", addButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
            wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
            LOGGER.info("Đã mở form thêm mới.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Thêm mới: " + e.getMessage());
            throw e;
        }
    }

    public void clickEdit() {
        try {
            LOGGER.info("Click nút Chỉnh sửa...");
            WebElement editBtn = waitForElementNotStale(editButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", editBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);
            wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
            LOGGER.info("Đã mở form chỉnh sửa.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi click nút Chỉnh sửa: " + e.getMessage());
            throw e;
        }
    }

    public void fillDocumentInfo(Map<String, String> documentData) {
        try {
            LOGGER.info("Điền thông tin văn bản...");
            if (documentData.containsKey("title")) {
                fillInputField(titleField, documentData.get("title"), "Tiêu đề");
            }
            if (documentData.containsKey("type")) {
                fillInputField(typeField, documentData.get("type"), "Loại văn bản");
            }
            if (documentData.containsKey("ref")) {
                fillInputField(refField, documentData.get("ref"), "Số/Viết tắt loại văn bản");
            }
            if (documentData.containsKey("office")) {
                fillInputField(officeField, documentData.get("office"), "Cơ quan ban hành");
            }
            if (documentData.containsKey("field")) {
                fillInputField(fieldField, documentData.get("field"), "Lĩnh vực liên quan");
            }
            if (documentData.containsKey("location")) {
                fillInputField(locationField, documentData.get("location"), "Địa danh");
            }
            if (documentData.containsKey("signature")) {
                fillInputField(signatureField, documentData.get("signature"), "Chữ ký");
            }
            if (documentData.containsKey("publishDate")) {
                fillDateField(issueDateField, documentData.get("publishDate"), "Ngày ban hành");
            }
            if (documentData.containsKey("effectiveDate")) {
                fillDateField(effectiveDateField, documentData.get("effectiveDate"), "Ngày hiệu lực bắt đầu");
            }
            if (documentData.containsKey("summary")) {
                fillInputField(summaryField, documentData.get("summary"), "Nội dung tóm tắt");
            }
            if (documentData.containsKey("reason")) {
                fillReasonField(documentData.get("reason"));
            }
            LOGGER.info("Đã điền thông tin văn bản.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điền form: " + e.getMessage());
            throw e;
        }
    }

    private void fillInputField(By locator, String value, String fieldName) {
        int retries = 3;
        for (int i = 1; i <= retries; i++) {
            try {
                LOGGER.info("Lần thử " + i + ": Điền " + fieldName + "...");
                WebElement field = waitForElementNotStale(locator);
                wait.until(ExpectedConditions.elementToBeClickable(field));
                field.clear();
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", field, value);
                LOGGER.info(fieldName + " đã điền giá trị: " + value);
                return;
            } catch (StaleElementReferenceException | TimeoutException e) {
                LOGGER.warning("Lần thử " + i + ": Lỗi khi điền " + fieldName + ": " + e.getMessage());
                if (i == retries) {
                    throw new RuntimeException("Không thể điền " + fieldName + " sau " + retries + " lần thử", e);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    LOGGER.warning("Lỗi khi chờ retry: " + ie.getMessage());
                }
            }
        }
    }

    private void fillDateField(By locator, String value, String fieldName) {
        int retries = 3;
        for (int i = 1; i <= retries; i++) {
            try {
                LOGGER.info("Lần thử " + i + ": Điền " + fieldName + "...");
                WebElement field = waitForElementNotStale(locator);
                wait.until(ExpectedConditions.elementToBeClickable(field));
                String formattedDate = value.replaceAll("(\\d{4})-(\\d{2})-(\\d{2})", "$3/$2/$1");
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", field, formattedDate);
                LOGGER.info(fieldName + " đã điền giá trị: " + formattedDate);
                return;
            } catch (StaleElementReferenceException | TimeoutException e) {
                LOGGER.warning("Lần thử " + i + ": Lỗi khi điền " + fieldName + ": " + e.getMessage());
                if (i == retries) {
                    throw new RuntimeException("Không thể điền " + fieldName + " sau " + retries + " lần thử", e);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    LOGGER.warning("Lỗi khi chờ retry: " + ie.getMessage());
                }
            }
        }
    }

    private void fillReasonField(String reason) {
        int retries = 3;
        for (int i = 1; i <= retries; i++) {
            try {
                LOGGER.info("Lần thử " + i + ": Điền lý do sửa đổi...");
                WebElement reasonElement = waitForElementNotStale(reasonField);
                wait.until(ExpectedConditions.elementToBeClickable(reasonElement));
                ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1];", reasonElement, "<p>" + reason + "</p>");
                LOGGER.info("Lý do sửa đổi đã điền: " + reason);
                return;
            } catch (StaleElementReferenceException | TimeoutException e) {
                LOGGER.warning("Lần thử " + i + ": Lỗi khi điền lý do sửa đổi: " + e.getMessage());
                if (i == retries) {
                    throw new RuntimeException("Không thể điền lý do sửa đổi sau " + retries + " lần thử", e);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    LOGGER.warning("Lỗi khi chờ retry: " + ie.getMessage());
                }
            }
        }
    }

    public String clickSave() {
        try {
            LOGGER.info("Click nút Lưu trên form...");
            WebElement saveBtn = waitForElementNotStale(saveButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", saveBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);

            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(confirmModal));
                LOGGER.info("Modal xác nhận tạo mới hiển thị, click nút Lưu...");
                WebElement modalSave = waitForElementNotStale(modalSaveButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", modalSave);
            } catch (TimeoutException e1) {
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(reasonField));
                    LOGGER.info("Modal lý do sửa đổi hiển thị, click nút Lưu...");
                    WebElement modalSave = waitForElementNotStale(modalSaveButton);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", modalSave);
                } catch (TimeoutException e2) {
                    LOGGER.info("Không có modal nào hiển thị, tiếp tục kiểm tra thông báo.");
                }
            }
            try {
                WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
                String alertText = alert.getText();
                LOGGER.info("Thông báo sau khi lưu: " + alertText);
                return alertText;
            } catch (TimeoutException e) {
                try {
                    WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
                    String errorText = error.getText();
                    LOGGER.info("Thông báo lỗi sau khi lưu: " + errorText);
                    return errorText;
                } catch (TimeoutException ex) {
                    LOGGER.info("Không có thông báo xuất hiện sau khi lưu.");
                    return "";
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Lưu: " + e.getMessage());
            throw e;
        }
    }

    public boolean isRequiredFieldAlertDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Thông tin bắt buộc')]")));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo bắt buộc");
            return false;
        }
    }

    public boolean verifyDocumentAdded(String title) {
        try {
            LOGGER.info("Kiểm tra văn bản đã được thêm hoặc chỉnh sửa: " + title);
            By documentTitle = By.xpath("//ul[@id='ul-list']//li[contains(@class, 'entity-item')]//h6[contains(text(), '" + title + "')]");
            wait.until(ExpectedConditions.presenceOfElementLocated(documentTitle));
            LOGGER.info("Văn bản " + title + " đã được thêm hoặc chỉnh sửa thành công.");
            return true;
        } catch (Exception e) {
            LOGGER.warning("Không tìm thấy văn bản " + title + ": " + e.getMessage());
            return false;
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

    public String clickDelete() {
        try {
            LOGGER.info("Click nút Xóa...");
            WebElement deleteBtn = waitForElementNotStale(deleteButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", deleteBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmModal));
            WebElement confirmBtn = waitForElementNotStale(confirmButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
            wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
            String alertText = driver.findElement(alertMessage).getText();
            LOGGER.info("Thông báo: " + alertText);
            return alertText;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi xóa văn bản: " + e.getMessage());
            throw e;
        }
    }

    public void selectDocument(String title) {
        try {
            LOGGER.info("Chọn văn bản: " + title);
            By documentLocator = By.xpath("//ul[@id='ul-list']//li[contains(@class, 'entity-item')]//h6[contains(text(), '" + title + "')]");
            WebElement document = waitForElementNotStale(documentLocator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", document);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", document);
            LOGGER.info("Đã chọn văn bản: " + title);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi chọn văn bản: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_select_document.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_select_document.png");
            throw e;
        }
    }

    public boolean isDocumentDetailDisplayed() {
        try {
            WebElement detail = wait.until(ExpectedConditions.visibilityOfElementLocated(detailPage));
            LOGGER.info("Trang chi tiết văn bản hiển thị.");
            return detail.isDisplayed();
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
                    By.xpath("//ul[@id='ul-list']//h6[contains(text(), '" + keyword + "')]")));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm: " + e.getMessage());
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

    public boolean checkFilterButtonDisplayed() {
        try {
            LOGGER.info("Đang kiểm tra nút lọc trạng thái...");
            navigateToLegalDocument();
            WebElement filterBtn = waitForElementNotStale(filterIcon);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", filterBtn);
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
                    LOGGER.info("Lần thử " + i + ": Click nút lọc...");
                    WebElement filterBtn = waitForElementNotStale(filterIcon);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", filterBtn);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", filterBtn);
                    wait.until(ExpectedConditions.attributeContains(filterIcon, "aria-expanded", "true"));
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
                    By statusLocator = By.xpath("//div[contains(@class, 'dropdown-item cursor-pointer font-size-18 typ-select') and @data-code='" + dataCode + "']");
                    WebElement statusOption = waitForElementNotStale(statusLocator);
                    Rectangle rect = statusOption.getRect();
                    if (!statusOption.isDisplayed() || rect.getHeight() == 0 || rect.getWidth() == 0) {
                        LOGGER.warning("Trạng thái " + status + " không hiển thị hoặc bị che");
                        continue;
                    }
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", statusOption);
                    wait.until((WebDriver d) -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ul[@id='ul-list']")));
                    By statusColumn = By.xpath("//ul[@id='ul-list']//li[contains(@class, 'entity-item')]//span[contains(@class, 'badge')]");
                    List<WebElement> statusCells = driver.findElements(statusColumn);
                    LOGGER.info("Số lượng trạng thái tìm thấy: " + statusCells.size());
                    for (WebElement cell : statusCells) {
                        LOGGER.info("Trạng thái văn bản: " + cell.getText().trim());
                    }
                    boolean allMatch;
                    if (status.equals("Tất cả")) {
                        allMatch = !statusCells.isEmpty();
                    } else {
                        allMatch = statusCells.isEmpty() ? false : statusCells.stream().allMatch(cell -> cell.getText().trim().equals(status));
                    }
                    if (!allMatch) {
                        LOGGER.warning("Danh sách văn bản không khớp trạng thái: " + status);
                    }
                    success = allMatch;
                    break;
                } catch (StaleElementReferenceException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + i + ": Lỗi khi lọc trạng thái " + status + ": " + e.getMessage());
                    if (i == retries) {
                        throw e;
                    }
                    Thread.sleep(100);
                }
            }
            if (!success) {
                capturePageSource("C:\\test\\resources\\error_filter_status.html");
            }
            LOGGER.info("Kết quả lọc trạng thái " + status + ": " + (success ? "Thành công" : "Thất bại"));
            return success;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi lọc trạng thái: " + e.getMessage());
            return false;
        }
    }

//    public boolean verifyPagination() {
//        try {
//            List<WebElement> pages = driver.findElements(paginationLinks);
//            if (pages.size() < 2) {
//                LOGGER.info("Không đủ trang để kiểm tra phân trang.");
//                return false;
//            }
//            WebElement page2 = pages.get(1);
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", page2);
//            page2.click();
//            wait.until(ExpectedConditions.urlContains("page=2"));
//            return true;
//        } catch (Exception e) {
//            LOGGER.severe("Lỗi khi kiểm tra phân trang: " + e.getMessage());
//            captureScreenshot("C:\\test\\resources\\screenshots\\error_pagination.png");
//            return false;
//        }
//    }
    private boolean clickAndVerifyPage(By locator, String pageNum, List<String> groupNamesPage1) {
        try {
            WebElement element = waitForElementNotStale(locator);
            if (element.getRect().getHeight() == 0 || element.getRect().getWidth() == 0) {
                LOGGER.severe("Nút trang " + pageNum + " không hiển thị.");
                return false;
            }
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@class='paginationjs-pages']//li[@data-num='" + pageNum + "' and contains(@class, 'active')]")));
            List<String> groupNamesPage2 = driver.findElements(groupList).stream()
                    .map(item -> item.findElement(By.xpath(".//h6[@class='mb-0']")).getText())
                    .collect(Collectors.toList());
            if (groupNamesPage2.isEmpty()) {
                LOGGER.info("Trang " + pageNum + " không có văn bản.");
                return !groupNamesPage1.isEmpty(); // Pass nếu trang 1 có dữ liệu nhưng trang 2 trống
            }
            if (groupNamesPage2.equals(groupNamesPage1)) {
                LOGGER.warning("Danh sách văn bản trên trang " + pageNum + " giống trang 1.");
                return false;
            }
            LOGGER.info("Chuyển sang trang " + pageNum + " thành công.");
            return true;
        } catch (Exception e) {
            LOGGER.warning("Lỗi khi chuyển trang " + pageNum + ": " + e.getMessage());
            return false;
        }
    }

    public boolean verifyPagination() {
        try {
            List<WebElement> pages = driver.findElements(paginationLinks);
            if (pages.size() < 2) {
                LOGGER.info("Không đủ trang để kiểm tra phân trang (chỉ có " + pages.size() + " trang).");
                return false;
            }
            List<String> groupNamesPage1 = driver.findElements(groupList).stream()
                    .map(item -> item.findElement(By.xpath(".//h6[@class='mb-0']")).getText())
                    .collect(Collectors.toList());
            if (groupNamesPage1.isEmpty()) {
                LOGGER.info("Trang 1 không có văn bản, không thể kiểm tra phân trang.");
                return false;
            }
            // Lặp qua tất cả các trang
            for (int i = 0; i < pages.size(); i++) {
                String pageNum = String.valueOf(i + 1);
                By pageLocator = By.xpath("//div[@class='paginationjs-pages']//li[@data-num='" + pageNum + "']");
                if (!clickAndVerifyPage(pageLocator, pageNum, groupNamesPage1)) {
                    LOGGER.severe("Không thể chuyển sang trang " + pageNum + ".");
                    return false;
                }
                LOGGER.info("Đã kiểm tra trang " + pageNum + " thành công.");
            }
            // Kiểm tra nút Previous từ trang cuối
            WebElement lastPage = driver.findElements(paginationLinks).get(pages.size() - 1);
            String lastPageNum = lastPage.getAttribute("data-num");
            By lastPageLocator = By.xpath("//div[@class='paginationjs-pages']//li[@data-num='" + lastPageNum + "']");
            clickAndVerifyPage(lastPageLocator, lastPageNum, groupNamesPage1); // Đảm bảo ở trang cuối
            WebElement prevBtn = waitForElementNotStale(prevButton);
            if (!prevBtn.getAttribute("class").contains("disabled")) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prevBtn);
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@class='paginationjs-pages']//li[@data-num='" + (Integer.parseInt(lastPageNum) - 1) + "' and contains(@class, 'active')]")));
                LOGGER.info("Chuyển về trang " + (Integer.parseInt(lastPageNum) - 1) + " bằng nút Previous thành công.");
            } else {
                LOGGER.info("Nút Previous bị vô hiệu hóa ở trang cuối, không kiểm tra.");
            }
            // Kiểm tra nút Next từ trang 1
            clickAndVerifyPage(paginationLinks, "1", groupNamesPage1); // Quay về trang 1
            WebElement nextBtn = waitForElementNotStale(nextButton);
            if (!nextBtn.getAttribute("class").contains("disabled")) {
                if (!clickAndVerifyPage(nextButton, "2", groupNamesPage1)) {
                    LOGGER.severe("Không thể chuyển sang trang 2 bằng nút Next.");
                    return false;
                }
                // Tiếp tục kiểm tra Next qua các trang
                for (int i = 2; i < pages.size(); i++) {
                    String nextPageNum = String.valueOf(i + 1);
                    List<String> prevPageNames = driver.findElements(groupList).stream()
                            .map(item -> item.findElement(By.xpath(".//h6[@class='mb-0']")).getText())
                            .collect(Collectors.toList());
                    if (!clickAndVerifyPage(nextButton, nextPageNum, prevPageNames)) {
                        LOGGER.severe("Không thể chuyển sang trang " + nextPageNum + " bằng nút Next.");
                        return false;
                    }
                    LOGGER.info("Chuyển sang trang " + nextPageNum + " bằng nút Next thành công.");
                }
            } else {
                LOGGER.info("Nút Next bị vô hiệu hóa, không kiểm tra.");
            }
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra phân trang: " + e.getMessage());
            return false;
        }
    }
}
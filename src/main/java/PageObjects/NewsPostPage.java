package PageObjects;

import org.openqa.selenium.*;
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
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.TakesScreenshot;

public class NewsPostPage extends PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(NewsPostPage.class.getName());

    // Locators dựa trên HTML form thêm bài viết và danh sách bài viết
    private final By newsPostSubMenu = By.xpath("//a[@data-route='VI_MAIN/nso_post_news']");
    private final By addNewButton = By.id("btn_new_entity");
    private final By titleField = By.xpath("//input[@data-name='title']");
    private final By typeSelect = By.xpath("//select[@data-name='typ02']");
    private final By languageSelect = By.xpath("//select[@data-name='typ03']");
    private final By statusSelect = By.xpath("//select[@data-name='stat01']");
    private final By audienceSelect = By.xpath("//select[@data-name='stat02']");
    private final By authorField = By.xpath("//input[@data-name='inf01']");
    private final By publishDateField = By.xpath("//input[@data-name='dt03']");
    private final By summaryField = By.xpath("//textarea[@data-name='cont01']");
    private final By hashtagField = By.xpath("//textarea[@data-name='inf02']");
    private final By contentField = By.cssSelector(".note-editable[contenteditable='true']");
    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
    private final By successMessage = By.xpath("//div[contains(@class, 'alert') and contains(text(), 'Cập nhật thành công!')]");
    private final By requiredFieldError = By.xpath("//div[contains(text(), 'Thông tin bắt buộc!')]");
    private final By fileUploadInput = By.xpath("//input[@type='file']");
    private final By leavePageWarning = By.xpath("//div[contains(text(), 'Thoát sẽ mất dữ liệu, bạn chắc không?')]");
    private final By editButton = By.xpath("//td[contains(text(), '%s')]/following-sibling::td//a[contains(@class, 'edit')]");
    private final By deleteButton = By.xpath("//td[contains(text(), '%s')]/following-sibling::td//a[contains(@class, 'delete')]");
    private final By confirmDeleteButton = By.xpath("//button[contains(text(), 'Xác nhận xóa')]");
    private final By searchInput = By.xpath("//input[@data-name='search_keyword']");
    private final By searchButton = By.xpath("//button[contains(text(), 'Tìm kiếm')]");
    private final By postRow = By.xpath("//td[contains(text(), '%s')]");
    private final By filterButton = By.id("filter_icon");
    private final By statusOptions = By.xpath("//ul[@class='dropdown-menu dropdown-menu-right']//div[@class='dropdown-item cursor-pointer font-size-18 user-typ-select']");
    private final By pageLink = By.xpath("//a[contains(@class, 'page-link') and text()='%s']");
    private final By noDataMessage = By.xpath("//div[contains(text(), 'Chưa có dữ liệu')]");
    private final By removeFileButton = By.xpath("//button[contains(text(), 'Remove file')]");
    private final By confirmRemoveButton = By.xpath("//button[contains(text(), 'Đồng ý')]");
    private final By fileRemovedSuccess = By.xpath("//div[contains(text(), 'Các tệp tin đã được xóa thành công')]");
    private final By largeFileError = By.xpath("//div[contains(text(), 'Dung lượng file quá lớn. Vui lòng chọn file khác!')]");

    public NewsPostPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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

    // Kiểm tra hiển thị nút lọc trạng thái
    public boolean checkFilterButtonDisplayed() {
        try {
            LOGGER.info("Đang kiểm tra nút lọc trạng thái...");
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    WebElement filterBtn = wait.until(ExpectedConditions.elementToBeClickable(filterButton));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", filterBtn);
                    filterBtn.click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(statusOptions));
                    List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statusOptions));
                    List<String> expectedStatuses = Arrays.asList("Nháp", "Chờ duyệt", "Duyệt nội dung", "Được hiển thị", "Không duyệt", "Ẩn", "Tất cả");
                    boolean allPresent = true;
                    LOGGER.info("Số lượng trạng thái tìm thấy: " + options.size());
                    for (WebElement option : options) {
                        String text = option.getText();
                        String dataTyp02 = option.getAttribute("data-typ02");
                        boolean isVisible = option.isDisplayed();
                        Rectangle rect = option.getRect();
                        LOGGER.info("Trạng thái: " + text + ", data-typ02: " + dataTyp02 + ", Hiển thị: " + isVisible + ", Vị trí: " + rect);
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
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi kiểm tra nút lọc: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể kiểm tra nút lọc trạng thái sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra nút lọc trạng thái: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\filter_button_exception.png");
            capturePageSource("C:\\test\\resources\\error_filter_button.html");
            throw new RuntimeException("Lỗi kiểm tra nút lọc trạng thái", e);
        }
    }

    // Lọc theo trạng thái
    public boolean filterByStatus(String status) {
        try {
            LOGGER.info("Đang lọc bài viết theo trạng thái: " + status);
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    WebElement filterBtn = wait.until(ExpectedConditions.elementToBeClickable(filterButton));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", filterBtn);
                    filterBtn.click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(statusOptions));
                    String dataTyp02;
                    switch (status) {
                        case "Nháp": dataTyp02 = "0"; break;
                        case "Chờ duyệt": dataTyp02 = "5"; break;
                        case "Duyệt nội dung": dataTyp02 = "6"; break;
                        case "Được hiển thị": dataTyp02 = "1"; break;
                        case "Không duyệt": dataTyp02 = "-1"; break;
                        case "Ẩn": dataTyp02 = "10"; break;
                        case "Tất cả": dataTyp02 = "null"; break;
                        default: throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
                    }
                    By statusLocator = By.xpath("//div[@class='dropdown-item cursor-pointer font-size-18 user-typ-select' and @data-typ02='" + dataTyp02 + "']");
                    WebElement statusOption = wait.until(ExpectedConditions.elementToBeClickable(statusLocator));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", statusOption);
                    By statusColumn = By.xpath("//td[contains(@class, 'status')]");
                    List<WebElement> statusCells = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statusColumn));
                    boolean allMatch = statusCells.stream().allMatch(cell -> cell.getText().equals(status) || status.equals("Tất cả"));
                    if (!allMatch) {
                        LOGGER.warning("Danh sách bài viết không khớp trạng thái: " + status);
                        captureScreenshot("C:\\test\\resources\\screenshots\\filter_status_" + status + "_mismatch.png");
                    }
                    return allMatch;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi lọc trạng thái " + status + ": " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể lọc trạng thái " + status + " sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi lọc trạng thái: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\filter_status_" + status + "_exception.png");
            capturePageSource("C:\\test\\resources\\error_filter_status.html");
            throw new RuntimeException("Lỗi lọc trạng thái", e);
        }
    }

    // Điều hướng đến trang Thêm bài viết
    public void navigateToAddNewsPost() {
        try {
            LOGGER.info("Đang điều hướng đến trang Bài viết/Tin tức...");
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    LOGGER.info("HTML của menu con Bài viết/Tin tức: " + subMenu.getAttribute("outerHTML"));
                    String dataRight = subMenu.getAttribute("data-right");
                    LOGGER.info("Quyền của menu con Bài viết/Tin tức: " + (dataRight != null ? dataRight : "Không có data-right"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    LOGGER.info("Đã vào trang Bài viết/Tin tức");
                    WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addNewButton));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
                    addButton.click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
                    LOGGER.info("Đã điều hướng đến trang Thêm bài viết thành công");
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi điều hướng đến Bài viết/Tin tức: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể điều hướng đến trang Thêm bài viết sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến trang Thêm bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_add_news_post.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_add_news_post.png");
            throw new RuntimeException("Lỗi điều hướng đến trang Thêm bài viết", e);
        }
    }

    // Nhập thông tin bài viết
    public void enterPostDetails(Map<String, String> postData) {
        try {
            LOGGER.info("Đang nhập thông tin bài viết...");
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                try {
                    if (postData.containsKey("title")) {
                        WebElement titleInput = wait.until(ExpectedConditions.elementToBeClickable(titleField));
                        titleInput.clear();
                        titleInput.sendKeys(postData.get("title"));
                        LOGGER.info("Đã nhập tiêu đề: " + postData.get("title"));
                    }
                    if (postData.containsKey("type")) {
                        WebElement typeElement = wait.until(ExpectedConditions.elementToBeClickable(typeSelect));
                        Select typeDropdown = new Select(typeElement);
                        typeDropdown.selectByValue(postData.get("type"));
                        LOGGER.info("Đã chọn kiểu: " + postData.get("type"));
                    }
                    if (postData.containsKey("language")) {
                        WebElement langElement = wait.until(ExpectedConditions.elementToBeClickable(languageSelect));
                        Select langDropdown = new Select(langElement);
                        langDropdown.selectByValue(postData.get("language"));
                        LOGGER.info("Đã chọn ngôn ngữ: " + postData.get("language"));
                    }
                    if (postData.containsKey("status")) {
                        WebElement statusElement = wait.until(ExpectedConditions.elementToBeClickable(statusSelect));
                        if (!statusElement.getAttribute("disabled").equals("true")) {
                            Select statusDropdown = new Select(statusElement);
                            statusDropdown.selectByValue(postData.get("status"));
                            LOGGER.info("Đã chọn trạng thái: " + postData.get("status"));
                        } else {
                            LOGGER.info("Trường trạng thái bị vô hiệu hóa, bỏ qua.");
                        }
                    }
                    if (postData.containsKey("audience")) {
                        WebElement audienceElement = wait.until(ExpectedConditions.elementToBeClickable(audienceSelect));
                        Select audienceDropdown = new Select(audienceElement);
                        audienceDropdown.selectByValue(postData.get("audience"));
                        LOGGER.info("Đã chọn đối tượng: " + postData.get("audience"));
                    }
                    if (postData.containsKey("author")) {
                        WebElement authorInput = wait.until(ExpectedConditions.elementToBeClickable(authorField));
                        authorInput.clear();
                        authorInput.sendKeys(postData.get("author"));
                        LOGGER.info("Đã nhập tác giả: " + postData.get("author"));
                    }
                    if (postData.containsKey("publishDate")) {
                        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(publishDateField));
                        dateInput.clear();
                        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", dateInput, postData.get("publishDate"));
                        LOGGER.info("Đã nhập ngày đăng: " + postData.get("publishDate"));
                    }
                    if (postData.containsKey("summary")) {
                        WebElement summaryInput = wait.until(ExpectedConditions.elementToBeClickable(summaryField));
                        summaryInput.clear();
                        summaryInput.sendKeys(postData.get("summary"));
                        LOGGER.info("Đã nhập tóm tắt: " + postData.get("summary"));
                    }
                    if (postData.containsKey("hashtag")) {
                        WebElement hashtagInput = wait.until(ExpectedConditions.elementToBeClickable(hashtagField));
                        hashtagInput.clear();
                        hashtagInput.sendKeys(postData.get("hashtag"));
                        LOGGER.info("Đã nhập hashtag: " + postData.get("hashtag"));
                    }
                    if (postData.containsKey("content")) {
                        WebElement contentArea = wait.until(ExpectedConditions.elementToBeClickable(contentField));
                        contentArea.click();
                        ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1];", contentArea, postData.get("content"));
                        LOGGER.info("Đã nhập nội dung: " + postData.get("content"));
                    }
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi nhập thông tin: " + e.getMessage());
                    if (i == retries - 1) {
                        throw new RuntimeException("Không thể nhập thông tin bài viết sau " + retries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhập thông tin bài viết: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_enter_post_details_exception.png");
            capturePageSource("C:\\test\\resources\\error_enter_post_details.html");
            throw new RuntimeException("Lỗi nhập thông tin bài viết", e);
        }
    }

    // Nhấn nút Lưu
    public String clickSave() {
        try {
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveBtn);
            saveBtn.click();
            try {
                WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
                String alertText = alert.getText();
                LOGGER.info("Thông báo sau khi lưu: " + alertText);
                return alertText;
            } catch (TimeoutException e) {
                LOGGER.info("Không có thông báo thành công, kiểm tra lỗi...");
                if (isRequiredFieldErrorDisplayed()) {
                    WebElement error = driver.findElement(requiredFieldError);
                    String errorText = error.getText();
                    LOGGER.info("Thông báo lỗi: " + errorText);
                    return errorText;
                }
                if (isLargeFileErrorDisplayed()) {
                    WebElement error = driver.findElement(largeFileError);
                    String errorText = error.getText();
                    LOGGER.info("Thông báo lỗi: " + errorText);
                    return errorText;
                }
                return "";
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Lưu: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_save_button.png");
            throw new RuntimeException("Lỗi nhấn nút Lưu", e);
        }
    }

    // Kiểm tra thông báo thành công
    public boolean isSuccessMessageDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo thành công.");
            return false;
        }
    }

    // Kiểm tra thông báo lỗi trường bắt buộc
    public boolean isRequiredFieldErrorDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(requiredFieldError));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo lỗi bắt buộc.");
            return false;
        }
    }

    // Kiểm tra thông báo lỗi file dung lượng lớn
    public boolean isLargeFileErrorDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(largeFileError));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo lỗi dung lượng file.");
            return false;
        }
    }

    // Upload file
    public void uploadFile(String filePath) {
        try {
            WebElement uploadInput = wait.until(ExpectedConditions.elementToBeClickable(fileUploadInput));
            uploadInput.sendKeys(filePath);
            LOGGER.info("Đã upload file: " + filePath);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi upload file: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_upload_file.png");
            throw new RuntimeException("Lỗi upload file", e);
        }
    }

    // Upload nhiều file
    public void uploadMultipleFiles(List<String> filePaths) {
        try {
            WebElement uploadInput = wait.until(ExpectedConditions.elementToBeClickable(fileUploadInput));
            String combinedPaths = String.join("\n", filePaths);
            uploadInput.sendKeys(combinedPaths);
            LOGGER.info("Đã upload các file: " + combinedPaths);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi upload nhiều file: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_upload_multiple_files.png");
            throw new RuntimeException("Lỗi upload nhiều file", e);
        }
    }

    // Xóa file đã upload
    public void removeUploadedFile() {
        try {
            WebElement removeBtn = wait.until(ExpectedConditions.elementToBeClickable(removeFileButton));
            removeBtn.click();
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmRemoveButton));
            confirmBtn.click();
            LOGGER.info("Đã xóa file đã upload.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi xóa file: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_remove_file.png");
            throw new RuntimeException("Lỗi xóa file", e);
        }
    }

    // Kiểm tra thông báo xóa file thành công
    public boolean isFileRemovedSuccessDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(fileRemovedSuccess));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo xóa file thành công.");
            return false;
        }
    }

    // Kiểm tra popup cảnh báo rời trang
    public boolean isLeavePageWarningDisplayed() {
        try {
            WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(leavePageWarning));
            return popup.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy popup cảnh báo rời trang.");
            return false;
        }
    }

    // Điều hướng đến menu khác
    public void navigateToAnotherMenu(String menuName) {
        try {
            LOGGER.info("Đang điều hướng đến menu: " + menuName);
            By menuLocator = By.xpath("//span[text()='" + menuName + "']/parent::a");
            WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", menu);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
            LOGGER.info("Đã click menu: " + menuName);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến menu " + menuName + ": " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_navigate_menu.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_navigate_menu.png");
            throw new RuntimeException("Lỗi điều hướng đến menu " + menuName, e);
        }
    }

    // Điều hướng đến trang chỉnh sửa bài viết
    public void navigateToEditNewsPost(String postTitle) {
        try {
            LOGGER.info("Đang điều hướng đến trang chỉnh sửa bài viết: " + postTitle);
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    LOGGER.info("Đã vào trang danh sách Bài viết/Tin tức");
                    By editLocator = By.xpath(String.format(editButton.toString(), postTitle));
                    WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(editLocator));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", editBtn);
                    editBtn.click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
                    LOGGER.info("Đã vào trang chỉnh sửa bài viết: " + postTitle);
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi điều hướng đến chỉnh sửa bài viết: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể điều hướng đến trang chỉnh sửa bài viết sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến trang chỉnh sửa: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_edit_news_post.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_edit_news_post.png");
            throw new RuntimeException("Lỗi điều hướng đến trang chỉnh sửa bài viết", e);
        }
    }

    // Xóa bài viết
    public void deleteNewsPost(String postTitle) {
        try {
            LOGGER.info("Đang xóa bài viết: " + postTitle);
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    LOGGER.info("Đã vào trang danh sách Bài viết/Tin tức");
                    By deleteLocator = By.xpath(String.format(deleteButton.toString(), postTitle));
                    WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(deleteLocator));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteBtn);
                    deleteBtn.click();
                    WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
                    confirmBtn.click();
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(deleteLocator));
                    LOGGER.info("Đã xóa bài viết: " + postTitle);
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi xóa bài viết: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể xóa bài viết sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi xóa bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_delete_news_post.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_delete_news_post.png");
            throw new RuntimeException("Lỗi xóa bài viết", e);
        }
    }

    // Tìm kiếm bài viết
    public boolean searchNewsPost(String keyword) {
        try {
            LOGGER.info("Đang tìm kiếm bài viết với từ khóa: " + keyword);
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    LOGGER.info("Đã vào trang danh sách Bài viết/Tin tức");
                    WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(searchInput));
                    searchBox.clear();
                    searchBox.sendKeys(keyword);
                    WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(searchButton));
                    searchBtn.click();
                    By postLocator = By.xpath(String.format(postRow.toString(), keyword));
                    try {
                        WebElement post = wait.until(ExpectedConditions.visibilityOfElementLocated(postLocator));
                        LOGGER.info("Tìm thấy bài viết với từ khóa: " + keyword);
                        return post.isDisplayed();
                    } catch (TimeoutException e) {
                        LOGGER.info("Không tìm thấy bài viết, kiểm tra thông báo 'Chưa có dữ liệu'");
                        return isNoDataMessageDisplayed();
                    }
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi tìm kiếm bài viết: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể tìm kiếm bài viết sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_search_news_post.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_search_news_post.png");
            throw new RuntimeException("Lỗi tìm kiếm bài viết", e);
        }
    }

    // Kiểm tra thông báo "Chưa có dữ liệu"
    public boolean isNoDataMessageDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(noDataMessage));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo 'Chưa có dữ liệu'.");
            return false;
        }
    }

    // Xem chi tiết bài viết
    public boolean viewPostDetails(String postTitle) {
        try {
            LOGGER.info("Đang xem chi tiết bài viết: " + postTitle);
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    By postLocator = By.xpath(String.format(postRow.toString(), postTitle));
                    WebElement post = wait.until(ExpectedConditions.elementToBeClickable(postLocator));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", post);
                    post.click();
                    String escapedTitle = postTitle.replace("'", "\\'").replace("\"", "\\\"");
                    By detailLocator = By.xpath(String.format("//*[self::h1 or self::h2 or self::h3 or self::div][contains(normalize-space(text()), '%s')]", escapedTitle));
                    WebElement detailTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(detailLocator));
                    LOGGER.info("Đã tìm thấy tiêu đề chi tiết: " + detailTitle.getText());
                    return detailTitle.isDisplayed();
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi xem chi tiết bài viết: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể xem chi tiết bài viết sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi xem chi tiết bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_view_post_details.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_view_post_details_exception.png");
            throw new RuntimeException("Lỗi xem chi tiết bài viết", e);
        }
    }

    // Chuyển trạng thái bài viết
    public boolean changePostStatus(String postTitle, String newStatus) {
        try {
            LOGGER.info("Đang chuyển trạng thái bài viết: " + postTitle + " sang " + newStatus);
            navigateToEditNewsPost(postTitle);
            WebElement statusElement = wait.until(ExpectedConditions.elementToBeClickable(statusSelect));
            Select statusDropdown = new Select(statusElement);
            statusDropdown.selectByValue(newStatus);
            LOGGER.info("Đã chọn trạng thái: " + newStatus);
            clickSave();
            return isSuccessMessageDisplayed();
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi chuyển trạng thái: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_change_status.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_change_status.png");
            throw new RuntimeException("Lỗi chuyển trạng thái bài viết", e);
        }
    }

    public boolean navigateToPage(String pageNumber) {
        try {
            LOGGER.info("Đang chuyển đến trang: " + pageNumber);
            navigateToPostManagement();
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                    wait.until(ExpectedConditions.urlContains("nso_post_news"));
                    By pageLocator = By.xpath(String.format(pageLink.toString(), pageNumber));
                    WebElement pageBtn = wait.until(ExpectedConditions.elementToBeClickable(pageLocator));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pageBtn);
                    pageBtn.click();
                    wait.until(ExpectedConditions.urlContains("page=" + pageNumber));
                    LOGGER.info("Đã chuyển đến trang: " + pageNumber);
                    return true;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi chuyển trang: " + e.getMessage());
                    if (i == maxRetries - 1) {
                        throw new RuntimeException("Không thể chuyển trang sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(2000);
                }
            }
            return false;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi chuyển trang: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_pagination.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_pagination.png");
            throw new RuntimeException("Lỗi chuyển trang", e);
        }
    }

    @Override
    public void capturePageSource(String filePath) {
        try {
            String pageSource = driver.getPageSource();
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filePath),
                    pageSource.getBytes(),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
            LOGGER.info("Đã lưu page source vào: " + filePath);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi lưu page source: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_page_source.png");
        }
    }
}
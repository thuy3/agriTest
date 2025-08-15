package PageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.TakesScreenshot;
import java.util.stream.Collectors;

public class NewsPostPage extends PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(NewsPostPage.class.getName());

    // Locators
    private final By newsPostSubMenu = By.xpath("//a[@data-route='VI_MAIN/nso_post_news']");
    private final By addNewButton = By.id("btn_new_entity");
    private final By titleField = By.xpath("//input[@data-name='title']");
    private final By authorField = By.xpath("//input[@data-name='inf01']");
    private final By publishDateField = By.xpath("//input[@data-name='dt03']");
    private final By summaryField = By.xpath("//textarea[@data-name='cont01']");
    private final By contentField = By.cssSelector(".note-editable[contenteditable='true']");
    private final By saveButton = By.id("btn_save");
    private final By successMessage = By.xpath("//div[contains(@class, 'alert') and contains(text(), 'Cập nhật thành công')]");
    private final By requiredFieldError = By.xpath("//div[contains(text(), 'Thông tin bắt buộc!')]");
    private final By editButton = By.id("btn_edit");
    private final By deleteButton = By.id("btn_del");
    private final By confirmModal = By.xpath("//div[@class='modal-content' and .//h4[text()='Xóa bài viết']]");
    private final By confirmDeleteButton = By.id("btn_msgbox_OK");
    private final By searchInput = By.id("inp_search");
    private final By filterButton = By.id("filter_icon");
    private final By detailPage = By.xpath("//div[contains(@class, 'd-flex align-items-center justify-content-between') and .//h5[text()='Thông tin bài viết']]");
    private final By statusOptions = By.xpath("//div[@id='dropdown-rights']//div[@class='dropdown-item cursor-pointer font-size-18 user-typ-select']");
    private final By alertMessage = By.xpath("//div[contains(@class, 'notifyjs-bootstrap-success')]//span[@data-notify-text]");
    private final By pageLink = By.xpath("//a[contains(@class, 'page-link') and text()='%s']");
    private final By postStatus = By.xpath("//span[contains(@class, 'badge-danger') and text()='Ẩn']");
    private final By postRow = By.xpath("//div[contains(@class, 'media-body') and .//h6[text()='%s']]");

    public NewsPostPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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

    public boolean checkFilterButtonDisplayed() {
        try {
            LOGGER.info("Đang kiểm tra nút lọc trạng thái...");
            navigateToPostManagement();

            WebElement subMenu = wait.until(ExpectedConditions.elementToBeClickable(newsPostSubMenu));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", subMenu);
            subMenu.click();
            wait.until(ExpectedConditions.urlContains("nso_post_news"));

            WebElement filterBtn = wait.until(ExpectedConditions.elementToBeClickable(filterButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", filterBtn);
            filterBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(statusOptions));

            List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statusOptions));
            List<String> expectedStatuses = Arrays.asList("Nháp", "Chờ duyệt", "Duyệt nội dung", "Được hiển thị", "Không duyệt", "Ẩn", "Tất cả");

            boolean allPresent = options.size() == expectedStatuses.size() &&
                    options.stream().map(WebElement::getText).collect(Collectors.toList()).containsAll(expectedStatuses);
            if (!allPresent) {
                LOGGER.warning("Danh sách trạng thái không đầy đủ: " + options.stream().map(WebElement::getText).collect(Collectors.toList()));
                captureScreenshot("C:\\test\\resources\\screenshots\\filter_button_error.png");
                capturePageSource("C:\\test\\resources\\error_filter_button.html");
            } else {
                LOGGER.info("Nút lọc hiển thị đầy đủ " + options.size() + " trạng thái.");
            }

            return allPresent;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra nút lọc trạng thái: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\filter_button_exception.png");
            capturePageSource("C:\\test\\resources\\error_filter_button.html");
            throw new RuntimeException("Lỗi kiểm tra nút lọc trạng thái", e);
        }
    }

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

                    // Kiểm tra "Chưa có dữ liệu"
                    try {
                        WebElement noData = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'simplebar-content')]//li[text()='Chưa có dữ liệu']")));
                        if (noData.isDisplayed()) {
                            LOGGER.info("Không có bài viết cho trạng thái " + status + ": Chưa có dữ liệu.");
                            return true;
                        }
                    } catch (TimeoutException ex) {}

                    // Lấy danh sách badge qua phân trang
                    By statusBadgeLocator = By.xpath("//span[contains(@class, 'badge')]");
                    List<WebElement> statusBadges = new ArrayList<>();
                    By nextPageLocator = By.xpath("//li[contains(@class, 'paginationjs-next') and contains(@class, 'J-paginationjs-next') and not(contains(@class, 'disabled'))]");
                    do {
                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statusBadgeLocator));
                        statusBadges.addAll(driver.findElements(statusBadgeLocator));
                        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                        WebElement nextPage = driver.findElements(nextPageLocator).stream().findFirst().orElse(null);
                        if (nextPage != null && nextPage.isEnabled()) {
                            nextPage.click();
                            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(statusBadgeLocator));
                            try {
                                Thread.sleep(500); // Đợi thêm để đảm bảo
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                                LOGGER.warning("Thread interrupted: " + ex.getMessage());
                            }
                            LOGGER.info("Chuyển sang trang tiếp theo cho trạng thái " + status);
                        } else {
                            break;
                        }
                    } while (true);

                    boolean allMatch = !statusBadges.isEmpty();
                    if (status.equals("Tất cả")) {
                        allMatch = statusBadges.stream().anyMatch(badge -> Arrays.asList("Nháp", "Chờ duyệt", "Duyệt nội dung", "Được hiển thị", "Không duyệt", "Ẩn").contains(badge.getText()));
                    } else {
                        allMatch = statusBadges.stream().allMatch(badge -> badge.getText().equals(status));
                    }
                    if (!allMatch) {
                        LOGGER.warning("Danh sách bài viết không khớp trạng thái hoặc không tìm thấy: " + status);
                        captureScreenshot("C:\\test\\resources\\screenshots\\filter_status_" + status + "_mismatch.png");
                    }
                    return allMatch;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException ex) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi lọc trạng thái " + status + ": " + ex.getMessage());
                    if (i == maxRetries - 1) throw new RuntimeException("Không thể lọc trạng thái " + status + " sau " + maxRetries + " lần thử", ex);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex2) {
                        Thread.currentThread().interrupt();
                        LOGGER.warning("Thread interrupted: " + ex2.getMessage());
                    }
                }
            }
            return false;
        } catch (Exception ex) {
            LOGGER.severe("Lỗi khi lọc trạng thái: " + ex.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\filter_status_" + status + "_exception.png");
            capturePageSource("C:\\test\\resources\\error_filter_status.html");
            throw new RuntimeException("Lỗi lọc trạng thái", ex);
        }
    }

    public void navigateToNewsPostPage() {
        try {
            LOGGER.info("Đang điều hướng đến trang Bài viết/Tin tức...");
            navigateToPostManagement();
            int retries = 2;
            for (int i = 1; i <= retries; i++) {
                try {
                    LOGGER.info("Lần thử " + i + ": Tìm menu con Bài viết/Tin tức...");
                    WebElement subMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(newsPostSubMenu));
                    LOGGER.info("HTML của menu con Bài viết/Tin tức: " + subMenu.getAttribute("outerHTML"));
                    String dataRight = subMenu.getAttribute("data-right");
                    LOGGER.info("Quyền của menu con: " + (dataRight != null ? dataRight : "Không có data-right"));
                    Rectangle rect = subMenu.getRect();
                    if (subMenu.isDisplayed() && subMenu.isEnabled() && rect.getHeight() > 0 && rect.getWidth() > 0) {
                        LOGGER.info("Click vào Bài viết/Tin tức...");
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", subMenu);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", subMenu);
                        wait.until(ExpectedConditions.urlContains("nso_post_news"));
                        LOGGER.info("Đã vào trang Bài viết/Tin tức");
                        return;
                    } else {
                        LOGGER.warning("Menu con không hiển thị hoặc không tương tác được: displayed=" + subMenu.isDisplayed() + ", enabled=" + subMenu.isEnabled());
                        if (i == retries) throw new RuntimeException("Menu con không tương tác được sau " + retries + " lần thử");
                        Thread.sleep(500);
                    }
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + i + ": Lỗi khi tương tác với menu con: " + e.getMessage());
                    if (i == retries) throw new RuntimeException("Không thể điều hướng đến Bài viết/Tin tức sau " + retries + " lần thử: " + e.getMessage(), e);
                    Thread.sleep(500);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến trang Bài viết/Tin tức: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_news_post_page.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_news_post_page.png");
            throw new RuntimeException("Lỗi điều hướng đến trang Bài viết/Tin tức", e);
        }
    }

    public void clickAddNewNewsPost() {
        try {
            LOGGER.info("Click nút Thêm mới bài viết...");
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addNewButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", addButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
            wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
            LOGGER.info("Đã mở form Thêm bài viết thành công");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Thêm mới bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_add_news_post.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_add_news_post.png");
            throw new RuntimeException("Lỗi khi nhấn Thêm mới bài viết", e);
        }
    }

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
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhập thông tin bài viết: " + e.getMessage());
            captureScreenshot("C:\\test\\resources\\screenshots\\error_enter_post_details_exception.png");
            capturePageSource("C:\\test\\resources\\error_enter_post_details.html");
            throw new RuntimeException("Lỗi nhập thông tin bài viết", e);
        }
    }

//    public String clickSave() {
//        try {
//            LOGGER.info("Đang nhấn nút Lưu");
//            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", saveBtn);
//
//            WebElement popupSaveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn_msgbox_OK")));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", popupSaveBtn);
//
//            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
//            LOGGER.info("Phát hiện thông báo: " + alert.getText());
//            return alert.getText();
//        } catch (Exception e) {
//            LOGGER.severe("Lỗi khi nhấn nút Lưu: " + e.getMessage());
//            capturePageSource("C:\\test\\resources\\error_click_save_" + System.currentTimeMillis() + ".html");
//            throw new RuntimeException("Lỗi khi nhấn nút Lưu", e);
//        }
//    }
    public String clickSave() {
        try {
            LOGGER.info("Đang nhấn nút Lưu");
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", saveBtn);

            WebElement popupSaveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn_msgbox_OK")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", popupSaveBtn);

            try {
                WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
                LOGGER.info("Phát hiện thông báo thành công: " + successAlert.getText());
                return successAlert.getText();
            } catch (TimeoutException e) {
                WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(requiredFieldError));
                LOGGER.info("Phát hiện thông báo lỗi: " + errorAlert.getText());
                return errorAlert.getText();
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn nút Lưu: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_click_save_" + System.currentTimeMillis() + ".html");
            throw new RuntimeException("Lỗi khi nhấn nút Lưu", e);
        }
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo thành công.");
            return false;
        }
    }

    public boolean isRequiredFieldErrorDisplayed() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.errMsg")));
            return alert.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy thông báo bắt buộc.");
            return false;
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
            capturePageSource("C:\\test\\resources\\error_detail_page.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_detail_page.png");
            return false;
        }
    }

    public void editNewsPost() {
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

    public String deleteNewsPost() {
        try {
            LOGGER.info("Đang xóa bài viết");
            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", deleteBtn);

            WebElement confirmModalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmModal));
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);

            Thread.sleep(200); // Chờ cập nhật trạng thái
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi xóa bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_delete_" + System.currentTimeMillis() + ".html");
            throw new RuntimeException("Lỗi xóa bài viết", e);
        }
        return "";
    }

    public boolean isPostHidden(String title) {
        try {
            LOGGER.info("Đang kiểm tra trạng thái bài viết: " + title);
            By rowLocator = By.xpath(String.format(postRow.toString(), title));
            WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(rowLocator));
            WebElement status = wait.until(ExpectedConditions.presenceOfElementLocated(postStatus));
            return status.isDisplayed();
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra trạng thái bài viết: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_status_" + System.currentTimeMillis() + ".html");
            return false;
        }
    }

    public boolean searchNewsPost(String keyword) {
        try {
            WebElement search = waitForElementNotStale(searchInput);
            search.clear();
            search.sendKeys(keyword);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='ul-list']//h6[contains(text(), '" + keyword + "')]")));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_search_document.html");
            captureScreenshot("C:\\test\\resources\\screenshots\\error_search_document.png");
            return false;
        }
    }

    public boolean searchNewsNoResult(String keyword) {
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
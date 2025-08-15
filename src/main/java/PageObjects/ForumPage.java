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

    // Locators
    private final By forumSubMenu = By.xpath("//a[@data-route='VI_MAIN/nso_post_forum']");
    private final By addNewButton = By.id("btn_new_entity");
    private final By titleField = By.id("title");
    private final By typeSelect = By.id("typ02");
//    private final By languageSelect = By.id("typ03");
//    private final By statusSelect = By.id("stat01");
//    private final By audienceSelect = By.id("stat02");
    private final By authorField = By.id("inf01");
    private final By publishDateField = By.id("dt03");
    private final By summaryField = By.cssSelector("#div_blog_content01 .note-editable");
    private final By contentField = By.cssSelector("#div_blog_content_lv1_area .note-editable");
    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
    private final By popupSaveButton = By.id("btn_msgbox_OK");
    private final By searchInput = By.id("inp_search");
    private final By clearSearchIcon = By.id("clear_icon");
    private final By filterIcon = By.id("filter_icon");
    private final By statusFilterOptions = By.xpath("//ul[contains(@class, 'dropdown-menu')]//div[contains(@class, 'dropdown-item')]");
    private final By confirmButton = By.xpath("//button[contains(text(), 'Đồng ý')]");
    private final By detailPage = By.xpath("//div[contains(@class, 'd-flex align-items-center justify-content-between') and .//h5[text()='Thông tin bài viết']]");
    private final By alertMessage = By.xpath("//div[contains(@class, 'notifyjs-bootstrap-success')]//span[@data-notify-text]");

    public ForumPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
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
                    Thread.sleep(300);
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
            String dataRoute;
            switch (menuName) {
                case "Bài viết/Tin tức":
                    dataRoute = "VI_MAIN/nso_post_news";
                    break;
                case "Diễn đàn":
                    dataRoute = "VI_MAIN/nso_post_forum";
                    break;
                case "Nhóm diễn đàn":
                    dataRoute = "VI_MAIN/nso_group_forum";
                    break;
                case "Quản lý lĩnh vực bài viết":
                    dataRoute = "VI_MAIN/tpy_cat_news";
                    break;
                case "Văn bản pháp luật/hành chính":
                    dataRoute = "VI_MAIN/nso_post_law";
                    break;
                default:
                    throw new IllegalArgumentException("Menu không hợp lệ: " + menuName);
            }
            By menuLocator = By.xpath("//a[@data-route='" + dataRoute + "']");
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    driver.getCurrentUrl();
                    WebElement menu = wait.until(ExpectedConditions.presenceOfElementLocated(menuLocator));
                    String expectedUrlPart = menu.getAttribute("data-url");
                    if (expectedUrlPart == null || expectedUrlPart.isEmpty()) {
                        throw new RuntimeException("Không tìm thấy thuộc tính data-url cho menu " + menuName);
                    }
                    LOGGER.info("HTML của menu " + menuName + ": " + menu.getAttribute("outerHTML"));
                    Rectangle rect = menu.getRect();
                    LOGGER.info("Trạng thái menu: displayed=" + menu.isDisplayed() +
                            ", enabled=" + menu.isEnabled() +
                            ", x=" + rect.getX() + ", y=" + rect.getY() +
                            ", width=" + rect.getWidth() + ", height=" + rect.getHeight());
                    wait.until(ExpectedConditions.elementToBeClickable(menuLocator));
                    Actions actions = new Actions(driver);
                    actions.moveToElement(menu).pause(Duration.ofSeconds(1)).click().perform();
                    wait.until(ExpectedConditions.urlContains(expectedUrlPart));
                    LOGGER.info("Đã điều hướng đến menu " + menuName + " thành công.");
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi khi click menu " + menuName + ": " + e.getMessage());
                    if (i == maxRetries - 1) {
                        capturePageSource("C:\\test\\resources\\error_navigate_menu_" + menuName.replaceAll("[^a-zA-Z0-9]", "_") + ".html");
                        throw new RuntimeException("Không thể điều hướng đến menu " + menuName + " sau " + maxRetries + " lần thử", e);
                    }
                    Thread.sleep(500);
                } catch (NoSuchSessionException e) {
                    LOGGER.severe("Session bị ngắt: " + e.getMessage());
                    capturePageSource("C:\\test\\resources\\error_session_" + menuName.replaceAll("[^a-zA-Z0-9]", "_") + ".html");
                    throw new RuntimeException("Session bị ngắt khi điều hướng đến menu " + menuName, e);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điều hướng đến menu " + menuName + ": " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_navigate_menu_" + menuName.replaceAll("[^a-zA-Z0-9]", "_") + ".html");
            throw new RuntimeException("Lỗi điều hướng đến menu " + menuName, e);
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

    public void fillForumInfo(Map<String, String> forumData) {
        try {
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                try {
                    if (forumData.containsKey("title")) {
                        WebElement title = waitForElementNotStale(titleField);
                        title.clear();
                        title.sendKeys(forumData.get("title"));
                    }
                    if (forumData.containsKey("author")) {
                        WebElement author = waitForElementNotStale(authorField);
                        author.clear();
                        author.sendKeys(forumData.get("author"));
                    }
                    if (forumData.containsKey("publishDate")) {
                        WebElement date = waitForElementNotStale(publishDateField);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", date, forumData.get("publishDate"));
                    }
                    if (forumData.containsKey("summary")) {
                        WebElement summary = waitForElementNotStale(summaryField);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1];", summary, forumData.get("summary"));
                    }
                    if (forumData.containsKey("content")) {
                        WebElement content = waitForElementNotStale(contentField);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1];", content, forumData.get("content"));
                    }
                    LOGGER.info("Đã điền thông tin diễn đàn.");
                    return;
                } catch (StaleElementReferenceException | ElementClickInterceptedException | TimeoutException e) {
                    if (i == retries - 1) {
                        capturePageSource("C:\\test\\resources\\error_enter_post_details_" + System.currentTimeMillis() + ".html");
                        throw new RuntimeException("Không thể điền thông tin sau " + retries + " lần thử", e);
                    }
                    Thread.sleep(200);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi điền thông tin: " + e.getMessage());
            throw new RuntimeException("Lỗi khi điền thông tin diễn đàn", e);
        }
    }

//    public String clickSave() {
//        try {
//            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", saveBtn);
//
//            WebElement popupSaveBtn = wait.until(ExpectedConditions.elementToBeClickable(popupSaveButton));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", popupSaveBtn);
//
//            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
//            return alert.getText();
//        } catch (Exception e) {
//            LOGGER.severe("Lỗi khi nhấn nút Lưu: " + e.getMessage());
//            capturePageSource("C:\\test\\resources\\error_click_save_" + System.currentTimeMillis() + ".html");
//            throw new RuntimeException("Lỗi khi nhấn nút Lưu", e);
//        }
//    }

    public void clickSave() {
        try {
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", saveBtn);
            WebElement popupSaveBtn = wait.until(ExpectedConditions.elementToBeClickable(popupSaveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", popupSaveBtn);
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn nút Lưu: " + e.getMessage());
            capturePageSource("C:\\test\\resources\\error_click_save_" + System.currentTimeMillis() + ".html");
            throw new RuntimeException("Lỗi khi nhấn nút Lưu", e);
        }
    }

    public String getAlertMessage() {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
            return alert.getText();
        } catch (TimeoutException e) {
            return "";
        }
    }


//    public boolean isRequiredFieldAlertDisplayed() {
//        try {
//            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.cssSelector("div.errMsg")));
//            return alert.isDisplayed();
//        } catch (TimeoutException e) {
//            LOGGER.info("Không tìm thấy thông báo bắt buộc.");
//            return false;
//        }
//    }

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

    public boolean isDocumentDetailDisplayed() {
        try {
            WebElement detail = wait.until(ExpectedConditions.visibilityOfElementLocated(detailPage));
            LOGGER.info("Trang chi tiết văn bản hiển thị.");
            return detail.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy trang chi tiết văn bản.");
//            capturePageSource("C:\\test\\resources\\error_detail_page.html");
//            captureScreenshot("C:\\test\\resources\\screenshots\\error_detail_page.png");
            return false;
        }
    }
}

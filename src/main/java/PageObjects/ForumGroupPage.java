package PageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.logging.Logger;

public class ForumGroupPage extends PostManagementPage {
    private static final Logger LOGGER = Logger.getLogger(ForumGroupPage.class.getName());

    // Locators
    private final By forumGroupSubMenu = By.xpath("//a[@data-route='VI_MAIN/nso_group_forum']");
    private final By addNewButton = By.id("btn_new_entity");
    private final By codeField = By.xpath("//input[@data-name='code01']");
    private final By groupNameField = By.xpath("//input[@data-name='name']");
    private final By typeSelect = By.xpath("//select[@data-name='typ02']");
    private final By summaryField = By.xpath("//textarea[@data-name='introduce']");
    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
    private final By confirmButton = By.id("btn_msgbox_OK");
    private final By confirmPopup = By.xpath("//div[@class='modal-content']//h4[text()='Xác nhận']");
    private final By deleteConfirmMessage = By.xpath("//div[@class='modal-content']//div[contains(text(), 'Bạn có chắc chắn muốn xóa nhóm này không?')]");
    private final By searchInput = By.id("inp_search");
    private final By groupDetailButton = By.xpath(".//span[contains(@class, 'badge-info') and text()='Xem chi tiết']");
    private final By clearSearchIcon = By.id("clear_icon");
    private final By groupList = By.xpath("//ul[@id='ul-list']//li[@class='entity-item']");
    private final By dropdownToggle = By.xpath("//a[contains(@class, 'dropdown-toggle card-drop action-item-duplicate')]");
    private final By editButton = By.id("btn_edit");
    private final By deleteButton = By.id("btn_del");
    private final By groupDetailPage = By.id("div_group_content");
    private final By paginationLinks = By.xpath("//div[@class='paginationjs-pages']//li[contains(@class, 'paginationjs-page')]/a");
    private final By nextButton = By.xpath("//div[@class='paginationjs-pages']//li[contains(@class, 'paginationjs-next')]/a");
    private final By prevButton = By.xpath("//div[@class='paginationjs-pages']//li[contains(@class, 'paginationjs-prev')]/a");
    private final By alertMessage = By.xpath("//div[contains(@class, 'notifyjs-bootstrap-success')]//span[@data-notify-text]");


    public ForumGroupPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

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
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    LOGGER.warning("Lỗi khi chờ retry: " + ie.getMessage());
                }
            }
        }
        return element;
    }

//    public void navigateToForumGroup() {
//        try {
//            LOGGER.info("Điều hướng đến Nhóm diễn đàn...");
//            navigateToPostManagement();
//            WebElement subMenu = waitForElementNotStale(forumGroupSubMenu);
//            if (subMenu.isDisplayed() && subMenu.isEnabled()) {
//                LOGGER.info("Click vào Nhóm diễn đàn...");
//                Actions actions = new Actions(driver);
//                actions.moveToElement(subMenu).pause(Duration.ofSeconds(2)).click().perform();
//                wait.until(ExpectedConditions.visibilityOfElementLocated(addNewButton));
//                LOGGER.info("Đã điều hướng đến Nhóm diễn đàn thành công.");
//            } else {
//                throw new RuntimeException("Không thể tương tác với menu Nhóm diễn đàn.");
//            }
//        } catch (Exception e) {
//            LOGGER.severe("Lỗi khi điều hướng đến Nhóm diễn đàn: " + e.getMessage());
//            capturePageSource("C:\\test\\resources\\error_forum_group.html");
//            throw e;
//        }
//    }

    public void navigateToForumGroup() {
        try {
            LOGGER.info("Điều hướng đến Nhóm diễn đàn...");
            navigateToPostManagement();
            WebElement subMenu = waitForElementNotStale(forumGroupSubMenu);
            if (subMenu.isDisplayed() && subMenu.isEnabled()) {
                LOGGER.info("Click vào Nhóm diễn đàn...");
                Actions actions = new Actions(driver);
                actions.moveToElement(subMenu).pause(Duration.ofSeconds(2)).click().perform();
                wait.until(ExpectedConditions.visibilityOfElementLocated(groupList));
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

//    public void clickAddNew() {
//        try {
//            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addNewButton));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
//            addButton.click();
//            WebDriverWait longerWait = new WebDriverWait(driver, Duration.ofSeconds(30));
//            longerWait.until(ExpectedConditions.visibilityOfElementLocated(codeField));
//            LOGGER.info("Đã nhấn nút Thêm mới.");
//        } catch (Exception e) {
//            LOGGER.severe("Lỗi khi nhấn Thêm mới: " + e.getMessage());
//            throw e;
//        }
//    }

    public void clickAddNew() {
        try {
            WebElement addBtn = waitForElementNotStale(addNewButton);
            Rectangle rect = addBtn.getRect();
            if (rect.getHeight() == 0 || rect.getWidth() == 0) {
                LOGGER.severe("Nút 'Thêm mới' không hiển thị hoặc bị che");
                throw new RuntimeException("Nút 'Thêm mới' không khả dụng");
            }
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                try {
                    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", addBtn);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addBtn);
                    LOGGER.info("Đã nhấn nút Thêm mới thành công");
                    return;
                } catch (ElementClickInterceptedException ex) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi click intercepted: " + ex.getMessage());
                    if (i == retries - 1) throw ex;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ie) {
                        LOGGER.warning("InterruptedException khi retry click: " + ie.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Thêm mới: " + e.getMessage());
            throw new RuntimeException("Lỗi khi nhấn Thêm mới", e);
        }
    }


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
//            if (groupData.containsKey("type")) {
//                WebElement type = waitForElementNotStale(typeSelect);
//                Select typeDropdown = new Select(type);
//                typeDropdown.selectByValue(groupData.get("type"));
//            }
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

    public String clickSave() {
        try {
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmPopup));
            WebElement confirmOkBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmOkBtn);
            try {
                WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage));
                String alertText = alert.getText();
                LOGGER.info("Thông báo sau khi lưu: " + alertText);
                return alertText;
            } catch (TimeoutException e) {
                LOGGER.info("Không có thông báo thành công xuất hiện sau khi lưu");
                return "";
            }
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Lưu" + e.getMessage());
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

    public void selectGroup(String groupName) throws Exception {
        try {
            // Tìm kiếm nhóm trước
            if (!searchGroup(groupName)) {
                throw new RuntimeException("Không tìm thấy nhóm: " + groupName);
            }
            // Chọn nhóm từ kết quả tìm kiếm
            WebElement group = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='ul-list']//h5[contains(text(), '" + groupName + "')]")));
            WebElement detailButton = group.findElement(By.xpath("./ancestor::li//span[contains(@class, 'badge-info') and text()='Xem chi tiết']"));
            Rectangle rect = detailButton.getRect();
            if (rect.getHeight() == 0 || rect.getWidth() == 0) {
                LOGGER.severe("Nút 'Xem chi tiết' không hiển thị hoặc bị che (Bug_08)");
                throw new RuntimeException("Nút 'Xem chi tiết' không khả dụng");
            }
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", detailButton);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", detailButton);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(groupDetailPage));
                    LOGGER.info("Đã chọn nhóm diễn đàn: " + groupName);
                    return;
                } catch (ElementClickInterceptedException ex) {
                    LOGGER.warning("Lần thử " + (i + 1) + ": Lỗi click intercepted: " + ex.getMessage());
                    if (i == retries - 1) throw ex;
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ie) {
                        LOGGER.warning("InterruptedException khi retry click: " + ie.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.severe("Lỗi khi chọn nhóm diễn đàn  " + ex.getMessage());
            throw new RuntimeException("Lỗi khi chọn nhóm: " + groupName, ex);
        }
    }


    public void clickEdit() {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownToggle));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
            WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(editButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);
            wait.until(ExpectedConditions.visibilityOfElementLocated(summaryField));
            LOGGER.info("Đã nhấn nút Chỉnh sửa.");
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Chỉnh sửa" + e.getMessage());
            throw e;
        }
    }


    public boolean clickDelete(String groupName) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownToggle));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(deleteButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
            wait.until(ExpectedConditions.visibilityOfElementLocated(deleteConfirmMessage));
            WebElement confirmOkBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmOkBtn);
            // Kiểm tra nhóm đã bị xóa
            boolean isDeleted = searchForumGroupNoResult(groupName);
            if (!isDeleted) {
                LOGGER.warning("Nhóm " + groupName + " vẫn tồn tại sau khi xóa.");
                return false;
            }
            LOGGER.info("Xóa nhóm " + groupName + " thành công.");
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi nhấn Xóa: " + e.getMessage());
            return false;
        }
    }

    public boolean isGroupDetailDisplayed() {
        try {
            WebElement detailPage = wait.until(ExpectedConditions.visibilityOfElementLocated(groupDetailPage));
            return detailPage.isDisplayed();
        } catch (TimeoutException e) {
            LOGGER.info("Không tìm thấy trang chi tiết nhóm diễn đàn (Bug_08).");
            return false;
        }
    }

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
                    .map(item -> item.findElement(By.xpath(".//h5")).getText())
                    .collect(Collectors.toList());
            if (groupNamesPage2.equals(groupNamesPage1)) {
                LOGGER.warning("Danh sách nhóm trên trang " + pageNum + " giống trang 1.");
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
                    .map(item -> item.findElement(By.xpath(".//h5")).getText())
                    .collect(Collectors.toList());

            if (!clickAndVerifyPage(paginationLinks, "2", groupNamesPage1)) {
                LOGGER.severe("Không thể chuyển sang trang 2.");
                return false;
            }

            WebElement prevBtn = waitForElementNotStale(prevButton);
            if (!prevBtn.findElement(By.xpath("..")).getAttribute("class").contains("disabled")) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prevBtn);
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[@class='paginationjs-pages']//li[@data-num='1' and contains(@class, 'active')]")));
            }
            if (!clickAndVerifyPage(nextButton, "2", groupNamesPage1)) {
                LOGGER.severe("Không thể chuyển sang trang 2 bằng nút 'Tiếp'.");
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi kiểm tra phân trang: " + e.getMessage());
            return false;
        }
    }

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

    public boolean searchForumGroupNoResult(String keyword) {
        try {
            WebElement search = waitForElementNotStale(searchInput);
            search.clear();
            search.sendKeys(keyword);
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//li[contains(text(), 'Chưa có dữ liệu')]")));
            return true;
        } catch (Exception e) {
            LOGGER.severe("Lỗi khi tìm kiếm không có kết quả: " + e.getMessage());
            return false;
        }
    }
}
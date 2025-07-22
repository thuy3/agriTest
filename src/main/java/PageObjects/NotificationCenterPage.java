package PageObjects;

import Common.Constant.Constant;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

public class NotificationCenterPage extends GeneralPage {
    private static final Logger LOGGER = Logger.getLogger(NotificationCenterPage.class.getName());

    // Locators
    private final By menuNotificationCenter = By.xpath("//span[contains(text(), 'Trung tâm thông báo')]");
    private final By pageTitle = By.xpath("//h3[contains(text(), 'Trung tâm thông báo')]");
    private final By btnNewNotification = By.id("btn_new_entity");
    private final By txtTitle = By.id("inp_group_location");
    private final By selectNotificationType = By.xpath("//select[@data-name='msg03']");
    private final By selectRelatedObject1 = By.xpath("//select[@data-name='msg02']");
    private final By selectRelatedObject2 = By.xpath("//select[@data-name='msg01']");
    private final By selectPosition = By.xpath("//select[@data-name='typ02']");
    private final By txtSummary = By.id("inp_group_age");
    private final By txtFullContent = By.cssSelector(".note-editable");
    private final By btnSave = By.id("btn_edit_entity");
    private final By btnContinue = By.id("btn_msgbox_OK");
    private final By lblSuccessMessage = By.xpath("//div[contains(text(), 'Gửi thông báo thành công!')]");
    private final By divNotificationDetail = By.id("div_group_content");
    //private final By recipientList = By.xpath("//h5[contains(text(), 'Người nhận')]/following-sibling::div//div[@style[contains(., 'border-radius: 50%')]]");
    private final By recipientList = By.xpath("//h5[contains(text(), 'Người nhận')]" +
            "/following-sibling::div//div[@style[contains(., 'border-radius: 50%')]] | //h5[contains(text(), 'Người nhận')]/following-sibling::div//div[contains(text(), '+')]"); // Cập nhật để đếm cả "+X Người nhận"
    //private final By notificationItem = By.xpath("//h6[contains(text(), 'Test Notification')]");
    private final By notificationItem = By.xpath("//li[@class='entity-item task-item mb-1']//h6[contains(text(), 'Test')]"); // Tìm h6 chứa "Test"

    public NotificationCenterPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToNotificationCenter() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        try {
            LOGGER.info("Navigating to Trung tâm thông báo...");
            WebElement menuItem = wait.until(ExpectedConditions.presenceOfElementLocated(menuNotificationCenter));
            executeScrollToElement(menuItem);
            wait.until(ExpectedConditions.elementToBeClickable(menuNotificationCenter));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuItem);
            waitForPageLoad(wait);
            LOGGER.info("Navigated to Trung tâm thông báo successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to navigate to Trung tâm thông báo: " + e.getMessage());
            throw new RuntimeException("Failed to navigate to Trung tâm thông báo: " + e.getMessage(), e);
        }
    }

    private void waitForPageLoad(WebDriverWait wait) {
        try {
            LOGGER.info("Waiting for Trung tâm thông báo page to load...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
            wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
            LOGGER.info("Trung tâm thông báo page loaded successfully");
        } catch (Exception e) {
            LOGGER.severe("Failed to load Trung tâm thông báo page: " + e.getMessage());
            throw new RuntimeException("Failed to load Trung tâm thông báo page: " + e.getMessage(), e);
        }
    }

    public void clickNewNotificationButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        waitForPageLoad(wait);
        clickButton(wait, btnNewNotification, "Thêm mới");
    }

    public void fillNotificationForm(String title, String notificationType, String relatedObject1, String relatedObject2, String position, String summary, String fullContent) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        fillInputField(wait, txtTitle, title, "Tiêu đề");
        selectDropdownOption(wait, selectNotificationType, notificationType, "Loại thông báo");
        selectDropdownOption(wait, selectRelatedObject1, relatedObject1, "Đối tượng liên quan 1");
        selectDropdownOption(wait, selectRelatedObject2, relatedObject2, "Đối tượng liên quan 2");
        selectDropdownOption(wait, selectPosition, position, "Chức vụ");
        fillInputField(wait, txtSummary, summary, "Nội dung tóm tắt");

        // Điền nội dung đầy đủ bằng JavaScript cho Summernote
        try {
            WebElement editor = wait.until(ExpectedConditions.presenceOfElementLocated(txtFullContent));
            ((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[1];", editor, fullContent);
            LOGGER.info("Nội dung đầy đủ đã điền giá trị.");
        } catch (Exception e) {
            LOGGER.severe("Failed to fill Nội dung đầy đủ: " + e.getMessage());
            throw new RuntimeException("Failed to fill Nội dung đầy đủ: " + e.getMessage(), e);
        }
    }

    public void clickSendButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        clickButton(wait, btnSave, "Lưu");
    }

    public void clickContinueButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        clickButton(wait, btnContinue, "Tiếp tục");
    }

    public String getSuccessMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(lblSuccessMessage));
            LOGGER.info("Success message retrieved: " + successMessage.getText());
            return successMessage.getText();
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve success message: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve success message: " + e.getMessage(), e);
        }
    }

    public boolean isNotificationSaved() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            WebElement notification = wait.until(ExpectedConditions.visibilityOfElementLocated(notificationItem));
            LOGGER.info("Notification item found in the system");
            return notification.isDisplayed();
        } catch (Exception e) {
            LOGGER.severe("Failed to check if notification is saved: " + e.getMessage());
            return false;
        }
    }

//    public int getRecipientCount() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//        try {
//            List<WebElement> recipients = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(recipientList));
//            LOGGER.info("Found " + recipients.size() + " recipients.");
//            return recipients.size();
//        } catch (Exception e) {
//            LOGGER.severe("Failed to retrieve recipient count: " + e.getMessage());
//            return 0;
//        }
//    }

    public int getRecipientCount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            List<WebElement> recipients = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(recipientList));
            int count = recipients.size();
            // Kiểm tra nếu có "+X Người nhận", cộng thêm số lượng trong text
            WebElement plusRecipient = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h5[contains(text(), 'Người nhận')]/following-sibling::div//div[contains(text(), '+')]")
            ));
            if (plusRecipient != null) {
                String plusText = plusRecipient.getText().replace("+", "").replace(" Người nhận", "").trim();
                count += Integer.parseInt(plusText);
            }
            LOGGER.info("Found " + count + " recipients.");
            return count;
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve recipient count: " + e.getMessage());
            return 0;
        }
    }

    public boolean isRedirectedToDetailPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            WebElement detailPage = wait.until(ExpectedConditions.visibilityOfElementLocated(divNotificationDetail));
            LOGGER.info("Redirected to notification detail page");
            return detailPage.isDisplayed();
        } catch (Exception e) {
            LOGGER.severe("Failed to check redirection to detail page: " + e.getMessage());
            return false;
        }
    }

    private void executeScrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            LOGGER.info("Scrolled to Trung tâm thông báo");
        } catch (Exception e) {
            LOGGER.severe("Failed to scroll to Trung tâm thông báo: " + e.getMessage());
            throw e;
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
            } catch (Exception e) {
                LOGGER.warning("Lỗi khi tương tác với " + fieldName + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    throw new RuntimeException(fieldName + " không thể tương tác sau " + maxRetries + " lần thử", e);
                }
                attempt++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    LOGGER.severe("Bị gián đoạn khi retry: " + ie.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void selectDropdownOption(WebDriverWait wait, By locator, String value, String fieldName) {
        int maxRetries = 3;
        int attempt = 1;
        while (attempt <= maxRetries) {
            try {
                LOGGER.info("Lần thử " + attempt + ": Đợi dropdown " + fieldName + "...");
                WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                wait.until(ExpectedConditions.elementToBeClickable(locator));

                if (isInteractable(dropdown)) {
                    Select select = new Select(dropdown);
                    select.selectByValue(value);
                    LOGGER.info(fieldName + " đã chọn giá trị: " + value);
                    return;
                } else {
                    logElementError(locator, fieldName);
                    throw new RuntimeException(fieldName + " không thể tương tác");
                }
            } catch (Exception e) {
                LOGGER.warning("Lỗi khi tương tác với " + fieldName + ": " + e.getMessage());
                if (attempt == maxRetries) {
                    throw new RuntimeException(fieldName + " không thể tương tác sau " + maxRetries + " lần thử", e);
                }
                attempt++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    LOGGER.severe("Bị gián đoạn khi retry: " + ie.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void clickButton(WebDriverWait wait, By locator, String buttonName) {
        try {
            LOGGER.info("Waiting for " + buttonName + " button...");
            WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            wait.until(ExpectedConditions.elementToBeClickable(locator));

            if (isInteractable(button)) {
                executeScrollToElement(button);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
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

    private void waitForOverlayToDisappear(WebDriverWait wait) {
        try {
            LOGGER.info("Waiting for loading overlay to disappear...");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loading")));
            LOGGER.info("Loading overlay disappeared");
        } catch (Exception e) {
            LOGGER.warning("Loading overlay still visible after timeout.");
        }
    }

    private boolean isInteractable(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            LOGGER.warning("Lỗi khi kiểm tra tương tác: " + e.getMessage());
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
}
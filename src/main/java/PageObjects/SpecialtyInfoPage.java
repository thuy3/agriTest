package PageObjects;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.logging.Logger;

public class SpecialtyInfoPage extends GeneralPage {
    private static final Logger LOGGER = Logger.getLogger(SpecialtyInfoPage.class.getName());
    // Locator cho menu cha "Quản lý đặc sản"
    private final By menuParent = By.xpath("//span[contains(text(), 'Quản lý đặc sản')]//ancestor::a[contains(@class, 'has-arrow')]");
    // Locator cho menu con "Quản lý thông tin đặc sản"
    private final By menuSpecialtyManagement = By.xpath("//a[contains(@data-route, 'VI_MAIN/prj_mat_material') and contains(text(), 'Quản lý thông tin đặc sản')]");
    private final By btnAddNew = By.id("btn_new_entity");
    private final By nameField = By.id("inp_material_name");
    private final By codeField = By.id("inp_material_code");
    private final By weightField = By.id("inp_material_weight");
    private final By regionField = By.id("inp_material_region");
    private final By statusField = By.id("sel_land_stat");
    private final By summaryField = By.xpath("//div[@id='smn_material_summary']//div[@class='note-editable']");
    private final By descriptionField = By.xpath("//div[@id='smn_material_description']//div[@class='note-editable']");
    private final By ingredientsField = By.xpath("//div[@id='smn_material_element']//div[@class='note-editable']");
    private final By processField = By.xpath("//div[@id='smn_material_process']//div[@class='note-editable']");
    private final By otherInfoField = By.xpath("//div[@id='smn_material_other_info']//div[@class='note-editable']");
    private final By imageField = By.id("frm_dropzone_send");
    private final By attachmentField = By.id("frm_dropzone_send_file");
    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
    private final By confirmSaveButton = By.id("btn_msgbox_OK");
    private final By loadingOverlay = By.className("loading");
    // Locator cho sidebar menu, điều chỉnh dựa trên HTML
    private final By sidebarMenu = By.xpath("//ul[contains(@class, 'metismenu')]"); // Dùng ul thay vì div
    // Thêm locator cho phần tử cố định (menu đầu tiên)
    private final By firstMenuItem = By.xpath("//ul[contains(@class, 'metismenu')]//li/a[contains(@data-route, 'VI_MAIN/prj_dashboard')]");

    public SpecialtyInfoPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToSpecialtyManagement() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            LOGGER.info("Current URL after login: " + driver.getCurrentUrl());
            Thread.sleep(5000); // Đợi 5 giây để trang load

            // Chờ loading biến mất
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));

            // Chờ menu đầu tiên xuất hiện
            wait.until(ExpectedConditions.presenceOfElementLocated(firstMenuItem));
            LOGGER.info("First menu item detected");

            // Chờ sidebar menu xuất hiện
            wait.until(ExpectedConditions.presenceOfElementLocated(sidebarMenu));
            LOGGER.info("Sidebar menu detected");

            // Cuộn đến menu cha "Quản lý đặc sản"
            WebElement parentMenu = wait.until(ExpectedConditions.presenceOfElementLocated(menuParent));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", parentMenu);
            Thread.sleep(1000); // Đợi sau khi cuộn

            // Mở menu cha nếu tồn tại
            if (isDisplayed(menuParent)) {
                LOGGER.info("Parent menu 'Quản lý đặc sản' found, clicking to expand...");
                click(menuParent);
                Thread.sleep(2000); // Đợi submenu mở
                waitForOverlayToDisappear(wait);
            } else {
                LOGGER.warning("Parent menu 'Quản lý đặc sản' not found");
                throw new RuntimeException("Parent menu 'Quản lý đặc sản' not found");
            }

            // Cuộn đến menu con và click
            WebElement specialtyMenu = wait.until(ExpectedConditions.presenceOfElementLocated(menuSpecialtyManagement));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", specialtyMenu);
            Thread.sleep(1000); // Đợi sau khi cuộn

            if (isDisplayed(menuSpecialtyManagement)) {
                LOGGER.info("Specialty management menu found, clicking...");
                click(menuSpecialtyManagement);
                waitForOverlayToDisappear(wait);
            } else {
                LOGGER.severe("Specialty management menu not displayed");
                throw new RuntimeException("Specialty management menu not displayed");
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted while waiting: " + e.getMessage());
        }
    }

    public void clickAddNewButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        int maxRetries = 3;
        int attempt = 1;

        while (attempt <= maxRetries) {
            try {
                LOGGER.info("Lần thử " + attempt + ": Đợi nút 'Add new'...");
                WebElement addButton = wait.until(ExpectedConditions.presenceOfElementLocated(btnAddNew));
                wait.until(ExpectedConditions.elementToBeClickable(btnAddNew)); // Đổi sang elementToBeClickable

                //// Ẩn navbar-header nếu có
                try {
                    ((JavascriptExecutor) driver).executeScript(
                            "var navbar = document.querySelector('.navbar-header'); if (navbar) navbar.style.display = 'none';"
                    );
                    LOGGER.info("Ẩn navbar-header để tránh che khuất");
                } catch (Exception e) {
                    LOGGER.warning("Không thể ẩn navbar-header: " + e.getMessage());
                }
                // Cuộn đến nút
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addButton);
                Thread.sleep(500); // Đợi cuộn xong

                // Kiểm tra xem có element che khuất không
                if (isElementInteractable(addButton)) {
                    addButton.click();
                    LOGGER.info("Clicked 'Add new' button");
                    waitForOverlayToDisappear(wait); // Đợi overlay nếu có
                    return; // Thành công, thoát vòng lặp
                } else {
                    LOGGER.warning("Nút 'Add new' không thể tương tác, thử dùng JavaScript click...");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
                    LOGGER.info("Clicked 'Add new' button bằng JavaScript");
                    waitForOverlayToDisappear(wait);
                    return;
                }
            } catch (ElementClickInterceptedException e) {
                LOGGER.warning("Lần thử " + attempt + ": Nút 'Add new' bị che khuất: " + e.getMessage());
                if (attempt == maxRetries) {
                    LOGGER.severe("Lỗi click nút 'Add new' sau " + maxRetries + " lần thử: " + e.getMessage());
                    LOGGER.info("Page source tại thời điểm lỗi: " + driver.getPageSource());
                    throw new RuntimeException("Add new button not displayed or clickable", e);
                }
                attempt++;
                try {
                    Thread.sleep(1000); // Đợi 1s trước khi thử lại
                } catch (InterruptedException ie) {
                    LOGGER.severe("Bị gián đoạn khi retry: " + ie.getMessage());
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                LOGGER.severe("Lỗi khi click nút 'Add new': " + e.getMessage());
                throw new RuntimeException("Add new button not displayed or clickable", e);
            }
        }

    }

    private boolean isElementInteractable(WebElement element) {
        try {
            return element.isDisplayed() && element.isEnabled();
        } catch (StaleElementReferenceException e) {
            LOGGER.warning("Element bị stale khi kiểm tra tương tác: " + e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.warning("Lỗi khi kiểm tra tương tác: " + e.getMessage());
            return false;
        }
    }

    private void waitForOverlayToDisappear(WebDriverWait wait) {
        try {
            LOGGER.info("Đợi loading overlay biến mất...");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));
            LOGGER.info("Loading overlay đã biến mất");
        } catch (TimeoutException e) {
            LOGGER.warning("Loading overlay vẫn hiện sau timeout: " + driver.getPageSource());
        }
    }

    public void addNewSpecialty(String name, String code, String weight, String region, String status,
                                String summary, String description, String ingredients, String process,
                                String otherInfo, String imagePath, String attachmentPath) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Thread.sleep(2000);
            if (isDisplayed(nameField)) {
                type(nameField, name);
            } else {
                throw new RuntimeException("Name field not displayed");
            }
            if (isDisplayed(codeField)) {
                type(codeField, code);
            } else {
                throw new RuntimeException("Code field not displayed");
            }
            if (isDisplayed(weightField)) {
                type(weightField, weight);
            } else {
                throw new RuntimeException("Weight field not displayed");
            }
            if (isDisplayed(regionField)) {
                type(regionField, region);
            } else {
                throw new RuntimeException("Region field not displayed");
            }
            if (isDisplayed(statusField)) {
                new Select(find(statusField)).selectByVisibleText(status);
            } else {
                throw new RuntimeException("Status field not displayed");
            }
            if (isDisplayed(summaryField)) {
                find(summaryField).sendKeys(summary);
            } else {
                throw new RuntimeException("Summary field not displayed");
            }
            if (isDisplayed(descriptionField)) {
                find(descriptionField).sendKeys(description);
            } else {
                throw new RuntimeException("Description field not displayed");
            }
            if (isDisplayed(ingredientsField)) {
                find(ingredientsField).sendKeys(ingredients);
            } else {
                throw new RuntimeException("Ingredients field not displayed");
            }
            if (isDisplayed(processField)) {
                find(processField).sendKeys(process);
            } else {
                throw new RuntimeException("Process field not displayed");
            }
            if (isDisplayed(otherInfoField)) {
                find(otherInfoField).sendKeys(otherInfo);
            } else {
                throw new RuntimeException("Other info field not displayed");
            }
            if (isDisplayed(imageField)) {
                WebElement imageForm = find(imageField);
                WebElement imageInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("#frm_dropzone_send input[type='file']")
                ));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].style.display='block'; arguments[0].style.opacity='1'; arguments[0].style.visibility='visible';",
                        imageInput
                );
                imageInput.sendKeys(imagePath);
                LOGGER.info("Uploaded image: " + imagePath);
            } else {
                throw new RuntimeException("Image field not displayed");
            }
            if (isDisplayed(attachmentField)) {
                WebElement attachmentForm = find(attachmentField);
                WebElement attachmentInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("#frm_dropzone_send_file input[type='file']")
                ));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].style.display='block'; arguments[0].style.opacity='1'; arguments[0].style.visibility='visible';",
                        attachmentInput
                );
                attachmentInput.sendKeys(attachmentPath);
                LOGGER.info("Uploaded attachment: " + attachmentPath);
            } else {
                throw new RuntimeException("Attachment field not displayed");
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted while waiting: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void clickSaveButton() {
        try {
            Thread.sleep(1000);
            if (isDisplayed(saveButton)) {
                click(saveButton);
            } else {
                throw new RuntimeException("Save button not displayed");
            }
            Thread.sleep(1000);
            if (isDisplayed(confirmSaveButton)) {
                click(confirmSaveButton);
            } else {
                throw new RuntimeException("Confirm save button not displayed");
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted while waiting: " + e.getMessage());
        }
    }

    public boolean verifySpecialtyDetails(String name, String code, String weight, String region, String status,
                                          String summary, String description, String ingredients, String process,
                                          String otherInfo) {
        try {
            Thread.sleep(1000);
            By displayedName = By.xpath("//h5[contains(text(), '" + name + "')]");
            By displayedCode = By.xpath("//p[contains(text(), '" + code + "')]");
            By displayedWeight = By.xpath("//p[contains(text(), '" + weight + "')]");
            By displayedRegion = By.xpath("//p[contains(text(), '" + region + "')]");
            By displayedStatus = By.xpath("//p[contains(text(), '" + status + "')]");
            By displayedSummary = By.xpath("//div[contains(@class, 'info-content-description') and contains(.//p, '" + summary + "')]");
            By displayedDescription = By.xpath("//div[contains(@class, 'info-content-description') and contains(.//p, '" + description + "')]");
            By displayedIngredients = By.xpath("//div[contains(@class, 'info-content-description') and contains(.//p, '" + ingredients + "')]");
            By displayedProcess = By.xpath("//div[contains(@class, 'info-content-description') and contains(.//p, '" + process + "')]");
            By displayedOtherInfo = By.xpath("//div[contains(@class, 'info-content-description') and contains(.//p, '" + otherInfo + "')]");
            return isDisplayed(displayedName) &&
                    isDisplayed(displayedCode) &&
                    isDisplayed(displayedWeight) &&
                    isDisplayed(displayedRegion) &&
                    isDisplayed(displayedStatus) &&
                    isDisplayed(displayedSummary) &&
                    isDisplayed(displayedDescription) &&
                    isDisplayed(displayedIngredients) &&
                    isDisplayed(displayedProcess) &&
                    isDisplayed(displayedOtherInfo);
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted while waiting: " + e.getMessage());
            return false;
        }
    }
}

//package PageObjects;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.ui.*;
//import java.time.Duration;
//import java.util.List;
//import java.util.logging.Logger;
//
//public class SpecialtyInfoPage extends GeneralPage {
//    private static final Logger LOGGER = Logger.getLogger(SpecialtyInfoPage.class.getName());
//
//    // Menu locators
//    private final By menuParent = By.xpath("//span[contains(text(), 'Quản lý đặc sản')]//ancestor::a[contains(@class, 'has-arrow')]");
//    private final By menuSpecialtyManagement = By.xpath("//a[contains(@data-route, 'VI_MAIN/prj_mat_material') and contains(text(), 'Đặc sản')]");
//    private final By firstMenuItem = By.xpath("//ul[contains(@class, 'metismenu')]//li/a[contains(@data-route, 'VI_MAIN/prj_dashboard')]");
//    private final By sidebarMenu = By.xpath("//ul[contains(@class, 'metismenu')]");
//
//    // Form field locators
//    private final By btnAddNew = By.id("btn_new_entity");
//    private final By nameField = By.id("inp_material_name");
//    private final By codeField = By.id("inp_material_code");
//    private final By weightField = By.id("inp_material_weight");
//    private final By regionField = By.id("inp_material_region");
//    private final By statusField = By.id("sel_land_stat");
//
//    private final By summaryField = By.xpath("//div[@id='smn_material_summary']//div[@class='note-editable']");
//    private final By descriptionField = By.xpath("//div[@id='smn_material_description']//div[@class='note-editable']");
//    private final By ingredientsField = By.xpath("//div[@id='smn_material_element']//div[@class='note-editable']");
//    private final By processField = By.xpath("//div[@id='smn_material_process']//div[@class='note-editable']");
//    private final By otherInfoField = By.xpath("//div[@id='smn_material_other_info']//div[@class='note-editable']");
//
//    private final By imageField = By.id("frm_dropzone_send");
//    private final By attachmentField = By.id("frm_dropzone_send_file");
//
//    private final By saveButton = By.xpath("//button[contains(text(), 'Lưu')]");
//    private final By confirmSaveButton = By.id("btn_msgbox_OK");
//    private final By loadingOverlay = By.className("loading");
//
//    public SpecialtyInfoPage(WebDriver driver) {
//        super(driver);
//    }
//
//    public void navigateToSpecialtyManagement() {
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//
//        try {
//            waitUntilDisappear(loadingOverlay);
//            wait.until(ExpectedConditions.presenceOfElementLocated(firstMenuItem));
//            wait.until(ExpectedConditions.presenceOfElementLocated(sidebarMenu));
//
//            scrollTo(menuParent);
//            clickIfDisplayed(menuParent);
//
//            scrollTo(menuSpecialtyManagement);
//            clickIfDisplayed(menuSpecialtyManagement);
//        } catch (Exception e) {
//            LOGGER.severe("Navigation failed: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void clickAddNewButton() {
//        try {
//            WebElement addButton = new WebDriverWait(driver, Duration.ofSeconds(20))
//                    .until(ExpectedConditions.visibilityOfElementLocated(btnAddNew));
//            scrollTo(addButton);
//            addButton.click();
//            LOGGER.info("Clicked 'Add new' button");
//        } catch (Exception e) {
//            throw new RuntimeException("Add new button not clickable", e);
//        }
//    }
//
//    public void addNewSpecialty(String name, String code, String weight, String region, String status,
//                                String summary, String description, String ingredients, String process,
//                                String otherInfo, String imagePath, String attachmentPath) {
//        try {
//            setInput(nameField, name);
//            setInput(codeField, code);
//            setInput(weightField, weight);
//            setInput(regionField, region);
//            selectDropdown(statusField, status);
//
//            setRichText(summaryField, summary);
//            setRichText(descriptionField, description);
//            setRichText(ingredientsField, ingredients);
//            setRichText(processField, process);
//            setRichText(otherInfoField, otherInfo);
//
//            uploadFile(imageField, imagePath);
//            uploadFile(attachmentField, attachmentPath);
//        } catch (Exception e) {
//            LOGGER.severe("Add new specialty failed: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void clickSaveButton() {
//        clickIfDisplayed(saveButton);
//        clickIfDisplayed(confirmSaveButton);
//    }
//
//    public boolean verifySpecialtyDetails(
//            String expectedName,
//            String expectedCode,
//            String expectedWeight,
//            String expectedRegion,
//            String expectedStatus,
//            String expectedSummary,
//            String expectedDescription,
//            String expectedIngredients,
//            String expectedProcess,
//            String expectedOtherInfo
//    ) {
//        try {
//            // Lấy các giá trị đang hiển thị trên màn hình chi tiết
//            String actualName = driver.findElement(By.xpath("//div[@class='col-8']//div/h5")).getText().trim();
//            String actualCode = driver.findElement(By.xpath("//h6[contains(text(),'Mã đặc sản')]/following-sibling::p")).getText().trim();
//            String actualWeight = driver.findElement(By.xpath("//h6[contains(text(),'Trọng lượng')]/following-sibling::p")).getText().trim();
//            String actualRegion = driver.findElement(By.xpath("//h6[contains(text(),'Đặc sản vùng')]/following-sibling::p")).getText().trim();
//            String actualStatus = driver.findElement(By.xpath("//h6[contains(text(),'Trạng thái')]/following-sibling::p")).getText().trim();
//
//            // Các phần mô tả phía dưới
//            List<WebElement> infoDescriptions = driver.findElements(By.cssSelector(".info-content-description > p"));
//
//            String actualSummary = infoDescriptions.get(0).getText().trim();
//            String actualDescription = infoDescriptions.get(1).getText().trim();
//            String actualIngredients = infoDescriptions.get(2).getText().trim();
//            String actualProcess = infoDescriptions.get(3).getText().trim();
//            String actualOtherInfo = infoDescriptions.get(4).getText().trim();
//
//            // So sánh với các giá trị mong đợi
//            return actualName.equals(expectedName) &&
//                    actualCode.equals(expectedCode) &&
//                    actualWeight.equals(expectedWeight) &&
//                    actualRegion.equals(expectedRegion) &&
//                    actualStatus.equals(expectedStatus) &&
//                    actualSummary.equals(expectedSummary) &&
//                    actualDescription.equals(expectedDescription) &&
//                    actualIngredients.equals(expectedIngredients) &&
//                    actualProcess.equals(expectedProcess) &&
//                    actualOtherInfo.equals(expectedOtherInfo);
//        } catch (Exception e) {
//            System.out.println("Lỗi khi kiểm tra chi tiết đặc sản: " + e.getMessage());
//            return false;
//        }
//    }
//
//    // ------------------- Private Utilities ---------------------
//
//    private void scrollTo(By locator) {
//        WebElement element = driver.findElement(locator);
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
//    }
//
//    private void scrollTo(WebElement element) {
//        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
//    }
//
//    private void setInput(By locator, String value) {
//        if (isDisplayed(locator)) {
//            find(locator).clear();
//            find(locator).sendKeys(value);
//        } else {
//            throw new RuntimeException("Field not displayed: " + locator);
//        }
//    }
//
//    private void setRichText(By locator, String content) {
//        if (isDisplayed(locator)) {
//            find(locator).sendKeys(content);
//        } else {
//            throw new RuntimeException("Rich text field not displayed: " + locator);
//        }
//    }
//
//    private void uploadFile(By locator, String filePath) {
//        if (isDisplayed(locator)) {
//            WebElement dropZone = find(locator);
//            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", dropZone);
//            dropZone.findElement(By.tagName("form")).sendKeys(filePath);
//        } else {
//            throw new RuntimeException("Upload field not displayed: " + locator);
//        }
//    }
//
//    private void selectDropdown(By locator, String visibleText) {
//        if (isDisplayed(locator)) {
//            new Select(find(locator)).selectByVisibleText(visibleText);
//        } else {
//            throw new RuntimeException("Dropdown not displayed: " + locator);
//        }
//    }
//
//    private void clickIfDisplayed(By locator) {
//        if (isDisplayed(locator)) {
//            click(locator);
//        } else {
//            throw new RuntimeException("Element not clickable: " + locator);
//        }
//    }
//
//    private void waitUntilDisappear(By locator) {
//        new WebDriverWait(driver, Duration.ofSeconds(30))
//                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
//    }
//}

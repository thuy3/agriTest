package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import java.util.logging.Logger;

public class SpecialtyInfoPage extends GeneralPage {
    private static final Logger LOGGER = Logger.getLogger(SpecialtyInfoPage.class.getName());
    // Locator cho menu cha
    private final By menuParent = By.xpath("//a[contains(., 'Quản lý đặc sản')]//span[text()='Quản lý đặc sản']");
    // Locator cho menu con
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

    public SpecialtyInfoPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToSpecialtyManagement() {
        try {
            LOGGER.info("Current URL after login: " + driver.getCurrentUrl());
            Thread.sleep(3000); // Đợi 3 giây để trang load
            // Mở menu cha nếu tồn tại
            if (isDisplayed(menuParent)) {
                LOGGER.info("Parent menu found, clicking to expand...");
                click(menuParent);
                Thread.sleep(1000); // Đợi submenu mở
            } else {
                LOGGER.warning("Parent menu not found");
            }
            // Kiểm tra và click menu con
            if (isDisplayed(menuSpecialtyManagement)) {
                LOGGER.info("Specialty management menu found, clicking...");
                click(menuSpecialtyManagement);
            } else {
                LOGGER.severe("Specialty management menu not displayed");
                throw new RuntimeException("Specialty management menu not displayed");
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Interrupted while waiting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clickAddNewButton() {
        try {
            Thread.sleep(1000);
            if (isDisplayed(btnAddNew)) {
                click(btnAddNew);
            } else {
                throw new RuntimeException("Add new button not displayed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addNewSpecialty(String name, String code, String weight, String region, String status,
                                String summary, String description, String ingredients, String process,
                                String otherInfo, String imagePath, String attachmentPath) {
        try {
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
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", find(imageField));
                find(imageField).sendKeys(imagePath);
            } else {
                throw new RuntimeException("Image field not displayed");
            }
            if (isDisplayed(attachmentField)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", find(attachmentField));
                find(attachmentField).sendKeys(attachmentPath);
            } else {
                throw new RuntimeException("Attachment field not displayed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}
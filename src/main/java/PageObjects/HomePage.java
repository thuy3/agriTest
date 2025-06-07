package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends GeneralPage {
    private final By sidebarToggle = By.cssSelector("button[aria-label='Toggle Sidebar']");
    private final By specialtyMenu = By.xpath("//span[text()='Quản lý đặc sản']");
    private final By specialtyInfoSubMenu = By.xpath("//span[text()='Quản lý thông tin đặc sản']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void goToSpecialtyInfoPage() {
        click(sidebarToggle);
        click(specialtyMenu);
        click(specialtyInfoSubMenu);
    }
}


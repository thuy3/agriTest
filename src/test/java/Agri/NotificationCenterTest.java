package Agri;

import Common.Constant.Constant;
import PageObjects.LoginPage;
import PageObjects.NotificationCenterPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class NotificationCenterTest extends BaseTest {

    @Test(description = "Tạo mới một thông báo thành công với nhập đầy đủ các thông tin")
    public void TTTB_1() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();

        NotificationCenterPage notificationPage = new NotificationCenterPage(driver);
        notificationPage.navigateToNotificationCenter();

        notificationPage.clickNewNotificationButton();

        notificationPage.fillNotificationForm(
                "Test Notification",
                "200", // Dịch bệnh
                "200", // Dịch bệnh (đối tượng liên quan 1)
                "200", // Dịch bệnh (đối tượng liên quan 2)
                "40",  // Nông dân
                "Test notification",
                "This is a test notification content"
        );

        notificationPage.clickSendButton();
        notificationPage.clickContinueButton();

        // Verify Expected Results
        Assert.assertTrue(notificationPage.isRedirectedToDetailPage(),
                "Hệ thống không điều hướng tới trang chi tiết thông báo!");
        Assert.assertTrue(notificationPage.isNotificationSaved(),
                "Thông báo không được lưu trong hệ thống!");
        Assert.assertTrue(notificationPage.getRecipientCount() > 0,
                "Danh sách người nhận rỗng, thông báo không được gửi!");
    }

    @Test(description = "Kiểm tra Thêm thông báo thiếu thông tin bắt buộc")
    public void TTTB_4() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();

        NotificationCenterPage notificationPage = new NotificationCenterPage(driver);
        notificationPage.navigateToNotificationCenter();

        notificationPage.clickNewNotificationButton();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        notificationPage.fillNotificationForm(
                "Test Notification Missing Summary",
                "200", // Dịch bệnh
                "200", // Dịch bệnh (đối tượng liên quan 1)
                "200", // Dịch bệnh (đối tượng liên quan 2)
                "40",  // Nông dân
                "",    // Bỏ trống Nội dung tóm tắt
                "This is a test notification content"
        );

        // Nhấn nút "Lưu" và chờ popup
        notificationPage.clickSendButton();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn_msgbox_OK")));

        notificationPage.clickContinueButton();

        // Verify Expected Results
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn_new_entity"))).isDisplayed(),
                "Hệ thống không ở lại màn hình Thêm mới sau khi nhấn Tiếp tục!");
        By errorMessage = By.xpath("//div[@class='errMsg' and text()='Thông tin bắt buộc']");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed(),
                "Hệ thống không hiển thị cảnh báo 'Thông tin bắt buộc' khi bỏ trống Nội dung tóm tắt!");
    }

    @Test(description = "Kiểm tra chức năng tìm kiếm thông báo")
    public void TTTB_5() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();

        NotificationCenterPage notificationPage = new NotificationCenterPage(driver);
        notificationPage.navigateToNotificationCenter();

        // Nhập từ khóa vào ô tìm kiếm
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        By searchInput = By.id("inp_search");
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        searchBox.clear();
        searchBox.sendKeys("test"); // Nhập từ khóa "test"

        // Verify Expected Results
        By searchResults = By.xpath("//ul[@id='ul-list']//li//h6[contains(text(), 'test')]");
        List<WebElement> results = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(searchResults));
        Assert.assertTrue(!results.isEmpty(),
                "Danh sách không hiển thị các thông báo chứa từ khóa 'test'!");

        // Đảm bảo số lượng kết quả khớp với các thông báo có từ khóa (ít nhất 1)
        Assert.assertTrue(results.size() > 0,
                "Không tìm thấy thông báo nào chứa từ khóa 'test'!");
    }
}
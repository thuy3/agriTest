package Agri;

import Common.Constant.Constant;
import PageObjects.ForumPage;
import PageObjects.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ForumTest extends BaseTest {
    private ForumPage forumPage;
    private static final Logger LOGGER = Logger.getLogger(ForumTest.class.getName());

    @Test(description = "QLBV_DD-37: Kiểm tra tạo mới diễn đàn hợp lệ")
    public void TC37_CreateForum_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        //fill form
        Map<String, String> forumData = new HashMap<>();
        String uniqueTitle = "Diễn đàn Test " + UUID.randomUUID().toString().substring(0, 8);
        forumData.put("title", uniqueTitle);
        forumData.put("author", "Test Author");
        forumData.put("publishDate", "10/08/2025");
        forumData.put("summary", "Nội dung tóm tắt");
        forumData.put("content", "Nội dung bài viết");
        forumPage.fillForumInfo(forumData);

        forumPage.clickSave();
        String alert = forumPage.getAlertMessage();
        Assert.assertEquals(alert, "Dữ liệu được tạo mới thành công", "Thông báo không đúng");

        WebElement cardBody = forumPage.waitForElementNotStale(By.xpath("//div[@class='card-body']"));
        Assert.assertTrue(cardBody.isDisplayed(), "Trang chi tiết không hiển thị");

        WebElement detailTitle = forumPage.waitForElementNotStale(By.xpath("//div[@class='card-body']//h1[contains(@class, 'align-items-center')]"));
        Assert.assertEquals(detailTitle.getText(), uniqueTitle, "Tiêu đề trong trang chi tiết không khớp");

        forumPage.navigateToForum();
        boolean isForumListed = forumPage.searchForum(uniqueTitle);
        Assert.assertTrue(isForumListed, "Diễn đàn vừa tạo không hiển thị trong danh sách");
    }

    @Test(description = "QLBV_DD-38: Kiểm tra thêm mới diễn đàn bỏ trống trường bắt buộc")
    public void TC38_CreateForum_MissingRequiredField() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("type", "105100");
        forumPage.fillForumInfo(forumData);
        forumPage.clickSave();
        Assert.assertTrue(forumPage.isRequiredFieldAlertDisplayed(), "Thông báo lỗi bắt buộc không hiển thị");
    }

    @Test(description = "QLBV_DD-42: Kiểm tra popup cảnh báo khi rời khỏi trang tạo mới")
    public void TC42_LeavePageWarning() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        try {
            forumPage = new ForumPage(driver);
            forumPage.navigateToForum();
            forumPage.clickAddNew();
            Map<String, String> forumData = new HashMap<>();
            forumData.put("title", "Diễn đàn Tạm");
            forumPage.fillForumInfo(forumData);
            forumPage.navigateToAnotherMenu("Bài viết/Tin tức");
            Assert.assertFalse(forumPage.isExitWarningPopupDisplayed(), "Popup cảnh báo rời trang hiển thị sai");
            Assert.assertTrue(driver.getCurrentUrl().contains("nso_post_news"), "Không điều hướng đến Bài viết/Tin tức");
        } catch (Exception e) {
            LOGGER.severe("Lỗi trong TC44_LeavePageWarning: " + e.getMessage());
            throw e;
        }
    }
    @Test(description = "QLBV_DD-45: Kiểm tra tìm kiếm diễn đàn")
    public void TC45_SearchForum() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        Assert.assertTrue(forumPage.searchForum("Học mãi"), "Tìm kiếm không tìm thấy diễn đàn!");
    }

    @Test(description = "QLBV_DD-41: Kiểm tra xem chi tiết diễn đàn")
    public void TC41_ViewDocumentDetails() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.openForumDetails("Học mãi");
        Assert.assertTrue(forumPage.isDocumentDetailDisplayed(), "Không hiển thị trang chi tiết diễn đàn");
    }


    @Test(description = "QLBV_DD-46: Kiểm tra hiển thị nút lọc trạng thái")
    public void TC46_CheckFilterButtonDisplayed() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        boolean isDisplayed = forumPage.checkFilterButtonDisplayed();
        Assert.assertTrue(isDisplayed, "Nút lọc trạng thái không hiển thị đầy đủ các trạng thái");
    }
}

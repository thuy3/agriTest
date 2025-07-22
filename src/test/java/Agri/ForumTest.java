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

public class ForumTest extends BaseTest {
    private ForumPage forumPage;

    @Test(description = "QLBV_DD-38: Kiểm tra tạo mới diễn đàn hợp lệ")
    public void TC38_CreateForum_Success() {
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
        forumData.put("type", "105100");
        forumData.put("language", "1");
        forumData.put("audience", "100");
        forumPage.fillForumInfo(forumData);

        String alert = forumPage.clickSave();
        Assert.assertEquals(alert, "Dữ liệu được tạo mới thành công", "Thông báo không đúng");

        WebElement cardBody = forumPage.waitForElementNotStale(By.xpath("//div[@class='card-body']"));
        Assert.assertTrue(cardBody.isDisplayed(), "Trang chi tiết không hiển thị");

        WebElement detailTitle = forumPage.waitForElementNotStale(By.xpath("//div[@class='card-body']//h1[contains(@class, 'align-items-center')]"));
        Assert.assertEquals(detailTitle.getText(), uniqueTitle, "Tiêu đề trong trang chi tiết không khớp");

        forumPage.navigateToForum();
        boolean isForumListed = forumPage.searchForum(uniqueTitle);
        Assert.assertTrue(isForumListed, "Diễn đàn vừa tạo không hiển thị trong danh sách");
    }

    @Test(description = "QLBV_DD-39: Kiểm tra thêm mới diễn đàn bỏ trống trường bắt buộc")
    public void TC39_CreateForum_MissingRequiredField() {
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

    @Test(description = "QLBV_DD-44: Kiểm tra popup cảnh báo khi rời khỏi trang tạo mới")
    public void TC44_LeavePageWarning() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("title", "Diễn đàn Tạm");
        forumPage.fillForumInfo(forumData);
        forumPage.navigateToAnotherMenu("Bài viết/Tin tức");
        Assert.assertFalse(forumPage.isExitWarningPopupDisplayed(), "Popup cảnh báo rời trang hiển thị sai (Bug_09)");
    }

    @Test(description = "QLBV_DD-46: Kiểm tra tìm kiếm diễn đàn")
    public void TC46_SearchForum() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        Assert.assertTrue(forumPage.searchForum("Diễn đàn Test"), "Tìm kiếm không tìm thấy diễn đàn!");
    }

    @Test(description = "QLBV_DD-47: Kiểm tra hiển thị nút lọc trạng thái")
    public void TC47_CheckFilterButtonDisplayed() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        boolean isDisplayed = forumPage.checkFilterButtonDisplayed();
        forumPage.capturePageSource("C:\\test\\resources\\filter_button_forum.html");
        Assert.assertTrue(isDisplayed, "Nút lọc trạng thái không hiển thị đầy đủ các trạng thái");
    }

    @Test(description = "QLBV_DD-50: Kiểm tra upload ảnh hợp lệ")
    public void TC50_UploadValidImage() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("title", "Diễn đàn Test Image");
        forumData.put("type", "105100");
        forumData.put("language", "1");
        forumData.put("audience", "100");
        forumPage.fillForumInfo(forumData);
        String filePath = "C:\\Users\\Admin\\Downloads\\1206-sach-anh.jpg";
        String alert = forumPage.uploadFile(filePath);
        forumPage.capturePageSource("C:\\test\\resources\\upload_success_forum.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Upload ảnh hợp lệ thất bại!");
    }

    @Test(description = "QLBV_DD-51: Kiểm tra xóa ảnh đã upload")
    public void TC51_RemoveUploadedFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("title", "Diễn đàn Test Remove");
        forumData.put("type", "105100");
        forumData.put("language", "1");
        forumData.put("audience", "100");
        forumPage.fillForumInfo(forumData);
        String filePath = "C:\\Users\\Admin\\Downloads\\1206-sach-anh.jpg";
        forumPage.uploadFile(filePath);
        String alert = forumPage.removeUploadedFile();
        forumPage.capturePageSource("C:\\test\\resources\\remove_file_success_forum.html");
        Assert.assertTrue(alert.contains("Các tệp tin đã được xóa thành công"), "Xóa ảnh thất bại!");
    }

    @Test(description = "QLBV_DD-52: Kiểm tra upload ảnh dung lượng lớn")
    public void TC52_UploadLargeImage() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("title", "Diễn đàn Test Large Image");
        forumData.put("type", "105100");
        forumData.put("language", "1");
        forumData.put("audience", "100");
        forumPage.fillForumInfo(forumData);
        String filePath = "C:\\Users\\Admin\\Downloads\\large_image.jpg";
        String alert = forumPage.uploadLargeFile(filePath);
        forumPage.capturePageSource("C:\\test\\resources\\large_image_forum.html");
        Assert.assertTrue(alert.contains("Dung lượng file quá lớn") || alert.isEmpty(), "Thông báo không khớp khi upload ảnh lớn");
    }

    @Test(description = "QLBV_DD-53: Kiểm tra upload tệp đính kèm hợp lệ")
    public void TC53_UploadValidAttachment() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("title", "Diễn đàn Test Attachment");
        forumData.put("type", "105100");
        forumData.put("language", "1");
        forumData.put("audience", "100");
        forumPage.fillForumInfo(forumData);
        String filePath = "C:\\Users\\Admin\\Downloads\\attachment.pdf";
        String alert = forumPage.uploadFile(filePath);
        forumPage.capturePageSource("C:\\test\\resources\\upload_attachment_forum.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Upload tệp hợp lệ thất bại!");
    }

    @Test(description = "QLBV_DD-54: Kiểm tra upload nhiều file")
    public void TC54_UploadMultipleFiles() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("title", "Diễn đàn Test Multiple Files");
        forumData.put("type", "105100");
        forumData.put("language", "1");
        forumData.put("audience", "100");
        forumPage.fillForumInfo(forumData);
        String filePath = "C:\\Users\\Admin\\Downloads\\attachment1.pdf";
        String alert = forumPage.uploadFile(filePath);
        forumPage.uploadFile("C:\\Users\\Admin\\Downloads\\attachment2.pdf");
        forumPage.capturePageSource("C:\\test\\resources\\upload_multiple_forum.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Upload nhiều file thất bại!");
    }

    @Test(description = "QLBV_DD-55: Kiểm tra upload file dung lượng lớn")
    public void TC55_UploadLargeFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        Map<String, String> forumData = new HashMap<>();
        forumData.put("title", "Diễn đàn Test Large File");
        forumData.put("type", "105100");
        forumData.put("language", "1");
        forumData.put("audience", "100");
        forumPage.fillForumInfo(forumData);
        String filePath = "C:\\Users\\Admin\\Downloads\\large_file.zip";
        String alert = forumPage.uploadLargeFile(filePath);
        forumPage.capturePageSource("C:\\test\\resources\\large_file_forum.html");
        Assert.assertTrue(alert.contains("Dung lượng file quá lớn") || alert.isEmpty(), "Thông báo không khớp khi upload file lớn");
    }

    @Test(description = "QLBV_DD-56: Kiểm tra ngày đăng dự kiến nhỏ hơn ngày hiện tại")
    public void TC56_PublishDateBeforeCurrent() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumPage(driver);
        forumPage.navigateToForum();
        forumPage.clickAddNew();
        boolean isSaved = forumPage.verifyDateValidation("2025-06-01");
        forumPage.capturePageSource("C:\\test\\resources\\date_validation_forum.html");
        Assert.assertFalse(isSaved, "Lưu thành công với ngày đăng nhỏ hơn ngày hiện tại");
    }
}

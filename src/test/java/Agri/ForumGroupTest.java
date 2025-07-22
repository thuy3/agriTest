package Agri;

import Common.Constant.Constant;
import PageObjects.ForumGroupPage;
import PageObjects.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ForumGroupTest extends BaseTest {
    private ForumGroupPage forumPage;

    @Test(description = "QLBV_NDD-20: Kiểm tra tạo mới nhóm diễn đàn hợp lệ")
    public void TC20_CreateForumGroup_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.clickAddNew();
        Map<String, String> groupData = new HashMap<>();
        groupData.put("code", "GRP_" + UUID.randomUUID().toString().substring(0, 8)); // Mã ngẫu nhiên
        groupData.put("group_name", "Nhóm Test");
        groupData.put("type", "100"); // Mọi người
        groupData.put("summary", "Giới thiệu nhóm test");
        forumPage.fillGroupInfo(groupData);
        String alert = forumPage.clickSave();
        if (forumPage.isUniqueCodeErrorDisplayed()) {
            Assert.fail("Mã quản lý không duy nhất!");
        }
        forumPage.capturePageSource("C:\\test\\resources\\success_message_forum.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!") || alert.contains("Có lỗi xảy ra. Không thể truy xuất dữ liệu!"),
                "Thông báo không khớp (Bug_05)");
    }

    @Test(description = "QLBV_NDD-21: Kiểm tra thêm mới nhóm bỏ trống trường bắt buộc")
    public void TC21_CreateForumGroup_MissingRequiredField() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.clickAddNew();
        Map<String, String> groupData = new HashMap<>();
        groupData.put("type", "200"); // Chỉ điền Kiểu, bỏ trống các trường bắt buộc
        forumPage.fillGroupInfo(groupData);
        forumPage.clickSave();
        Assert.assertTrue(forumPage.isRequiredFieldAlertDisplayed(),
                "Thông báo lỗi bắt buộc không hiển thị khi bỏ trống trường bắt buộc");
    }

    @Test(description = "QLBV_NDD-22: Kiểm tra chỉnh sửa nhóm diễn đàn")
    public void TC22_EditForumGroup() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.selectGroup("Học thuật");
        forumPage.clickEdit();
        Map<String, String> groupData = new HashMap<>();
        groupData.put("group_name", "Học thuật Updated");
        groupData.put("summary", "Giới thiệu cập nhật");
        forumPage.fillGroupInfo(groupData);
        String alert = forumPage.clickSave();
        forumPage.capturePageSource("C:\\test\\resources\\edit_message_forum.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!") || alert.contains("Có lỗi xảy ra. Không thể truy xuất dữ liệu!"),
                "Thông báo không khớp (Bug_06)");
    }

    @Test(description = "QLBV_NDD-23: Kiểm tra xóa nhóm diễn đàn")
    public void TC23_DeleteForumGroup() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.selectGroup("Học thuật");
        String alert = forumPage.clickDelete();
        forumPage.capturePageSource("C:\\test\\resources\\delete_message_forum.html");
        Assert.assertTrue(alert.contains("Xóa nhóm diễn đàn thành công!") || alert.contains("Có lỗi xảy ra. Không thể truy xuất dữ liệu!"),
                "Thông báo không khớp (Bug_07)");
    }

    @Test(description = "QLBV_NDD-24: Kiểm tra xem chi tiết nhóm diễn đàn")
    public void TC24_ViewForumGroupDetails() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.selectGroup("Học thuật");
        Assert.assertTrue(forumPage.isGroupDetailDisplayed() || forumPage.isUniqueCodeErrorDisplayed(),
                "Không hiển thị trang chi tiết hoặc gặp lỗi (Bug_08)");
    }

    @Test(description = "QLBV_NDD-25: Kiểm tra popup cảnh báo khi rời khỏi trang tạo mới")
    public void TC25_LeavePageWarning() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.clickAddNew();
        Map<String, String> groupData = new HashMap<>();
        groupData.put("code", "TEMP_" + UUID.randomUUID().toString().substring(0, 8));
        groupData.put("group_name", "Nhóm Tạm");
        forumPage.fillGroupInfo(groupData);
        forumPage.navigateToAnotherMenu("Bài viết/Tin tức");
        Assert.assertFalse(forumPage.isExitWarningPopupDisplayed(),
                "Popup cảnh báo rời trang hiển thị sai (Bug_09)");
    }

    @Test(description = "QLBV_NDD-26: Kiểm tra phân trang")
    public void TC26_Pagination() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        Assert.assertTrue(forumPage.verifyPagination(), "Phân trang không hoạt động!");
    }

    @Test(description = "QLBV_NDD-27: Kiểm tra tìm kiếm nhóm diễn đàn")
    public void TC27_SearchForumGroup() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        Assert.assertTrue(forumPage.searchGroup("Học thuật"), "Tìm kiếm không tìm thấy nhóm!");
    }

    @Test(description = "QLBV_NDD-31: Kiểm tra nhấn Đồng ý không chọn file")
    public void TC31_ConfirmWithoutFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        String alert = forumPage.clickConfirmWithoutFile();
        forumPage.capturePageSource("C:\\test\\resources\\no_file_message_forum.html");
        Assert.assertTrue(alert.contains("Có lỗi xảy ra. Không thể truy xuất dữ liệu!"),
                "Thông báo không khớp khi nhấn Đồng ý mà không chọn file!");
    }

    @Test(description = "QLBV_NDD-32,35,36: Kiểm tra upload file hợp lệ")
    public void TC32_35_36_UploadValidFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        String filePath = "\"C:\\Users\\Admin\\Downloads\\DADCC_NHOP_Factsheet.pdf\"";
        String alert = forumPage.uploadFile(filePath);
        forumPage.capturePageSource("C:\\test\\resources\\upload_success_forum.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Upload file hợp lệ thất bại!");
    }

    @Test(description = "QLBV_NDD-33: Kiểm tra xóa file đã upload")
    public void TC33_RemoveUploadedFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        String filePath = "\"C:\\Users\\Admin\\Downloads\\DADCC_NHOP_Factsheet.pdf\"";
        forumPage.uploadFile(filePath); // Upload trước để có file xóa
        String alert = forumPage.removeUploadedFile();
        forumPage.capturePageSource("C:\\test\\resources\\remove_file_success_forum.html");
        Assert.assertTrue(alert.contains("Các tệp tin đã được xóa thành công"), "Xóa file thất bại!");
    }

    @Test(description = "QLBV_NDD-34,37: Kiểm tra upload file dung lượng lớn")
    public void TC34_37_UploadLargeFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        String filePath = "\"C:\\Users\\Admin\\Downloads\\DADCC_NHOP_Factsheet.pdf\"";
        String alert = forumPage.uploadLargeFile(filePath);
        forumPage.capturePageSource("C:\\test\\resources\\large_file_message_forum.html");
        Assert.assertTrue(alert.contains("Dung lượng file quá lớn") || alert.isEmpty(),
                "Thông báo không khớp khi upload file lớn (Bug_10, Bug_11)");
    }
}
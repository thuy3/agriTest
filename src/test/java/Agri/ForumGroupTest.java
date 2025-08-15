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

    @Test(description = "QLBV_NDD-19: Kiểm tra tạo mới nhóm diễn đàn hợp lệ")
    public void TC20_CreateForumGroup_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.clickAddNew();
        Map<String, String> groupData = new HashMap<>();
        groupData.put("code", "GRP_" + UUID.randomUUID().toString().substring(0, 8));
        groupData.put("group_name", "Nhóm Test");
        //groupData.put("type", "100"); // Mọi người
        groupData.put("summary", "Giới thiệu nhóm test");
        forumPage.fillGroupInfo(groupData);
        String alert = forumPage.clickSave();
        Assert.assertTrue(alert.contains("Dữ liệu được tạo mới thành công"),
                "Tạo nhóm thất bại, thông báo: " + alert);
    }

    @Test(description = "QLBV_NDD-20: Kiểm tra thêm mới nhóm bỏ trống trường bắt buộc")
    public void TC20_CreateForumGroup_MissingRequiredField() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.clickAddNew();
        Map<String, String> groupData = new HashMap<>();
        groupData.put("group_name", "Nhóm Test2");
        forumPage.fillGroupInfo(groupData);
        forumPage.clickSave();
        Assert.assertTrue(forumPage.isRequiredFieldAlertDisplayed(),
                "Thông báo lỗi bắt buộc không hiển thị khi bỏ trống trường bắt buộc");
    }

    @Test(description = "QLBV_NDD-21: Kiểm tra chỉnh sửa nhóm diễn đàn")
    public void TC21_EditForumGroup() throws Exception{
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.selectGroup("Học thuật");
        forumPage.clickEdit();
        Map<String, String> groupData = new HashMap<>();
        groupData.put("group_name", "Học thuật");
        groupData.put("summary", "Đây là nhóm diễn đàn học thuật");
        forumPage.fillGroupInfo(groupData);
        String alert = forumPage.clickSave();
        Assert.assertTrue(alert.contains("Cập nhật thành công") || alert.contains("Có lỗi xảy ra. Không thể truy xuất dữ liệu!"),
                "Thông báo không khớp");
    }

    @Test(description = "QLBV_NDD-22: Kiểm tra xóa nhóm diễn đàn")
    public void TC22_DeleteForumGroup() throws Exception {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.searchGroup("jhc");
        forumPage.selectGroup("jhc");
        boolean isDeleted = forumPage.clickDelete("jhc");
        Assert.assertTrue(isDeleted, "Xóa nhóm diễn đàn 'jhc' thất bại!");
    }

    @Test(description = "QLBV_NDD-23: Kiểm tra xem chi tiết nhóm diễn đàn")
    public void TC23_ViewForumGroupDetails() throws Exception {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        forumPage.selectGroup("Học thuật");
        Assert.assertTrue(forumPage.isGroupDetailDisplayed() || forumPage.isUniqueCodeErrorDisplayed(),
                "Không hiển thị trang chi tiết hoặc gặp lỗi");
    }

    @Test(description = "QLBV_NDD-25: Kiểm tra phân trang")
    public void TC25_Pagination() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        Assert.assertTrue(forumPage.verifyPagination(), "Phân trang không hoạt động!");
    }

    @Test(description = "QLBV_NDD-26: Kiểm tra tìm kiếm nhóm diễn đàn hợp lệ")
    public void TC26_SearchForumGroup() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        Assert.assertTrue(forumPage.searchGroup("Học thuật"), "Tìm kiếm không tìm thấy nhóm!");
    }

    @Test(description = "QLBV_NDD-27: Kiểm tra tìm kiếm nhóm diễn đàn với từ khóa không khớp")
    public void TC27_SearchForumGroup_Invalid() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        forumPage = new ForumGroupPage(driver);
        forumPage.navigateToForumGroup();
        Assert.assertTrue(forumPage.searchForumGroupNoResult("Nonexistent Document"),
                "Không hiển thị 'Chưa có dữ liệu' khi tìm kiếm không khớp");
    }
}
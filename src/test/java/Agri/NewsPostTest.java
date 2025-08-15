package Agri;

import Common.Constant.Constant;
import PageObjects.LoginPage;
import PageObjects.NewsPostPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsPostTest extends BaseTest {
    private NewsPostPage newsPostPage;

    @Test(description = "QLBV_TT-01: Kiểm tra hiển thị nút lọc trạng thái")
    public void TC01_CheckFilterButtonDisplayed() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        boolean isDisplayed = newsPostPage.checkFilterButtonDisplayed();
        Assert.assertTrue(isDisplayed, "Nút lọc trạng thái không hiển thị đầy đủ các trạng thái: Nháp, Ẩn, Chờ duyệt, Duyệt nội dung, Được hiển thị, Tất cả");
    }

    @Test(description = "QLBV_TT-04: Kiểm tra tạo mới bài viết hợp lệ")
    public void TC04_CreateNewsPost_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToNewsPostPage();
        newsPostPage.clickAddNewNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post");
        //postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/08/2025");
        postData.put("summary", "Đây là bài test");
        postData.put("content", "Test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.clickSave();
        Assert.assertTrue(newsPostPage.isSuccessMessageDisplayed(), "Thông báo thành công không hiển thị sau khi tạo bài viết");
    }

    @Test(description = "QLBV_TT-05: Kiểm tra thêm mới bài viết bỏ trống trường bắt buộc")
    public void TC05_CreateNewsPost_MissingRequiredField() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToNewsPostPage();
        newsPostPage.clickAddNewNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post");
        //postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "");
        postData.put("summary", ""); // Bỏ trống Nội dung tóm tắt
        postData.put("content", "");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\required_field_error.html");
        Assert.assertTrue(newsPostPage.isRequiredFieldErrorDisplayed(), "Thông báo lỗi bắt buộc không hiển thị");
    }

    @Test(description = "QLBV_TT-08: Kiểm tra xem chi tiết bài viết")
    public void TC08_ViewPostDetails() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToNewsPostPage();
        newsPostPage.selectDocument("abc");
        Assert.assertTrue(newsPostPage.isDocumentDetailDisplayed(), "Không hiển thị trang chi tiết văn bản");
    }

    @Test(description = "QLBV_TT-12: Kiểm tra tìm kiếm bài viết với từ khóa hợp lệ")
    public void TC12_SearchNewsPost_ValidKeyword() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToNewsPostPage();
        Assert.assertTrue(newsPostPage.searchNewsPost("abc"), "Tìm kiếm không tìm thấy văn bản!");
    }

    @Test(description = "QLBV_TT-13: Kiểm tra tìm kiếm với từ khóa không khớp")
    public void TC13_SearchNewsPost_InvalidKeyword() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToNewsPostPage();
        Assert.assertTrue(newsPostPage.searchNewsNoResult("Nonexistent Document"), "Không hiển thị 'Chưa có dữ liệu' khi tìm kiếm không khớp");
    }

    @Test(description = "QLBV_TT-06: Kiểm tra chỉnh sửa bài viết")
    public void TC06_EditNewsPost_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToNewsPostPage();
        newsPostPage.searchNewsPost("Test post to edit");
        newsPostPage.selectDocument("Test post to edit");
        newsPostPage.editNewsPost();
        Map<String, String> postData = new HashMap<>();
        //postData.put("title", "Edited Test post");
        //postData.put("summary", "Bài test đã chỉnh sửa");
        postData.put("content", "Nội dung đã chỉnh sửa");
        newsPostPage.enterPostDetails(postData);
        String result = newsPostPage.clickSave();
        Assert.assertTrue(newsPostPage.isSuccessMessageDisplayed(), "Thông báo thành công không hiển thị sau khi chỉnh sửa bài viết");
    }

    @Test(description = "QLBV_TT-07: Kiểm tra xóa bài viết")
    public void TC07_DeleteNewsPost_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToNewsPostPage();
        String title = "aaaa";
        newsPostPage.searchNewsPost(title);
        newsPostPage.selectDocument(title);
        newsPostPage.deleteNewsPost();
        newsPostPage.searchNewsPost(title);
        Assert.assertTrue(newsPostPage.isPostHidden(title), "Trạng thái bài viết không chuyển thành Ẩn sau khi xóa");
    }


    @Test(description = "QLBV_TT-03: Kiểm tra lọc nhiều lần liên tiếp")
    public void TC03_FilterMultipleTimes() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        List<String> statuses = Arrays.asList("Nháp", "Chờ duyệt", "Duyệt nội dung", "Được hiển thị", "Không duyệt", "Ẩn", "Tất cả");
        boolean allFiltered = true;
        for (String status : statuses) {
            boolean isFiltered = newsPostPage.filterByStatus(status);
            if (!isFiltered) {
                allFiltered = false;
                break;
            }
        }
        Assert.assertTrue(allFiltered, "Lọc nhiều lần liên tiếp bị lỗi, một số trạng thái bị che mất (Bug_01)");
    }


//    @Test(description = "QLBV_TT-11: Kiểm tra chức năng phân trang")
//    public void TC11_CheckPagination() {
//        driver.get(Constant.BASE_URL);
//        LoginPage loginPage = new LoginPage(driver);
//        loginPage.login();
//        newsPostPage = new NewsPostPage(driver);
//        newsPostPage.navigateToNewsPostPage();
//        // Tạo nhiều bài viết để có phân trang
//        for (int i = 1; i <= 11; i++) {
//            newsPostPage.clickAddNewNewsPost();
//            Map<String, String> postData = new HashMap<>();
//            postData.put("title", "Test post for pagination " + i);
//            postData.put("type", "TT");
//            postData.put("author", "TT");
//            postData.put("publishDate", "20/06/2025");
//            postData.put("summary", "Bài test phân trang " + i);
//            postData.put("content", "Nội dung test");
//            newsPostPage.enterPostDetails(postData);
//            newsPostPage.clickSave();
//        }
//        // Kiểm tra chuyển trang
//        boolean isPageChanged = newsPostPage.navigateToPage("2");
//        newsPostPage.capturePageSource("C:\\test\\resources\\pagination.html");
//        Assert.assertTrue(isPageChanged, "Chuyển trang không hoạt động đúng");
//    }
}
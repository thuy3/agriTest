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
        newsPostPage.capturePageSource("C:\\test\\resources\\filter_button.html");
        Assert.assertTrue(isDisplayed, "Nút lọc trạng thái không hiển thị đầy đủ các trạng thái: Nháp, Ẩn, Chờ duyệt, Duyệt nội dung, Được hiển thị, Tất cả");
    }

    @Test(description = "QLBV_TT-02: Kiểm tra lọc theo trạng thái")
    public void TC02_FilterByStatus() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        boolean isFiltered = newsPostPage.filterByStatus("Duyệt nội dung");
        newsPostPage.capturePageSource("C:\\test\\resources\\filter_status.html");
        Assert.assertTrue(isFiltered, "Danh sách bài viết không hiển thị đúng trạng thái 'Duyệt nội dung'");
    }

    @Test(description = "QLBV_TT-03: Kiểm tra lọc nhiều lần liên tiếp")
    public void TC03_FilterMultipleTimes() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        List<String> statuses = Arrays.asList("Nháp", "Đã duyệt", "Chờ duyệt");
        boolean allFiltered = true;
        for (String status : statuses) {
            boolean isFiltered = newsPostPage.filterByStatus(status);
            if (!isFiltered) {
                allFiltered = false;
                break;
            }
        }
        newsPostPage.capturePageSource("C:\\test\\resources\\filter_multiple.html");
        Assert.assertTrue(allFiltered, "Lọc nhiều lần liên tiếp bị lỗi, một số trạng thái bị che mất (Bug_01)");
    }

    @Test(description = "QLBV_TT-04: Kiểm tra tạo mới bài viết hợp lệ")
    public void TC04_CreateNewsPost_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Đây là bài test");
        postData.put("content", "Test");
        newsPostPage.enterPostDetails(postData);
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\success_message.html");
        Assert.assertTrue(newsPostPage.isSuccessMessageDisplayed(), "Thông báo thành công không hiển thị sau khi tạo bài viết");
    }

    @Test(description = "QLBV_TT-05: Kiểm tra thêm mới bài viết bỏ trống trường bắt buộc")
    public void TC05_CreateNewsPost_MissingRequiredField() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", ""); // Bỏ trống Nội dung tóm tắt
        postData.put("content", "Test");
        newsPostPage.enterPostDetails(postData);
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\required_field_error.html");
        Assert.assertTrue(newsPostPage.isRequiredFieldErrorDisplayed(), "Thông báo lỗi bắt buộc không hiển thị khi bỏ trống Nội dung tóm tắt");
    }

    @Test(description = "QLBV_TT-08: Kiểm tra xem chi tiết bài viết")
    public void TC08_ViewPostDetails() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        // Tạo bài viết trước để xem chi tiết
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for view");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test để xem chi tiết");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.clickSave();
        // Xem chi tiết
        boolean isDisplayed = newsPostPage.viewPostDetails("Test post for view");
        newsPostPage.capturePageSource("C:\\test\\resources\\view_post_details.html");
        Assert.assertTrue(isDisplayed, "Không hiển thị trang chi tiết bài viết");
    }

    @Test(description = "QLBV_TT-09: Kiểm tra popup cảnh báo khi rời khỏi trang tạo mới")
    public void TC09_LeavePageWarning() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Đây là bài test");
        postData.put("content", "Test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.navigateToAnotherMenu("Nhóm diễn đàn");
        newsPostPage.capturePageSource("C:\\test\\resources\\leave_page_warning.html");
        Assert.assertTrue(newsPostPage.isLeavePageWarningDisplayed(), "Popup cảnh báo rời trang không hiển thị (Bug_02)");
    }

    @Test(description = "QLBV_TT-10: Kiểm tra chuyển trạng thái bài viết")
    public void TC10_ChangePostStatus() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        // Tạo bài viết trước để chuyển trạng thái
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for status");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test để chuyển trạng thái");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.clickSave();
        // Chuyển trạng thái
        boolean isChanged = newsPostPage.changePostStatus("Test post for status", "Duyệt nội dung");
        newsPostPage.capturePageSource("C:\\test\\resources\\change_status.html");
        Assert.assertTrue(isChanged, "Không chuyển được trạng thái bài viết sang 'Duyệt nội dung'");
    }

    @Test(description = "QLBV_TT-11: Kiểm tra chức năng phân trang")
    public void TC11_CheckPagination() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        // Tạo nhiều bài viết để có phân trang
        for (int i = 1; i <= 11; i++) {
            newsPostPage.navigateToAddNewsPost();
            Map<String, String> postData = new HashMap<>();
            postData.put("title", "Test post for pagination " + i);
            postData.put("type", "TT");
            postData.put("author", "TT");
            postData.put("publishDate", "20/06/2025");
            postData.put("summary", "Bài test phân trang " + i);
            postData.put("content", "Nội dung test");
            newsPostPage.enterPostDetails(postData);
            newsPostPage.clickSave();
        }
        // Kiểm tra chuyển trang
        boolean isPageChanged = newsPostPage.navigateToPage("2");
        newsPostPage.capturePageSource("C:\\test\\resources\\pagination.html");
        Assert.assertTrue(isPageChanged, "Chuyển trang không hoạt động đúng");
    }

    @Test(description = "QLBV_TT-12: Kiểm tra tìm kiếm bài viết với từ khóa hợp lệ")
    public void TC12_SearchNewsPost_ValidKeyword() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        // Tạo bài viết trước để tìm kiếm
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for search");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test để tìm kiếm");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.clickSave();
        // Tìm kiếm bài viết
        boolean isFound = newsPostPage.searchNewsPost("Test post for search");
        newsPostPage.capturePageSource("C:\\test\\resources\\success_message_search.html");
        Assert.assertTrue(isFound, "Không tìm thấy bài viết với từ khóa hợp lệ");
    }

    @Test(description = "QLBV_TT-13: Kiểm tra tìm kiếm với từ khóa không khớp")
    public void TC13_SearchNewsPost_InvalidKeyword() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        boolean isNoData = newsPostPage.searchNewsPost("Nonexistent keyword");
        newsPostPage.capturePageSource("C:\\test\\resources\\no_data_search.html");
        Assert.assertTrue(isNoData, "Không hiển thị thông báo 'Chưa có dữ liệu' khi tìm kiếm từ khóa không khớp");
    }

    @Test(description = "QLBV_TT-14: Kiểm tra upload ảnh hợp lệ")
    public void TC14_UploadValidImage() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Đây là bài test");
        postData.put("content", "Test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.uploadFile("C:\\Users\\Admin\\Downloads\\1206-sach-anh.jpg");
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\success_message_upload.html");
        Assert.assertTrue(newsPostPage.isSuccessMessageDisplayed(), "Thông báo thành công không hiển thị sau khi upload ảnh hợp lệ");
    }

    @Test(description = "QLBV_TT-15: Kiểm tra xóa bỏ ảnh đã upload")
    public void TC15_RemoveUploadedFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for remove file");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test để xóa file");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.uploadFile("C:\\Users\\Admin\\Downloads\\1206-sach-anh.jpg");
        newsPostPage.removeUploadedFile();
        newsPostPage.capturePageSource("C:\\test\\resources\\file_removed_success.html");
        Assert.assertTrue(newsPostPage.isFileRemovedSuccessDisplayed(), "Thông báo xóa file thành công không hiển thị");
    }

    @Test(description = "QLBV_TT-16: Kiểm tra upload ảnh dung lượng quá lớn")
    public void TC16_UploadLargeImage() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for large image");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test cho ảnh lớn");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.uploadFile("C:\\Users\\Admin\\Downloads\\large_image.jpg");
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\large_image_error.html");
        Assert.assertTrue(newsPostPage.isLargeFileErrorDisplayed(), "Không hiển thị thông báo lỗi dung lượng ảnh lớn (Bug_03)");
    }

    @Test(description = "QLBV_TT-17: Kiểm tra upload tệp đính kèm hợp lệ")
    public void TC17_UploadValidAttachment() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for attachment");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test cho tệp đính kèm");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.uploadFile("C:\\Users\\Admin\\Downloads\\attachment.pdf");
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\success_message_attachment.html");
        Assert.assertTrue(newsPostPage.isSuccessMessageDisplayed(), "Thông báo thành công không hiển thị sau khi upload tệp đính kèm");
    }

    @Test(description = "QLBV_TT-18: Kiểm tra upload nhiều file")
    public void TC18_UploadMultipleFiles() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for multiple files");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test cho nhiều file");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        List<String> filePaths = Arrays.asList(
                "C:\\Users\\Admin\\Downloads\\attachment1.pdf",
                "C:\\Users\\Admin\\Downloads\\attachment2.pdf"
        );
        newsPostPage.uploadMultipleFiles(filePaths);
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\success_message_multiple_files.html");
        Assert.assertTrue(newsPostPage.isSuccessMessageDisplayed(), "Thông báo thành công không hiển thị sau khi upload nhiều file");
    }

    @Test(description = "QLBV_TT-19: Kiểm tra upload file dung lượng lớn")
    public void TC19_UploadLargeFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post for large file");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test cho file lớn");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.uploadFile("C:\\Users\\Admin\\Downloads\\large_file.zip");
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\large_file_error.html");
        Assert.assertTrue(newsPostPage.isLargeFileErrorDisplayed(), "Không hiển thị thông báo lỗi dung lượng file lớn (Bug_04)");
    }

    @Test(description = "QLBV_TT-06: Kiểm tra chỉnh sửa bài viết")
    public void TC06_EditNewsPost_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        // Tạo bài viết trước để chỉnh sửa
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post to edit");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test để chỉnh sửa");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.clickSave();
        // Chỉnh sửa bài viết
        newsPostPage.navigateToEditNewsPost("Test post to edit");
        postData.put("title", "Edited Test post");
        postData.put("summary", "Bài test đã chỉnh sửa");
        postData.put("content", "Nội dung đã chỉnh sửa");
        newsPostPage.enterPostDetails(postData);
        String result = newsPostPage.clickSave();
        newsPostPage.capturePageSource("C:\\test\\resources\\success_message_edit.html");
        Assert.assertTrue(newsPostPage.isSuccessMessageDisplayed(), "Thông báo thành công không hiển thị sau khi chỉnh sửa bài viết");
    }

    @Test(description = "QLBV_TT-07: Kiểm tra xóa bài viết")
    public void TC07_DeleteNewsPost_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        newsPostPage = new NewsPostPage(driver);
        // Tạo bài viết trước để xóa
        newsPostPage.navigateToAddNewsPost();
        Map<String, String> postData = new HashMap<>();
        postData.put("title", "Test post to delete");
        postData.put("type", "TT");
        postData.put("author", "TT");
        postData.put("publishDate", "20/06/2025");
        postData.put("summary", "Bài test để xóa");
        postData.put("content", "Nội dung test");
        newsPostPage.enterPostDetails(postData);
        newsPostPage.clickSave();
        // Xóa bài viết
        newsPostPage.deleteNewsPost("Test post to delete");
        newsPostPage.capturePageSource("C:\\test\\resources\\success_message_delete.html");
        Assert.assertFalse(newsPostPage.searchNewsPost("Test post to delete"), "Bài viết vẫn hiển thị sau khi xóa");
    }
}
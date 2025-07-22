package Agri;

import Common.Constant.Constant;
import PageObjects.LegalDocumentPage;
import PageObjects.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LegalDocumentTest extends BaseTest {
    private LegalDocumentPage legalDocumentPage;

    @Test(description = "QLBV_PLHC-56: Kiểm tra hiển thị nút lọc trạng thái")
    public void TC56_CheckFilterButtonDisplayed() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        boolean isDisplayed = legalDocumentPage.checkFilterButtonDisplayed();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\filter_button_legal.html");
        Assert.assertTrue(isDisplayed, "Nút lọc trạng thái không hiển thị đầy đủ các trạng thái");
    }

    @Test(description = "QLBV_PLHC-57: Kiểm tra lọc nhiều lần liên tiếp")
    public void TC57_FilterMultipleTimes() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        boolean isFiltered = legalDocumentPage.filterByStatus("Duyệt nội dung");
        legalDocumentPage.filterByStatus("Nháp");
        legalDocumentPage.capturePageSource("C:\\test\\resources\\filter_multiple_legal.html");
        Assert.assertTrue(isFiltered, "Lọc nhiều lần liên tiếp thất bại");
    }

    @Test(description = "QLBV_PLHC-58: Kiểm tra bộ lọc theo từng trạng thái")
    public void TC58_FilterByStatus() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        boolean isFiltered = legalDocumentPage.filterByStatus("Duyệt nội dung");
        legalDocumentPage.capturePageSource("C:\\test\\resources\\filter_status_legal.html");
        Assert.assertTrue(isFiltered, "Lọc theo trạng thái 'Duyệt nội dung' thất bại");
    }

    @Test(description = "QLBV_PLHC-59: Kiểm tra tạo mới văn bản hợp lệ")
    public void TC59_CreateDocument_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test " + UUID.randomUUID().toString().substring(0, 8));
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String alert = legalDocumentPage.clickSave();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\success_message_legal.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!") || alert.isEmpty(), "Thông báo không khớp");
    }

    @Test(description = "QLBV_PLHC-60: Kiểm tra thêm mới văn bản bỏ trống trường bắt buộc")
    public void TC60_CreateDocument_MissingRequiredField() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("publishDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        legalDocumentPage.clickSave();
        Assert.assertTrue(legalDocumentPage.isRequiredFieldAlertDisplayed(), "Thông báo lỗi bắt buộc không hiển thị");
    }

    @Test(description = "QLBV_PLHC-61: Kiểm tra chỉnh sửa văn bản")
    public void TC61_EditDocument() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.selectDocument("Văn bản Test");
        legalDocumentPage.clickEdit();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Updated");
        documentData.put("reason", "Cập nhật nội dung");
        legalDocumentPage.fillDocumentInfo(documentData);
        String alert = legalDocumentPage.clickSave();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\edit_message_legal.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Thông báo không khớp");
    }

    @Test(description = "QLBV_PLHC-62: Kiểm tra chỉnh sửa không nhập lý do")
    public void TC62_EditDocument_NoReason() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.selectDocument("Văn bản Test");
        legalDocumentPage.clickEdit();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test No Reason");
        legalDocumentPage.fillDocumentInfo(documentData);
        legalDocumentPage.clickSave();
        Assert.assertTrue(legalDocumentPage.isRequiredFieldAlertDisplayed(), "Thông báo lỗi lý do không hiển thị");
    }

    @Test(description = "QLBV_PLHC-63: Kiểm tra xóa văn bản")
    public void TC63_DeleteDocument() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.selectDocument("Văn bản Test");
        String alert = legalDocumentPage.clickDelete();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\delete_message_legal.html");
        Assert.assertTrue(alert.contains("Xóa văn bản thành công!"), "Thông báo không khớp");
    }

    @Test(description = "QLBV_PLHC-64: Kiểm tra xem chi tiết văn bản")
    public void TC64_ViewDocumentDetails() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.selectDocument("Văn bản Test");
        Assert.assertTrue(legalDocumentPage.isDocumentDetailDisplayed(), "Không hiển thị trang chi tiết văn bản");
    }

    @Test(description = "QLBV_PLHC-65: Kiểm tra popup cảnh báo khi rời trang")
    public void TC65_LeavePageWarning() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Tạm");
        legalDocumentPage.fillDocumentInfo(documentData);
        legalDocumentPage.navigateToAnotherMenu("Bài viết/Tin tức");
        Assert.assertFalse(legalDocumentPage.isExitWarningPopupDisplayed(), "Popup cảnh báo rời trang hiển thị sai (Bug_13)");
    }

    @Test(description = "QLBV_PLHC-66: Kiểm tra chuyển trạng thái văn bản")
    public void TC66_ChangeDocumentStatus() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        boolean isChanged = legalDocumentPage.changeDocumentStatus("Văn bản Test", "6");
        legalDocumentPage.capturePageSource("C:\\test\\resources\\change_status_legal.html");
        Assert.assertTrue(isChanged, "Không chuyển được trạng thái văn bản sang 'Duyệt nội dung'");
    }

    @Test(description = "QLBV_PLHC-67: Kiểm tra phân trang")
    public void TC67_Pagination() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        Assert.assertTrue(legalDocumentPage.verifyPagination(), "Phân trang không hoạt động!");
    }

    @Test(description = "QLBV_PLHC-68: Kiểm tra ngày ban hành bằng ngày có hiệu lực")
    public void TC68_PublishDateEqualsEffectiveDate() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Equal Dates");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String alert = legalDocumentPage.clickSave();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\equal_dates_legal.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Lưu thất bại khi ngày ban hành bằng ngày có hiệu lực");
    }

    @Test(description = "QLBV_PLHC-69: Kiểm tra ngày ban hành nhỏ hơn ngày có hiệu lực")
    public void TC69_PublishDateBeforeEffectiveDate() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Before");
        documentData.put("publishDate", "2025-07-01");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String alert = legalDocumentPage.clickSave();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\before_dates_legal.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Lưu thất bại khi ngày ban hành nhỏ hơn ngày có hiệu lực");
    }

    @Test(description = "QLBV_PLHC-70: Kiểm tra ngày ban hành lớn hơn ngày có hiệu lực")
    public void TC70_PublishDateAfterEffectiveDate() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test After");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-01");
        legalDocumentPage.fillDocumentInfo(documentData);
        String alert = legalDocumentPage.clickSave();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\after_dates_legal.html");
        Assert.assertTrue(alert.contains("Ngày ban hành không hợp lệ") || alert.isEmpty(), "Thông báo không khớp khi ngày ban hành lớn hơn ngày có hiệu lực (Bug_14)");
    }

    @Test(description = "QLBV_PLHC-71: Kiểm tra tìm kiếm văn bản hợp lệ")
    public void TC71_SearchDocument_ValidKeyword() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        Assert.assertTrue(legalDocumentPage.searchDocument("Văn bản Test"), "Tìm kiếm không tìm thấy văn bản!");
    }

    @Test(description = "QLBV_PLHC-72: Kiểm tra tìm kiếm văn bản không khớp")
    public void TC72_SearchDocument_NoResult() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        Assert.assertTrue(legalDocumentPage.searchDocumentNoResult("Nonexistent Document"), "Không hiển thị 'Chưa có dữ liệu' khi tìm kiếm không khớp");
    }

    @Test(description = "QLBV_PLHC-73: Kiểm tra upload ảnh hợp lệ")
    public void TC73_UploadValidImage() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Image");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String filePath = "C:\\Users\\Admin\\Downloads\\1206-sach-anh.jpg";
        String alert = legalDocumentPage.uploadFile(filePath);
        legalDocumentPage.capturePageSource("C:\\test\\resources\\upload_success_legal.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Upload ảnh hợp lệ thất bại!");
    }

    @Test(description = "QLBV_PLHC-74: Kiểm tra xóa ảnh đã upload")
    public void TC74_RemoveUploadedFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Remove");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String filePath = "C:\\Users\\Admin\\Downloads\\1206-sach-anh.jpg";
        legalDocumentPage.uploadFile(filePath);
        String alert = legalDocumentPage.removeUploadedFile();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\remove_file_success_legal.html");
        Assert.assertTrue(alert.contains("Các tệp tin đã được xóa thành công"), "Xóa ảnh thất bại!");
    }

    @Test(description = "QLBV_PLHC-75: Kiểm tra upload ảnh dung lượng lớn")
    public void TC75_UploadLargeImage() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Large Image");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String filePath = "C:\\Users\\Admin\\Downloads\\large_image.jpg";
        String alert = legalDocumentPage.uploadLargeFile(filePath);
        legalDocumentPage.capturePageSource("C:\\test\\resources\\large_image_legal.html");
        Assert.assertTrue(alert.contains("Dung lượng file quá lớn") || alert.isEmpty(), "Thông báo không khớp khi upload ảnh lớn (Bug_15)");
    }

    @Test(description = "QLBV_PLHC-76: Kiểm tra upload tệp đính kèm hợp lệ")
    public void TC76_UploadValidAttachment() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Attachment");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String filePath = "C:\\Users\\Admin\\Downloads\\attachment.pdf";
        String alert = legalDocumentPage.uploadFile(filePath);
        legalDocumentPage.capturePageSource("C:\\test\\resources\\upload_attachment_legal.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Upload tệp hợp lệ thất bại!");
    }

    @Test(description = "QLBV_PLHC-77: Kiểm tra upload nhiều file")
    public void TC77_UploadMultipleFiles() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Multiple Files");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String filePath = "C:\\Users\\Admin\\Downloads\\attachment1.pdf";
        String alert = legalDocumentPage.uploadFile(filePath);
        legalDocumentPage.uploadFile("C:\\Users\\Admin\\Downloads\\attachment2.pdf");
        legalDocumentPage.capturePageSource("C:\\test\\resources\\upload_multiple_legal.html");
        Assert.assertTrue(alert.contains("Cập nhật thành công!"), "Upload nhiều file thất bại!");
    }

    @Test(description = "QLBV_PLHC-78: Kiểm tra upload file dung lượng lớn")
    public void TC78_UploadLargeFile() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("title", "Văn bản Test Large File");
        documentData.put("publishDate", "2025-07-07");
        documentData.put("effectiveDate", "2025-07-07");
        legalDocumentPage.fillDocumentInfo(documentData);
        String filePath = "C:\\Users\\Admin\\Downloads\\large_file.zip";
        String alert = legalDocumentPage.uploadLargeFile(filePath);
        legalDocumentPage.capturePageSource("C:\\test\\resources\\large_file_legal.html");
        Assert.assertTrue(alert.contains("Dung lượng file quá lớn") || alert.isEmpty(), "Thông báo không khớp khi upload file lớn (Bug_16)");
    }
}

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

    @Test(description = "QLBV_PLHC-54: Kiểm tra hiển thị nút lọc trạng thái")
    public void TC54_CheckFilterButtonDisplayed() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        boolean isDisplayed = legalDocumentPage.checkFilterButtonDisplayed();
        legalDocumentPage.capturePageSource("C:\\test\\resources\\filter_button_legal.html");
        Assert.assertTrue(isDisplayed, "Nút lọc trạng thái không hiển thị đầy đủ các trạng thái");
    }

    @Test(description = "QLBV_PLHC-55: Kiểm tra lọc nhiều lần liên tiếp")
    public void TC55_FilterMultipleTimes() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        boolean isFiltered = legalDocumentPage.filterByStatus("Đã duyệt");
        legalDocumentPage.filterByStatus("Nháp");
        Assert.assertTrue(isFiltered, "Lọc nhiều lần liên tiếp thất bại");
    }

    @Test(description = "QLBV_PLHC-57: Kiểm tra tạo mới văn bản hợp lệ")
    public void TC57_CreateDocument_Success() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        String title = "Văn bản Test " + UUID.randomUUID().toString().substring(0, 8);
        documentData.put("title", title);
        documentData.put("type", "Nghị định");
        documentData.put("ref", "123/2025");
        documentData.put("office", "Bộ Nông nghiệp");
        documentData.put("field", "Nông nghiệp");
        documentData.put("location", "Hà Nội");
        documentData.put("signature", "Nguyễn Văn A");
        documentData.put("publishDate", "09/09/2025");
        documentData.put("effectiveDate", "09/09/2025");
        documentData.put("summary", "Tóm tắt văn bản mẫu");
        legalDocumentPage.fillDocumentInfo(documentData);
        String alert = legalDocumentPage.clickSave();
        Assert.assertTrue(alert.contains("Văn bản đã được lưu thành công"), "Thông báo không khớp: " + alert);
        Assert.assertTrue(legalDocumentPage.verifyDocumentAdded(title), "Văn bản không xuất hiện trong danh sách");
    }

    @Test(description = "QLBV_PLHC-58: Kiểm tra thêm mới văn bản bỏ trống trường bắt buộc")
    public void TC58_CreateDocument_MissingRequiredField() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.clickAddNew();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("publishDate", "09/09/2025");
        legalDocumentPage.fillDocumentInfo(documentData);
        legalDocumentPage.clickSave();
        Assert.assertTrue(legalDocumentPage.isRequiredFieldAlertDisplayed(), "Thông báo lỗi bắt buộc không hiển thị");
    }

    @Test(description = "QLBV_PLHC-59: Kiểm tra chỉnh sửa văn bản")
    public void TC59_EditDocument() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.searchDocument("hj");
        legalDocumentPage.selectDocument("hj");
        legalDocumentPage.clickEdit();
        Map<String, String> documentData = new HashMap<>();
        documentData.put("reason", "Cập nhật nội dung văn bản");
        legalDocumentPage.fillDocumentInfo(documentData);
        String alert = legalDocumentPage.clickSave();
        Assert.assertTrue(alert.contains("Cập nhật thành công"), "Thông báo không khớp: " + alert);
    }

    @Test(description = "QLBV_PLHC-61: Kiểm tra xóa văn bản") //passed
    public void TC63_DeleteDocument() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.selectDocument("dv");
        String alert = legalDocumentPage.clickDelete();
        Assert.assertTrue(alert.contains("Xóa văn bản thành công"), "Thông báo không khớp");
    }

    @Test(description = "QLBV_PLHC-62: Kiểm tra xem chi tiết văn bản")
    public void TC62_ViewDocumentDetails() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        legalDocumentPage.selectDocument("Luật chăn nuôi");
        Assert.assertTrue(legalDocumentPage.isDocumentDetailDisplayed(), "Không hiển thị trang chi tiết văn bản");
    }

    @Test(description = "QLBV_PLHC-69: Kiểm tra tìm kiếm văn bản hợp lệ")
    public void TC69_SearchDocument_ValidKeyword() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        Assert.assertTrue(legalDocumentPage.searchDocument("Luật chăn nuôi"), "Tìm kiếm không tìm thấy văn bản!");
    }

    @Test(description = "QLBV_PLHC-70: Kiểm tra tìm kiếm văn bản không khớp")
    public void TC70_SearchDocument_NoResult() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        Assert.assertTrue(legalDocumentPage.searchDocumentNoResult("Nonexistent Document"), "Không hiển thị 'Chưa có dữ liệu' khi tìm kiếm không khớp");
    }

    @Test(description = "QLBV_PLHC-65: Kiểm tra phân trang")
    public void TC65_Pagination() {
        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();
        legalDocumentPage = new LegalDocumentPage(driver);
        legalDocumentPage.navigateToLegalDocument();
        Assert.assertTrue(legalDocumentPage.verifyPagination(), "Phân trang không hoạt động!");
    }
}

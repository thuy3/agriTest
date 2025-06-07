//package Agri;//package Agri;
//
//import Common.Constant.Constant;
//import PageObjects.LoginPage;
//import PageObjects.SpecialtyInfoPage;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
//public class SpecialtyInfoTest extends BaseTest {
//
//    @Test
//    public void TC_DS_01_AddValidSpecialty() {
//        // Bước 1: Đăng nhập
//        driver.get(Constant.BASE_URL);
//        LoginPage loginPage = new LoginPage(driver);
//        loginPage.login();
//
//        // Bước 2: Điều hướng đến trang Quản lý thông tin đặc sản
//        driver.get("https://agri.inotev.net/view_mat_material.html");
//
//        // Bước 3: Thêm mới đặc sản
//        SpecialtyInfoPage sip = new SpecialtyInfoPage(driver);
//        String uniqueCode = "MITCUCHI" + System.currentTimeMillis();  // Tạo mã duy nhất
//
//        sip.addNewSpecialty(
//                "Chả cá Quy Nhơn",
//                uniqueCode,
//                "250",
//                "Xác nhận",
//                "Bình Định",
//                "Miền Trung",
//                "Đặc sản nổi tiếng",
//                "Chả cá tươi, đóng gói hút chân không",
//                "Cá thu, muối, tiêu, bột mì",
//                "Xay nhuyễn, hấp, đóng gói",
//                "Không chất bảo quản"
//        );
//        sip.clickSaveButton();
//        // Bước 4: Xác nhận toast hiển thị thành công
//        Assert.assertTrue(sip.isToastDisplayed(), "Thông báo 'Lưu thành công' không hiển thị");
//    }
//}


package Agri;

import Common.Constant.Constant;
import PageObjects.LoginPage;
import PageObjects.SpecialtyInfoPage;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.UUID;

public class SpecialtyInfoTest extends BaseTest {

    @Test
    public void TC_DS_01_AddNewSpecialty() {

        driver.get(Constant.BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login();

        SpecialtyInfoPage specialtyInfoPage = new SpecialtyInfoPage(driver);
        specialtyInfoPage.navigateToSpecialtyManagement();
        String randomCode = "DS_" + UUID.randomUUID().toString().substring(0, 8);

        specialtyInfoPage.clickAddNewButton();
        specialtyInfoPage.addNewSpecialty(
                "Chả cá Quy Nhơn",
                randomCode,
                "250g",
                "Miền Trung",
                "Xác nhận",
                "Đặc sản nổi tiếng",
                "Chả cá tươi, đóng gói kĩ càng",
                "Cá thu, muối, tiêu, bột mì",
                "Xay nhuyễn, hấp, đóng gói",
                "Không chất bảo quản",
                "\"C:\\Users\\Admin\\Downloads\\cha_ca.jpg\"",
                "\"C:\\Users\\Admin\\Downloads\\DADCC_NHOP_Factsheet.pdf\"" // Cần đường dẫn thực
        );

        specialtyInfoPage.clickSaveButton();

        Assert.assertTrue(specialtyInfoPage.verifySpecialtyDetails(
                "Chả cá Quy Nhơn",
                randomCode,
                "250g",
                "Miền Trung",
                "Xác nhận",
                "Đặc sản nổi tiếng",
                "Chả cá tươi, đóng gói kĩ càng",
                "Cá thu, muối, tiêu, bột mì",
                "Xay nhuyễn, hấp, đóng gói",
                "Không chất bảo quản"
        ), "Chi tiết đặc sản không hiển thị đúng");
    }
}
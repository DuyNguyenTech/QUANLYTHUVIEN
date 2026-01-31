package com.qlthuvien.BLL;

import com.qlthuvien.DAL.DAL_Signup;
import com.qlthuvien.DTO.DTO_DocGia; // Sử dụng DTO_DocGia chuẩn
import com.qlthuvien.DTO.TaiKhoan;

public class BLL_Signup {
    
    private DAL_Signup dal = new DAL_Signup();

    // Kiểm tra trùng lặp
    public String checkSignup(String user, String email) {
        // Bên DAL giờ trả về String ("OK" hoặc thông báo lỗi)
        // Nên BLL chỉ cần trả về đúng giá trị đó cho GUI hiển thị
        return dal.checkExist(user, email);
    }

    // Đăng ký tài khoản
    // Tham số đầu vào phải là DTO_DocGia (khớp với DAL)
    public boolean signup(DTO_DocGia dg, TaiKhoan tk) {
        return dal.signup(dg, tk);
    }
}
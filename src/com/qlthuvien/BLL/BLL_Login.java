package com.qlthuvien.BLL;

import com.qlthuvien.DAL.DAL_Login;
import com.qlthuvien.DTO.TaiKhoan;

public class BLL_Login {
    private DAL_Login dalLogin = new DAL_Login();

    public TaiKhoan checkLogin(String user, String pass) {
        return dalLogin.checkLogin(user, pass);
    }
}
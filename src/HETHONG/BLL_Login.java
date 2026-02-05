package HETHONG;

public class BLL_Login {
    private DAL_Login dalLogin = new DAL_Login();

    public DTO_TaiKhoan checkLogin(String user, String pass) {
        return dalLogin.checkLogin(user, pass);
    }
}
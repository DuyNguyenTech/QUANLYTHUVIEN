package CHUNG;

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    // --- CẤU HÌNH GMAIL ---
    // 1. Nhập Email của bạn (Email dùng để gửi)
    private static final String SENDER_EMAIL = "duy0303nt@gmail.com"; 
    
    // 2. Nhập Mật khẩu ứng dụng 16 ký tự vừa tạo
    private static final String APP_PASSWORD = "kaex dpor pafj dloo"; 

    /**
     * Hàm gửi mã xác thực (OTP)
     * @param recipientEmail : Email người nhận
     * @return String : Trả về mã OTP (6 số) nếu thành công, null nếu thất bại
     */
    public static String sendOTP(String recipientEmail) {
        // Tạo mã OTP ngẫu nhiên 6 chữ số
        Random rand = new Random();
        int otpNumber = rand.nextInt(999999);
        String otp = String.format("%06d", otpNumber);

        // Cấu hình SMTP Server của Google
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Thêm dòng này để tránh lỗi bảo mật mới

        // Đăng nhập vào Gmail
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            // Tạo nội dung Email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("MÃ XÁC NHẬN ĐĂNG KÝ - THƯ VIỆN"); // Tiêu đề mail

            // Nội dung mail (HTML) cho đẹp
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd;'>"
                    + "<h2 style='color: #1976D2;'>Xác thực tài khoản thư viện</h2>"
                    + "<p>Xin chào,</p>"
                    + "<p>Mã xác thực (OTP) của bạn là:</p>"
                    + "<h1 style='color: #d32f2f; letter-spacing: 5px;'>" + otp + "</h1>"
                    + "<p>Mã này có hiệu lực trong vòng 5 phút.</p>"
                    + "<p><i>Đây là email tự động, vui lòng không trả lời.</i></p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");

            // Gửi ngay lập tức
            Transport.send(message);
            
            System.out.println(">>> ĐÃ GỬI EMAIL THÀNH CÔNG ĐẾN: " + recipientEmail);
            return otp; // Trả về mã OTP để code so sánh

        } catch (MessagingException e) {
            System.err.println(">>> GỬI MAIL THẤT BẠI!");
            e.printStackTrace(); // In lỗi ra để sửa
            return null;
        }
    }

    // --- HÀM MAIN ĐỂ CHẠY THỬ NGAY LẬP TỨC ---
    public static void main(String[] args) {
        System.out.println("Đang thử gửi email...");
        
        // Bạn hãy nhập email phụ của bạn vào đây để test xem có nhận được không
        String testEmail = "phuocnp25@gmail.com"; 
        
        String result = sendOTP(testEmail);
        
        if (result != null) {
            System.out.println("Thành công! Mã OTP là: " + result);
        } else {
            System.out.println("Thất bại! Xem lỗi phía trên.");
        }
    }
}
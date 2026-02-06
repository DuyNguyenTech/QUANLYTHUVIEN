package CAIDAT;

import CHUNG.DBConnect;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

// [ĐÃ SỬA] Chuyển từ JFrame sang JPanel để nhúng vào giao diện chính
public class GUI_SaoLuuPhucHoi extends JPanel {
    private JButton btnBackup, btnRestore;
    private DBConnect db = new DBConnect();

    public GUI_SaoLuuPhucHoi() {
        // [ĐÃ SỬA] Thiết lập layout cho Panel to
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 253)); // Màu nền đồng bộ

        // --- 1. HEADER (TIÊU ĐỀ) ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lblTitle = new JLabel("SAO LƯU & PHỤC HỒI DỮ LIỆU");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. NỘI DUNG (CANH GIỮA MÀN HÌNH) ---
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(new Color(245, 248, 253));
        
        // Tạo một hộp chứa các nút để canh chỉnh cho đẹp
        JPanel pnlBox = new JPanel(new GridLayout(3, 1, 0, 25)); // 3 dòng, cách nhau 25px
        pnlBox.setOpaque(false);
        
        // [ĐÃ SỬA] Làm nút to lên (300x60) cho hợp với màn hình lớn
        btnBackup = new JButton("SAO LƯU (BACKUP)");
        btnBackup.setPreferredSize(new Dimension(300, 60));
        btnBackup.setBackground(new Color(0, 102, 204));
        btnBackup.setForeground(Color.WHITE);
        btnBackup.setFont(new Font("Arial", Font.BOLD, 16));
        btnBackup.setFocusPainted(false);
        btnBackup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Thêm icon nếu có (tùy chọn)
        // btnBackup.setIcon(new ImageIcon(getClass().getResource("/com/qlthuvien/images/backup.png")));

        btnRestore = new JButton("PHỤC HỒI (RESTORE)");
        btnRestore.setPreferredSize(new Dimension(300, 60));
        btnRestore.setBackground(new Color(204, 0, 0));
        btnRestore.setForeground(Color.WHITE);
        btnRestore.setFont(new Font("Arial", Font.BOLD, 16));
        btnRestore.setFocusPainted(false);
        btnRestore.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblNote = new JLabel("<html><center>Lưu ý: Chức năng yêu cầu máy tính<br>đã cài đặt MySQL/XAMPP và cấu hình đúng đường dẫn.</center></html>", SwingConstants.CENTER);
        lblNote.setFont(new Font("Arial", Font.ITALIC, 14));
        lblNote.setForeground(Color.GRAY);

        pnlBox.add(btnBackup);
        pnlBox.add(btnRestore);
        pnlBox.add(lblNote);

        pnlCenter.add(pnlBox);
        add(pnlCenter, BorderLayout.CENTER);

        // Sự kiện giữ nguyên logic của anh
        btnBackup.addActionListener(e -> xuLyBackup());
        btnRestore.addActionListener(e -> xuLyRestore());
    }

    // --- GIỮ NGUYÊN CODE LOGIC CỦA ANH BÊN DƯỚI ---

    private void xuLyBackup() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chọn thư mục để lưu file Backup");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File folder = fc.getSelectedFile();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = "Backup_" + timeStamp + ".sql";
                String savePath = folder.getAbsolutePath() + File.separator + fileName;

                String user = db.getUser();
                String pass = db.getPass();
                String dbName = db.getDbName();

                // --- ĐƯỜNG DẪN CỦA BẠN (GIỮ NGUYÊN) ---
                String mysqldumpPath = "D:\\XAMPP\\mysql\\bin\\mysqldump.exe";
                // --------------------------------------

                File fileCheck = new File(mysqldumpPath);
                if (!fileCheck.exists()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy file mysqldump tại:\n" + mysqldumpPath);
                    return;
                }

                // --- DÙNG PROCESS BUILDER ---
                java.util.List<String> commands = new java.util.ArrayList<>();
                commands.add(mysqldumpPath);
                
                // [FIX 1] THÊM CỔNG 3307 (Quan trọng vì bạn đổi port)
                commands.add("-P"); 
                commands.add("3307"); 
                // --------------------------------------------------

                commands.add("-u" + user);
                if (!pass.isEmpty()) {
                    commands.add("-p" + pass);
                }

                // [FIX 2] ĐÃ XÓA DÒNG "--column-statistics=0" GÂY LỖI
                
                commands.add("--databases");
                commands.add(dbName);
                commands.add("-r");
                commands.add(savePath);

                ProcessBuilder pb = new ProcessBuilder(commands);
                pb.redirectErrorStream(true); 

                Process process = pb.start();
                
                // Đọc log lỗi
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                int exitCode = process.waitFor();

                // Kiểm tra kết quả
                File f = new File(savePath);
                if (f.exists() && f.length() > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Sao lưu thành công!\nFile: " + savePath);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Sao lưu thất bại! Lỗi:\n" + output.toString());
                    System.out.println("DEBUG ERROR: " + output.toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi Java: " + ex.getMessage());
        }
    }

    private void xuLyRestore() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chọn file .sql để phục hồi");
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String path = file.getAbsolutePath();

                String user = db.getUser();
                String pass = db.getPass();
                String dbName = db.getDbName();

                String mysqlPath = "D:\\XAMPP\\mysql\\bin\\mysql.exe"; 

                // Lệnh Restore cũng cần thêm Port 3307
                String[] executeCmd = new String[]{"cmd.exe", "/c", 
                    "\"" + mysqlPath + "\" -P 3307 -u" + user + (pass.isEmpty() ? "" : " -p" + pass) 
                    + " " + dbName + " < \"" + path + "\""};

                Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                int processComplete = runtimeProcess.waitFor();

                if (processComplete == 0) {
                    JOptionPane.showMessageDialog(this, "✅ Phục hồi dữ liệu thành công!\nHãy khởi động lại phần mềm.");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Phục hồi thất bại!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
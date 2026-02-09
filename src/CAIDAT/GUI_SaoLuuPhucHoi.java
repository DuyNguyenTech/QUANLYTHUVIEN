package CAIDAT;

import CHUNG.DBConnect;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GUI_SaoLuuPhucHoi extends JPanel {
    
    private JButton btnBackup, btnRestore;
    private DBConnect db = new DBConnect();
    
    // Màu chủ đạo
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);

    public GUI_SaoLuuPhucHoi() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 0, 10, 0)
        ));
        
        JLabel lblTitle = new JLabel("SAO LƯU & PHỤC HỒI DỮ LIỆU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(mainColor);
        
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT (CENTER) ---
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(bgColor);
        
        // Container chứa 2 card
        JPanel pnlContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        pnlContainer.setBackground(bgColor);
        pnlContainer.setBorder(new EmptyBorder(0, 50, 0, 50)); // Padding 2 bên

        // --- CARD 1: BACKUP ---
        JPanel pnlBackup = createCard(
            "SAO LƯU DỮ LIỆU",
            "Tạo bản sao an toàn cho hệ thống.\nFile sẽ được lưu dưới dạng .sql",
            "⬇️", 
            new Color(40, 167, 69) // Màu xanh lá
        );
        btnBackup = createButton("THỰC HIỆN SAO LƯU", new Color(40, 167, 69));
        pnlBackup.add(btnBackup, BorderLayout.SOUTH);

        // --- CARD 2: RESTORE ---
        JPanel pnlRestore = createCard(
            "PHỤC HỒI DỮ LIỆU",
            "Khôi phục dữ liệu từ file .sql đã lưu.\nCẩn thận: Dữ liệu hiện tại sẽ bị ghi đè.",
            "⬆️", 
            new Color(255, 152, 0) // Màu cam
        );
        btnRestore = createButton("CHỌN FILE PHỤC HỒI", new Color(255, 152, 0));
        pnlRestore.add(btnRestore, BorderLayout.SOUTH);

        pnlContainer.add(pnlBackup);
        pnlContainer.add(pnlRestore);
        
        pnlCenter.add(pnlContainer);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. FOOTER NOTE ---
        JLabel lblNote = new JLabel("<html><center><i>Lưu ý: Chức năng yêu cầu máy tính đã cài đặt MySQL/XAMPP và cấu hình đúng đường dẫn trong mã nguồn.</i></center></html>", SwingConstants.CENTER);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblNote.setForeground(Color.GRAY);
        lblNote.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblNote, BorderLayout.SOUTH);

        // --- EVENTS (GIỮ NGUYÊN LOGIC CỦA ANH) ---
        btnBackup.addActionListener(e -> xuLyBackup());
        btnRestore.addActionListener(e -> xuLyRestore());
    }

    // --- HELPER UI ---
    private JPanel createCard(String title, String desc, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        // Icon
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcon.setForeground(color);
        
        // Title
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(color);
        lblTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Description
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setEditable(false);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setForeground(Color.GRAY);
        txtDesc.setBackground(Color.WHITE);
        txtDesc.setMargin(new Insets(0, 10, 20, 10)); // Căn lề
        
        // Panel nội dung trên
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(Color.WHITE);
        pnlTop.add(lblIcon, BorderLayout.NORTH);
        pnlTop.add(lblTitle, BorderLayout.CENTER);
        pnlTop.add(txtDesc, BorderLayout.SOUTH);
        
        card.add(pnlTop, BorderLayout.CENTER);
        return card;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(0, 50)); // Chiều cao nút
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- LOGIC XỬ LÝ (GIỮ NGUYÊN) ---

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

                String mysqldumpPath = "C:\\xampp\\mysql\\bin\\mysqldump.exe"; // [LƯU Ý] Sửa lại đường dẫn nếu cần
                File fileCheck = new File(mysqldumpPath);
                if (!fileCheck.exists()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy file mysqldump tại:\n" + mysqldumpPath);
                    return;
                }

                java.util.List<String> commands = new java.util.ArrayList<>();
                commands.add(mysqldumpPath);
                
                // commands.add("-P"); commands.add("3306"); // Bỏ comment nếu dùng port khác
                
                commands.add("-u" + user);
                if (!pass.isEmpty()) {
                    commands.add("-p" + pass);
                }

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

                File f = new File(savePath);
                if (f.exists() && f.length() > 0) {
                    JOptionPane.showMessageDialog(this, "SAO LƯU THÀNH CÔNG!\nFile: " + savePath);
                } else {
                    JOptionPane.showMessageDialog(this, "SAO LƯU THẤT BẠI! Lỗi:\n" + output.toString());
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

                String mysqlPath = "C:\\xampp\\mysql\\bin\\mysql.exe"; 

                // Lệnh Restore
                String[] executeCmd = new String[]{"cmd.exe", "/c", 
                    "\"" + mysqlPath + "\" -u" + user + (pass.isEmpty() ? "" : " -p" + pass) 
                    + " " + dbName + " < \"" + path + "\""};

                Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                int processComplete = runtimeProcess.waitFor();

                if (processComplete == 0) {
                    JOptionPane.showMessageDialog(this, "PHỤC HỒI DỮ LIỆU THÀNH CÔNG!\nHãy khởi động lại phần mềm.");
                } else {
                    JOptionPane.showMessageDialog(this, "PHỤC HỒI THẤT BẠI!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
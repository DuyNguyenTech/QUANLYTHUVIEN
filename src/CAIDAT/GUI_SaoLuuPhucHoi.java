package CAIDAT;

import com.formdev.flatlaf.FlatClientProperties;
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
    
    // Màu chủ đạo đồng bộ
    private Color mainColor = new Color(50, 115, 220); 
    private Color bgColor = new Color(245, 248, 253);
    private Color successColor = new Color(40, 167, 69);
    private Color warningColor = new Color(255, 152, 0);

    public GUI_SaoLuuPhucHoi() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(bgColor);

        // --- 1. HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        JLabel lblTitle = new JLabel("SAO LƯU & PHỤC HỒI DỮ LIỆU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(mainColor);
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT (CENTER) ---
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setBackground(bgColor);
        
        // Container chứa 2 card
        JPanel pnlContainer = new JPanel(new GridLayout(1, 2, 40, 0));
        pnlContainer.setOpaque(false);
        pnlContainer.setPreferredSize(new Dimension(900, 450));

        // --- CARD 1: BACKUP ---
        JPanel pnlBackup = createPremiumCard(
            "SAO LƯU DỮ LIỆU",
            "Tạo bản sao lưu an toàn cho toàn bộ cơ sở dữ liệu hệ thống. File sẽ được lưu dưới định dạng .sql để có thể khôi phục bất cứ lúc nào.",
            "📥", 
            successColor
        );
        btnBackup = createStyledButton("THỰC HIỆN SAO LƯU", successColor);
        pnlBackup.add(btnBackup, BorderLayout.SOUTH);

        // --- CARD 2: RESTORE ---
        JPanel pnlRestore = createPremiumCard(
            "PHỤC HỒI DỮ LIỆU",
            "Khôi phục dữ liệu từ bản sao lưu trước đó. \nLưu ý: Dữ liệu hiện tại sẽ bị ghi đè hoàn toàn bởi dữ liệu từ file sao lưu.",
            "📤", 
            warningColor
        );
        btnRestore = createStyledButton("CHỌN FILE PHỤC HỒI", warningColor);
        pnlRestore.add(btnRestore, BorderLayout.SOUTH);

        pnlContainer.add(pnlBackup);
        pnlContainer.add(pnlRestore);
        
        pnlCenter.add(pnlContainer);
        add(pnlCenter, BorderLayout.CENTER);

        // --- 3. FOOTER NOTE ---
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(bgColor);
        pnlFooter.setBorder(new EmptyBorder(10, 0, 30, 0));

        JLabel lblNote = new JLabel("<html><center>Chức năng yêu cầu MySQL Server đang hoạt động và cấu hình đúng đường dẫn thực thi trong hệ thống.</center></html>", SwingConstants.CENTER);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblNote.setForeground(new Color(120, 120, 120));
        pnlFooter.add(lblNote, BorderLayout.CENTER);
        
        add(pnlFooter, BorderLayout.SOUTH);

        // --- EVENTS ---
        btnBackup.addActionListener(e -> xuLyBackup());
        btnRestore.addActionListener(e -> xuLyRestore());
    }

    // --- HELPER UI: TẠO CARD PREMIUM ---
    private JPanel createPremiumCard(String title, String desc, String icon, Color themeColor) {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 25; border: 1,1,1,1, #E0E0E0");
        card.setBorder(new EmptyBorder(40, 35, 40, 35));
        
        // Icon Section
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 70));
        lblIcon.setForeground(themeColor);
        
        // Text Section
        JPanel pnlText = new JPanel(new BorderLayout(0, 10));
        pnlText.setOpaque(false);
        
        JLabel lblT = new JLabel(title, SwingConstants.CENTER);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblT.setForeground(themeColor);
        
        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtDesc.setForeground(new Color(100, 100, 100));
        txtDesc.setOpaque(false);
        txtDesc.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        
        // Căn giữa text trong JTextArea thủ công
        txtDesc.setAlignmentX(CENTER_ALIGNMENT);
        
        pnlText.add(lblT, BorderLayout.NORTH);
        pnlText.add(txtDesc, BorderLayout.CENTER);
        
        card.add(lblIcon, BorderLayout.NORTH);
        card.add(pnlText, BorderLayout.CENTER);
        
        return card;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(0, 52));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 12; borderWidth: 0");
        return btn;
    }

    // --- LOGIC XỬ LÝ (GIỮ NGUYÊN GỐC) ---
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

                String mysqldumpPath = "C:\\xampp\\mysql\\bin\\mysqldump.exe"; 
                File fileCheck = new File(mysqldumpPath);
                if (!fileCheck.exists()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy file mysqldump tại:\n" + mysqldumpPath, "Lỗi cấu hình", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.util.List<String> commands = new java.util.ArrayList<>();
                commands.add(mysqldumpPath);
                commands.add("-u" + user);
                if (!pass.isEmpty()) commands.add("-p" + pass);
                commands.add("--databases");
                commands.add(dbName);
                commands.add("-r");
                commands.add(savePath);

                ProcessBuilder pb = new ProcessBuilder(commands);
                pb.redirectErrorStream(true); 
                Process process = pb.start();
                
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) output.append(line).append("\n");
                
                process.waitFor();

                File f = new File(savePath);
                if (f.exists() && f.length() > 0) {
                    JOptionPane.showMessageDialog(this, "✅ SAO LƯU THÀNH CÔNG!\nFile: " + fileName, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "❌ SAO LƯU THẤT BẠI! Lỗi:\n" + output.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
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

                String[] executeCmd = new String[]{"cmd.exe", "/c", 
                    "\"" + mysqlPath + "\" -u" + user + (pass.isEmpty() ? "" : " -p" + pass) 
                    + " " + dbName + " < \"" + path + "\""};

                if (JOptionPane.showConfirmDialog(this, "Cảnh báo: Dữ liệu hiện tại sẽ bị xóa hoàn toàn để thay thế bằng bản sao lưu.\nBạn có chắc chắn muốn tiếp tục?", "Xác nhận phục hồi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                    Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                    int processComplete = runtimeProcess.waitFor();

                    if (processComplete == 0) {
                        JOptionPane.showMessageDialog(this, "PHỤC HỒI DỮ LIỆU THÀNH CÔNG!\nHãy khởi động lại phần mềm để cập nhật thay đổi.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "PHỤC HỒI THẤT BẠI!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi phục hồi: " + ex.getMessage());
        }
    }
}
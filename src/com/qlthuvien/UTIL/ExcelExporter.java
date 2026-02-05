package com.qlthuvien.UTIL;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.awt.Desktop;

public class ExcelExporter {
    public void exportTable(JTable table) {
        // Khởi tạo JFileChooser để chọn nơi lưu
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        
        // Hiển thị hộp thoại lưu
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Tự động thêm đuôi .xls nếu người dùng quên nhập
            if (!fileToSave.getAbsolutePath().endsWith(".xls")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xls");
            }

            try {
                // Dùng UTF-16LE và BOM để Excel nhận diện đúng tiếng Việt
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-16LE"));
                bw.write("\uFEFF"); // Byte Order Mark (BOM)

                TableModel model = table.getModel();

                // 1. Ghi Tiêu Đề Cột
                for (int i = 0; i < model.getColumnCount(); i++) {
                    bw.write(model.getColumnName(i));
                    bw.write("\t"); // Dùng phím Tab để ngăn cách các cột
                }
                bw.newLine();

                // 2. Ghi Dữ Liệu Dòng
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        String str = (value == null) ? "" : value.toString();
                        
                        // Xử lý xuống dòng thành khoảng trắng để tránh vỡ format Excel
                        str = str.replace("\n", " ").replace("\r", " ");
                        
                        bw.write(str);
                        bw.write("\t");
                    }
                    bw.newLine();
                }

                bw.close();
                
                // Thông báo thành công và hỏi mở file
                int option = JOptionPane.showConfirmDialog(null, 
                    "Xuất file thành công!\nĐường dẫn: " + fileToSave.getAbsolutePath() + "\n\nBạn có muốn mở file ngay không?",
                    "Thông báo", JOptionPane.YES_NO_OPTION);
                
                // Mở file nếu người dùng chọn Yes
                if (option == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToSave);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi xuất file: " + ex.getMessage());
            }
        }
    }
}
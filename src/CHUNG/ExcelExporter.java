package CHUNG;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.awt.Desktop;

public class ExcelExporter {
    public void exportTable(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file báo cáo");
        
        // Mặc định định dạng CSV để Excel không chặn bảo mật
        fileChooser.setSelectedFile(new File("BaoCao_DuLieu.csv"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Đảm bảo đuôi file là .csv
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            // Dùng try-with-resources để tự động đóng file, tránh treo tiến trình
            try (OutputStream os = new FileOutputStream(fileToSave);
                 OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(osw)) {
                
                // Ghi mã BOM để Excel mở lên không bị lỗi font tiếng Việt
                bw.write('\ufeff');

                TableModel model = table.getModel();
                int cols = model.getColumnCount();
                int rows = model.getRowCount();

                // 1. Ghi tiêu đề
                for (int i = 0; i < cols; i++) {
                    bw.write(cleanData(model.getColumnName(i)));
                    if (i < cols - 1) bw.write(",");
                }
                bw.newLine();

                // 2. Ghi nội dung
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        Object val = model.getValueAt(i, j);
                        bw.write(cleanData(val));
                        if (j < cols - 1) bw.write(",");
                    }
                    bw.newLine();
                }

                bw.flush();
                JOptionPane.showMessageDialog(null, "Xuất file thành công!");
                
                // Mở file ngay sau khi ghi
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToSave);
                }

            } catch (FileNotFoundException e) {
                // Lỗi này 99% là do bạn đang mở file đó bằng Excel
                JOptionPane.showMessageDialog(null, "LỖI: File đang được mở bởi ứng dụng khác!\nHãy đóng file Excel và thử lại.");
            } catch (Exception ex) {
                // In ra chi tiết lỗi để debug nếu vẫn hỏng
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi hệ thống: " + ex.getMessage());
            }
        }
    }

    // Hàm xử lý dữ liệu tránh vỡ định dạng CSV
    private String cleanData(Object value) {
        if (value == null) return "";
        String s = value.toString();
        // CSV dùng dấu phẩy để ngăn cột, nên nếu dữ liệu có dấu phẩy ta đổi thành gạch ngang
        return s.replace(",", " - ").replace("\n", " ").replace("\r", "");
    }
}


//package CHUNG;
//
//import javax.swing.*;
//import javax.swing.table.TableModel;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.OutputStreamWriter;
//import java.awt.Desktop;
//
//public class ExcelExporter {
//    public void exportTable(JTable table) {
//        // Khởi tạo JFileChooser để chọn nơi lưu
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
//        
//        // Hiển thị hộp thoại lưu
//        int userSelection = fileChooser.showSaveDialog(null);
//
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File fileToSave = fileChooser.getSelectedFile();
//            
//            // Tự động thêm đuôi .xls nếu người dùng quên nhập
//            if (!fileToSave.getAbsolutePath().endsWith(".xls")) {
//                fileToSave = new File(fileToSave.getAbsolutePath() + ".xls");
//            }
//
//            try {
//                // Dùng UTF-16LE và BOM để Excel nhận diện đúng tiếng Việt
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-16LE"));
//                bw.write("\uFEFF"); // Byte Order Mark (BOM)
//
//                TableModel model = table.getModel();
//
//                // 1. Ghi Tiêu Đề Cột
//                for (int i = 0; i < model.getColumnCount(); i++) {
//                    bw.write(model.getColumnName(i));
//                    bw.write("\t"); // Dùng phím Tab để ngăn cách các cột
//                }
//                bw.newLine();
//
//                // 2. Ghi Dữ Liệu Dòng
//                for (int i = 0; i < model.getRowCount(); i++) {
//                    for (int j = 0; j < model.getColumnCount(); j++) {
//                        Object value = model.getValueAt(i, j);
//                        String str = (value == null) ? "" : value.toString();
//                        
//                        // Xử lý xuống dòng thành khoảng trắng để tránh vỡ format Excel
//                        str = str.replace("\n", " ").replace("\r", " ");
//                        
//                        bw.write(str);
//                        bw.write("\t");
//                    }
//                    bw.newLine();
//                }
//
//                bw.close();
//                
//                // Thông báo thành công và hỏi mở file
//                int option = JOptionPane.showConfirmDialog(null, 
//                    "Xuất file thành công!\nĐường dẫn: " + fileToSave.getAbsolutePath() + "\n\nBạn có muốn mở file ngay không?",
//                    "Thông báo", JOptionPane.YES_NO_OPTION);
//                
//                // Mở file nếu người dùng chọn Yes
//                if (option == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
//                    Desktop.getDesktop().open(fileToSave);
//                }
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(null, "Lỗi khi xuất file: " + ex.getMessage());
//            }
//        }
//    }
//}
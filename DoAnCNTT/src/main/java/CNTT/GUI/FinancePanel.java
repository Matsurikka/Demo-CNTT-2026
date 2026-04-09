package CNTT.GUI;

import CNTT.BUS.FinanceBUS;
import CNTT.DTO.FinanceDTO;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class FinancePanel extends JPanel {
    private JTextArea txtRule;
    private JTextField txtDesc, txtAmount;
    private JComboBox<String> cbType;
    private JTable table;
    private DefaultTableModel model;
    private final FinanceBUS bus;

    public FinancePanel(FinanceBUS bus) {
        this.bus = bus;
        setLayout(new BorderLayout());
        initComponents();
        loadData();
    }

    private void initComponents() {
        // --- 1. Vùng chứa chính ---
        JPanel pnlMain = new JPanel(new BorderLayout(20, 0));
        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 2. EDITOR LUẬT (BÊN TRÁI) ---
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        txtRule = new JTextArea();
        txtRule.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtRule.setTabSize(4);
        
        String defaultRules = "package rules;\n" +
            "import CNTT.DTO.FinanceDTO;\n\n" +

            "rule \"Auto Approve Revenue\"\n" +
            "when\n" +
            "    $f : FinanceDTO(type matches \"(?i)THU\")\n" +
            "then\n" +
            "    $f.setStatus(\"HOÀN THÀNH\");\n" +
            "    $f.setNote(\"Thu tiền - Duyệt tự động.\");\n" +
            "end\n\n" +

            "rule \"Critical Expense\"\n" +
            "when\n" +
            "    $f : FinanceDTO(type matches \"(?i)CHI\", amount >= 50000000)\n" +
            "then\n" +
            "    $f.setStatus(\"KHẨN CẤP, CẦN ĐƯỢC PHÊ DUYỆT\");\n" +
            "    $f.setNote(\"Cần CEO phê duyệt trực tiếp.\");\n" +
            "end\n\n" +

            "rule \"Quick Approve Ops\"\n" +
            "when\n" +
            "    $f : FinanceDTO(type matches \"(?i)CHI\", amount < 5000000, \n" +
            "                  description matches \"(?iu).*(dien|nuoc|van phong|internet).*\")\n" +
            "then\n" +
            "    $f.setStatus(\"ĐÃ DUYỆT\");\n" +
            "    $f.setNote(\"Phí vận hành định kỳ.\");\n" +
            "end";
        
        txtRule.setText(defaultRules);
        
        JButton btnDeploy = new JButton(" CẬP NHẬT TRI THỨC (DEPLOY)");
        btnDeploy.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeploy.setBackground(new Color(41, 128, 185));
        btnDeploy.setForeground(Color.WHITE);

        pnlLeft.add(new JLabel(" EDITOR LUẬT HỆ CHUYÊN GIA (DROOLS):"), BorderLayout.NORTH);
        pnlLeft.add(new JScrollPane(txtRule), BorderLayout.CENTER);
        pnlLeft.add(btnDeploy, BorderLayout.SOUTH);

        // --- 3. FORM NHẬP LIỆU (BÊN PHẢI) ---
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setPreferredSize(new Dimension(350, 0));
        pnlRight.setBorder(new TitledBorder(" NGHIỆP VỤ TÀI CHÍNH"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        txtDesc = new JTextField();
        txtAmount = new JTextField();
        cbType = new JComboBox<>(new String[]{"THU", "CHI"});
        JButton btnRun = new JButton(" LƯU & KIỂM TRA");
        btnRun.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRun.setBackground(new Color(39, 174, 96));
        btnRun.setForeground(Color.WHITE);

        gbc.gridy = 0; pnlRight.add(new JLabel("Mô tả:"), gbc);
        gbc.gridy = 1; pnlRight.add(txtDesc, gbc);
        gbc.gridy = 2; pnlRight.add(new JLabel("Số tiền (VNĐ):"), gbc);
        gbc.gridy = 3; pnlRight.add(txtAmount, gbc);
        gbc.gridy = 4; pnlRight.add(new JLabel("Loại:"), gbc);
        gbc.gridy = 5; pnlRight.add(cbType, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(25, 8, 8, 8); pnlRight.add(btnRun, gbc);

        pnlMain.add(pnlLeft, BorderLayout.CENTER);
        pnlMain.add(pnlRight, BorderLayout.EAST);

        // --- 4. BẢNG DỮ LIỆU ---
        model = new DefaultTableModel(new String[]{"ID", "Nội dung", "Loại", "Số tiền", "Trạng thái", "Ghi chú"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                java.awt.Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                String val = (v != null) ? v.toString() : "";
                
                if (val.equals("KHẨN CẤP, CẦN ĐƯỢC PHÊ DUYỆT")) {
                    comp.setForeground(Color.RED);
                } else if (val.equals("ĐÃ DUYỆT") || val.equals("HOÀN THÀNH")) {
                    comp.setForeground(new Color(39, 174, 96));
                } else {
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });

        add(pnlMain, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 5. SỰ KIỆN ---
        btnDeploy.addActionListener(e -> {
            try {
                bus.reloadRules(txtRule.getText());
                JOptionPane.showMessageDialog(this, "Nạp luật thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi luật: " + ex.getMessage());
            }
        });

        btnRun.addActionListener(e -> {
            try {
                FinanceDTO f = new FinanceDTO();
                
                // Thêm .trim() để xóa dấu cách thừa ở đầu/cuối
                String description = txtDesc.getText().trim();
                String type = cbType.getSelectedItem().toString().trim();
                
                f.setDescription(description);
                f.setAmount(Double.parseDouble(txtAmount.getText().trim()));
                f.setType(type);
                f.setStatus("CHỜ DUYỆT");

                // Gửi vào BUS để Drools xử lý
                bus.solve(f); 
                
                loadData(); // Load lại bảng để thấy kết quả thay đổi
                
                txtDesc.setText("");
                txtAmount.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        List<FinanceDTO> list = bus.getAll();
        for (FinanceDTO f : list) {
            model.addRow(new Object[]{
                f.getId(), 
                f.getDescription(), 
                f.getType(), 
                String.format("%,.0f", f.getAmount()), 
                f.getStatus(), 
                f.getNote()
            });
        }
    }
}
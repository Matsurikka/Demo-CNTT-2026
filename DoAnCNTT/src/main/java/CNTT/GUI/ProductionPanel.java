package CNTT.GUI;

import CNTT.BUS.ProductionBUS;
import CNTT.DTO.ProductionDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Giữ lại cái này cho UI
import java.awt.*;

// Dùng đường dẫn đầy đủ để tránh xung đột với java.awt.Component
@org.springframework.stereotype.Component 
public class ProductionPanel extends JPanel {
    private JTextArea txtRule;
    private JTextField txtProduct, txtQty, txtDefects;
    private JTable table;
    private DefaultTableModel model;
    private final ProductionBUS bus;

    public ProductionPanel(ProductionBUS bus) {
        this.bus = bus;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        createUI();
        loadData();
    }

    private void createUI() {
        // --- PHẦN TRÊN: EDITOR & INPUT ---
        JPanel pnlTop = new JPanel(new BorderLayout(20, 0));
        pnlTop.setOpaque(false);

        // 1. Bên trái: Editor Luật
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), " CẤU HÌNH LUẬT CHUYÊN GIA (DROOLS) ", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)));

        txtRule = new JTextArea();
        txtRule.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtRule.setText("package rules;\n" +
            "import CNTT.DTO.ProductionDTO;\n\n" +

            "rule \"Calculate Error Rate\"\n" +
            "salience 100\n" +
            "no-loop true\n" +
            "when\n" +
            "  $p : ProductionDTO(quantity > 0)\n" +
            "then\n" +
            "  modify($p) { setErrorRate(($p.getDefects() * 1.0 / $p.getQuantity()) * 100) }\n" +
            "end\n\n" +

            "rule \"Quality - Passed\"\n" +
            "salience 50\n" +
            "when\n" +
            "  $p : ProductionDTO(errorRate <= 3.0, status == null)\n" +
            "then\n" +
            "  modify($p) { setStatus(\"DAT CHUAN (PASSED)\") }\n" +
            "end\n\n" +

            "rule \"Quality - Warning\"\n" +
            "salience 50\n" +
            "when\n" +
            "  // Xu ly khoang tu 3% den 8%\n" +
            "  $p : ProductionDTO(errorRate > 3.0, errorRate <= 8.0, status == null)\n" +
            "then\n" +
            "  modify($p) { setStatus(\"CHO DUOC XET DUYET\") }\n" +
            "end\n\n" +

            "rule \"Quality - Rejected\"\n" +
            "salience 50\n" +
            "when\n" +
            "  $p : ProductionDTO(errorRate > 8.0, status == null)\n" +
            "then\n" +
            "  modify($p) { setStatus(\"HUY LO HANG (REJECTED)\") }\n" +
            "end");
        
        JScrollPane scrollRule = new JScrollPane(txtRule);
        scrollRule.setPreferredSize(new Dimension(500, 250));
        
        JButton btnDeploy = new JButton(" CAP NHAT TRI THUC (DEPLOY)");
        btnDeploy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDeploy.setBackground(new Color(41, 128, 185));
        btnDeploy.setForeground(Color.WHITE);
        btnDeploy.setFocusPainted(false);

        pnlLeft.add(scrollRule, BorderLayout.CENTER);
        pnlLeft.add(btnDeploy, BorderLayout.SOUTH);

        // 2. Bên phải: Form nhập liệu
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), " THÔNG TIN LÔ HÀNG ", 
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)));
        pnlRight.setPreferredSize(new Dimension(350, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.weightx = 1.0;

        txtProduct = new JTextField();
        txtQty = new JTextField();
        txtDefects = new JTextField();
        JButton btnRun = new JButton(" KIỂM TRA CHẤT LƯỢNG");
        btnRun.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRun.setBackground(new Color(39, 174, 96));
        btnRun.setForeground(Color.WHITE);
        btnRun.setPreferredSize(new Dimension(0, 40));

        gbc.gridy = 0; pnlRight.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridy = 1; pnlRight.add(txtProduct, gbc);
        gbc.gridy = 2; pnlRight.add(new JLabel("Tổng số lượng:"), gbc);
        gbc.gridy = 3; pnlRight.add(txtQty, gbc);
        gbc.gridy = 4; pnlRight.add(new JLabel("Số sản phẩm lỗi:"), gbc);
        gbc.gridy = 5; pnlRight.add(txtDefects, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(20, 15, 10, 15); pnlRight.add(btnRun, gbc);

        pnlTop.add(pnlLeft, BorderLayout.CENTER);
        pnlTop.add(pnlRight, BorderLayout.EAST);

        // --- PHẦN DƯỚI: BẢNG DỮ LIỆU ---
        model = new DefaultTableModel(new String[]{"ID", "Tên sản phẩm", "Số lượng", "Lỗi", "Tỉ lệ (%)", "Kết quả chuyên gia"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // RENDERER ĐÃ ĐƯỢC FIX LỖI TYPE
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                // Gọi hàm của cha để lấy đối tượng java.awt.Component
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                String val = (v != null) ? v.toString() : "";
                
                if (isS) {
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    if (val.contains("REJECTED")) {
                        comp.setForeground(new Color(192, 57, 43));
                    } else if (val.contains("PASSED")) {
                        comp.setForeground(new Color(39, 174, 96));
                    } else if (val.contains("WARNING")) {
                        comp.setForeground(new Color(230, 126, 34));
                    } else {
                        comp.setForeground(Color.BLACK);
                    }
                }

                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                return comp;
            }
        });

        // Căn giữa các cột số
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i : new int[]{2, 3, 4}) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        add(pnlTop, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- SỰ KIỆN ---
        btnDeploy.addActionListener(e -> {
            try { 
                bus.reloadRules(txtRule.getText()); 
                JOptionPane.showMessageDialog(this, "Hệ chuyên gia đã được cập nhật tri thức mới!"); 
            } catch(Exception ex) { 
                JOptionPane.showMessageDialog(this, "Lỗi cú pháp luật: " + ex.getMessage()); 
            }
        });

        btnRun.addActionListener(e -> {
            try {
                ProductionDTO p = new ProductionDTO();
                p.setProductName(txtProduct.getText());
                p.setQuantity(Integer.parseInt(txtQty.getText()));
                p.setDefects(Integer.parseInt(txtDefects.getText()));
                bus.solve(p);
                loadData();
                txtProduct.setText(""); txtQty.setText(""); txtDefects.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        });
    }

    private void loadData() {
        model.setRowCount(0);
        bus.getAll().forEach(p -> model.addRow(new Object[]{
                p.getId(), 
                p.getProductName(), 
                p.getQuantity(), 
                p.getDefects(), 
                String.format("%.2f %%", p.getErrorRate()), 
                p.getStatus()
        }));
    } 
}
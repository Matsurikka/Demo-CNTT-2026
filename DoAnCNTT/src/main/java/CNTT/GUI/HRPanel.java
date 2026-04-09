package CNTT.GUI;

import CNTT.BUS.EmployeeBUS;
import CNTT.DTO.EmployeeDTO;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class HRPanel extends JPanel {
    private JTextArea txtRule;
    private JTextField txtName, txtExp, txtSal, txtAbsent;
    private JLabel lblRes;
    private JTable tblEmployee;
    private DefaultTableModel tableModel;
    private final EmployeeBUS bus;

    public HRPanel(EmployeeBUS bus) {
        this.bus = bus;
        setLayout(new BorderLayout()); // JPanel sử dụng layout này
        createUI();
        loadTableData();
    }

    private void createUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.tabHeight", 35);

        // --- TAB 1: XỬ LÝ QUY TẮC ---
        JPanel pnlMain = new JPanel(new BorderLayout(20, 0));
        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Editor Rules (Bên trái)
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.add(new JLabel("Định nghĩa luật nhân sự (DRL):", JLabel.LEFT), BorderLayout.NORTH);
        
        txtRule = new JTextArea();
        txtRule.setText("package rules;\nimport CNTT.DTO.EmployeeDTO;\n\n" +
                "rule \"Calculate Fine\"\nsalience 100\nwhen\n  $e : EmployeeDTO(absentDays > 3)\nthen\n" +
                "  $e.setFine(($e.getAbsentDays() - 3) * 100000);\nend\n\n" +
                "rule \"Rank - Expert\"\nsalience 80\nwhen\n  $e : EmployeeDTO(experience >= 5)\nthen\n" +
                "  $e.setRank(\"Chuyên gia\");\n  $e.setBonus($e.getSalary() * 0.2);\nend");
        
        txtRule.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scrollEditor = new JScrollPane(txtRule);
        pnlLeft.add(scrollEditor, BorderLayout.CENTER);

        JButton btnDeploy = new JButton("NẠP TRI THỨC NHÂN SỰ");
        btnDeploy.setBackground(new Color(63, 81, 181));
        btnDeploy.setForeground(Color.WHITE);
        pnlLeft.add(btnDeploy, BorderLayout.SOUTH);

        // Form nhập liệu (Bên phải)
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setPreferredSize(new Dimension(350, 0));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Kiểm thử nhân viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(8, 15, 8, 15); gbc.weightx = 1.0;

        txtName = new JTextField("Quốc Huy");
        txtExp = new JTextField("6");
        txtSal = new JTextField("15000000");
        txtAbsent = new JTextField("0");

        int row = 0;
        addFormField(pnlRight, "Tên nhân viên:", txtName, gbc, row++);
        addFormField(pnlRight, "Kinh nghiệm (năm):", txtExp, gbc, row++);
        addFormField(pnlRight, "Lương cơ bản:", txtSal, gbc, row++);
        addFormField(pnlRight, "Ngày vắng:", txtAbsent, gbc, row++);

        JButton btnRun = new JButton("CHẠY SUY LUẬN");
        btnRun.setBackground(new Color(76, 175, 80)); btnRun.setForeground(Color.WHITE);
        gbc.gridy = row++; pnlRight.add(btnRun, gbc);

        lblRes = new JLabel("Kết quả: Sẵn sàng", JLabel.CENTER);
        lblRes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = row++; pnlRight.add(lblRes, gbc);

        pnlMain.add(pnlLeft, BorderLayout.CENTER);
        pnlMain.add(pnlRight, BorderLayout.EAST);

        // --- TAB 2: DANH SÁCH ---
        JPanel pnlData = new JPanel(new BorderLayout(15, 15));
        pnlData.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Họ Tên", "Kinh Nghiệm", "Lương", "Vắng", "Thưởng", "Phạt", "Xếp Hạng"};
        tableModel = new DefaultTableModel(columns, 0);
        tblEmployee = new JTable(tableModel);
        tblEmployee.setRowHeight(30);
        
        pnlData.add(new JScrollPane(tblEmployee), BorderLayout.CENTER);
        JButton btnReload = new JButton("LÀM MỚI");
        pnlData.add(btnReload, BorderLayout.SOUTH);

        tabbedPane.addTab(" Xử lý hệ chuyên gia", pnlMain);
        tabbedPane.addTab(" Danh sách thực thể", pnlData);
        add(tabbedPane, BorderLayout.CENTER);

        // Events
        btnDeploy.addActionListener(e -> deployRules());
        btnRun.addActionListener(e -> runInference());
        btnReload.addActionListener(e -> loadTableData());
    }

    private void addFormField(JPanel pnl, String label, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridy = row * 2; pnl.add(new JLabel(label), gbc);
        gbc.gridy = row * 2 + 1; pnl.add(field, gbc);
    }

    // Các hàm xử lý
    private void deployRules() {
        try {
            bus.reloadRules(txtRule.getText());
            JOptionPane.showMessageDialog(this, "Nạp thành công tri thức!");
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void runInference() {
        try {
            EmployeeDTO emp = new EmployeeDTO();
            emp.setName(txtName.getText());
            emp.setExperience(Integer.parseInt(txtExp.getText()));
            emp.setSalary(Double.parseDouble(txtSal.getText()));
            emp.setAbsentDays(Integer.parseInt(txtAbsent.getText())); 

            EmployeeDTO result = bus.solve(emp);
            lblRes.setText("Hạng: " + result.getRank() + " | Phạt: " + String.format("%,.0f", result.getFine()));
            loadTableData();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
    }

    private void loadTableData() {
        List<EmployeeDTO> list = bus.getAllEmployees();
        tableModel.setRowCount(0);
        for (EmployeeDTO e : list) {
            tableModel.addRow(new Object[]{
                e.getId(), e.getName(), e.getExperience() + " năm",
                String.format("%,.0f", e.getSalary()), e.getAbsentDays() + " ngày",
                String.format("%,.0f", e.getBonus()), String.format("%,.0f", e.getFine()), e.getRank()
            });
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(msg)), "Lỗi DRL", JOptionPane.ERROR_MESSAGE);
    }
}
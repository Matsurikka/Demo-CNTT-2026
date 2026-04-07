package CNTT.GUI;

import CNTT.BUS.EmployeeBUS;
import CNTT.DTO.EmployeeDTO;
import com.formdev.flatlaf.FlatIntelliJLaf; // Import FlatLaf
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class ExpertSystemGUI extends JFrame {
    private JTextArea txtRule;
    private JTextField txtName, txtExp, txtSal;
    private JLabel lblRes;
    private JTable tblEmployee;
    private DefaultTableModel tableModel;
    private final EmployeeBUS bus;

    public ExpertSystemGUI(EmployeeBUS bus) {
        this.bus = bus;
        setupLaf(); // Thiết lập giao diện hiện đại
        createUI();
        loadTableData();
    }

    private void setupLaf() {
        try {
            // Sử dụng giao diện phẳng hiện đại
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            // Tùy chỉnh font hệ thống cho đồng bộ
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
    }

    private void createUI() {
        setTitle("HR EXPERT SYSTEM - KMS");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.tabHeight", 35); // Làm tab cao hơn

        // --- TAB 1: XỬ LÝ QUY TẮC ---
        JPanel pnlMain = new JPanel(new BorderLayout(20, 0));
        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Editor Rules (Bên trái)
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.add(new JLabel("Định nghĩa luật (DRL Syntax):", JLabel.LEFT), BorderLayout.NORTH);
        
        txtRule = new JTextArea();
        txtRule.setText("package rules;\nimport CNTT.DTO.EmployeeDTO;\n\nrule \"Policy 1\"\nwhen\n    $e : EmployeeDTO(experience >= 5)\nthen\n    $e.setBonus($e.getSalary() * 0.2);\n    $e.setRank(\"Chuyên gia\");\nend");
        txtRule.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scrollEditor = new JScrollPane(txtRule);
        scrollEditor.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        pnlLeft.add(scrollEditor, BorderLayout.CENTER);

        JButton btnDeploy = new JButton("DEPLOY RULES");
        btnDeploy.setBackground(new Color(63, 81, 181)); // Màu xanh Indigo
        btnDeploy.setForeground(Color.WHITE);
        btnDeploy.setFocusPainted(false);
        pnlLeft.add(btnDeploy, BorderLayout.SOUTH);

        // Input Form (Bên phải)
        JPanel pnlRight = new JPanel(new GridBagLayout());
        pnlRight.setBorder(BorderFactory.createTitledBorder("Nhập thông tin kiểm thử"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.weightx = 1.0;

        // Tên
        gbc.gridx = 0; gbc.gridy = 0; pnlRight.add(new JLabel("Tên nhân viên:"), gbc);
        txtName = new JTextField("Quốc Huy");
        gbc.gridy = 1; pnlRight.add(txtName, gbc);

        // Kinh nghiệm
        gbc.gridy = 2; pnlRight.add(new JLabel("Kinh nghiệm (năm):"), gbc);
        txtExp = new JTextField("6");
        gbc.gridy = 3; pnlRight.add(txtExp, gbc);

        // Lương
        gbc.gridy = 4; pnlRight.add(new JLabel("Lương cơ bản (VNĐ):"), gbc);
        txtSal = new JTextField("15000000");
        gbc.gridy = 5; pnlRight.add(txtSal, gbc);

        // Nút chạy
        JButton btnRun = new JButton("CHẠY SUY LUẬN");
        btnRun.setBackground(new Color(76, 175, 80)); // Màu xanh lá
        btnRun.setForeground(Color.WHITE);
        gbc.gridy = 6; gbc.insets = new Insets(20, 15, 10, 15);
        pnlRight.add(btnRun, gbc);

        // Kết quả
        lblRes = new JLabel("Kết quả: Sẵn sàng", JLabel.CENTER);
        lblRes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRes.setForeground(new Color(33, 150, 243));
        gbc.gridy = 7; pnlRight.add(lblRes, gbc);

        pnlMain.add(pnlLeft, BorderLayout.CENTER);
        pnlMain.add(pnlRight, BorderLayout.EAST);
        pnlRight.setPreferredSize(new Dimension(350, 0));

        // --- TAB 2: DỮ LIỆU NHÂN VIÊN ---
        JPanel pnlData = new JPanel(new BorderLayout(15, 15));
        pnlData.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columns = {"ID", "Họ Tên", "Kinh Nghiệm", "Lương", "Thưởng", "Xếp Hạng"};
        tableModel = new DefaultTableModel(columns, 0);
        tblEmployee = new JTable(tableModel);
        tblEmployee.setRowHeight(30); // Dòng cao hơn cho thoáng
        tblEmployee.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Căn giữa nội dung bảng
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblEmployee.setDefaultRenderer(Object.class, centerRenderer);

        pnlData.add(new JScrollPane(tblEmployee), BorderLayout.CENTER);

        JButton btnReload = new JButton("LÀM MỚI DANH SÁCH");
        pnlData.add(btnReload, BorderLayout.SOUTH);

        tabbedPane.addTab("🖥️ Xử lý hệ chuyên gia", pnlMain);
        tabbedPane.addTab("📊 Danh sách thực thể", pnlData);
        add(tabbedPane);

        // --- EVENTS ---
        btnDeploy.addActionListener(e -> deployRules());
        btnRun.addActionListener(e -> runInference());
        btnReload.addActionListener(e -> loadTableData());
    }

    private void deployRules() {
        try {
            bus.reloadRules(txtRule.getText());
            JOptionPane.showMessageDialog(this, "Tri thức đã được nạp thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void runInference() {
        try {
            EmployeeDTO emp = new EmployeeDTO();
            emp.setName(txtName.getText());
            emp.setExperience(Integer.parseInt(txtExp.getText()));
            emp.setSalary(Double.parseDouble(txtSal.getText()));

            EmployeeDTO result = bus.solve(emp);
            lblRes.setText("Hạng: " + result.getRank() + " | Thưởng: " + String.format("%,.0f", result.getBonus()));
            loadTableData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void loadTableData() {
        List<EmployeeDTO> list = bus.getAllEmployees();
        tableModel.setRowCount(0);
        for (EmployeeDTO e : list) {
            tableModel.addRow(new Object[]{
                    e.getId(), e.getName(), e.getExperience() + " năm",
                    String.format("%,.0f", e.getSalary()),
                    String.format("%,.0f", e.getBonus()),
                    e.getRank()
            });
        }
    }

    private void showError(String msg) {
        JTextArea area = new JTextArea(msg);
        area.setRows(10);
        area.setColumns(40);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Lỗi biên dịch DRL", JOptionPane.ERROR_MESSAGE);
    }
}
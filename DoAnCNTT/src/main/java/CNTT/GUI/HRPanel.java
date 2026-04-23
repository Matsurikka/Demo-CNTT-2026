package CNTT.GUI;

import CNTT.BUS.EmployeeBUS;
import CNTT.DTO.EmployeeDTO;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        
        // Thiết lập Look and Feel hiện đại
        setupTheme();
        
        setLayout(new BorderLayout());
        createUI();
        loadTableData();
    }

    private void setupTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Tùy chỉnh màu sắc chủ đạo nhẹ nhàng hơn
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
    }

    private void createUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_HEIGHT, 45);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_ALIGNMENT, SwingConstants.LEADING);

        // --- TAB 1: EXPERT SYSTEM ---
        JPanel pnlExpert = new JPanel(new BorderLayout(25, 0));
        pnlExpert.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Phần soạn thảo DRL (Bên trái)
        JPanel pnlEditor = new JPanel(new BorderLayout(0, 15));
        JLabel lblEditorTitle = new JLabel("TRI THỨC HỆ CHUYÊN GIA (DRL)");
        lblEditorTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlEditor.add(lblEditorTitle, BorderLayout.NORTH);

        txtRule = new JTextArea();
        txtRule.setText("package rules;\nimport CNTT.DTO.EmployeeDTO;\n\n" +
                "rule \"Calculate Fine\"\nsalience 100\nwhen\n  $e : EmployeeDTO(absentDays > 3)\nthen\n" +
                "  $e.setFine(($e.getAbsentDays() - 3) * 100000);\nend\n\n" +
                "rule \"Rank - Expert\"\nsalience 80\nwhen\n  $e : EmployeeDTO(experience >= 5)\nthen\n" +
                "  $e.setRank(\"Chuyên gia\");\n  $e.setBonus($e.getSalary() * 0.2);\nend");
        
        txtRule.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        txtRule.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane scrollEditor = new JScrollPane(txtRule);
        scrollEditor.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1,#E0E0E0");
        pnlEditor.add(scrollEditor, BorderLayout.CENTER);

        JButton btnDeploy = new JButton("CẬP NHẬT CƠ SỞ TRI THỨC");
        btnDeploy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDeploy.putClientProperty(FlatClientProperties.STYLE, 
            "background: #3f51b5; foreground: #ffffff; hoverBackground: #303f9f; arc: 12; margin: 10,20,10,20");
        pnlEditor.add(btnDeploy, BorderLayout.SOUTH);

        // Phần Form Test (Bên phải)
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setPreferredSize(new Dimension(360, 0));
        pnlForm.putClientProperty(FlatClientProperties.STYLE, "background: #fdfdfd; arc: 20");
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 235, 235), 1),
            new EmptyBorder(25, 25, 25, 25)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 18, 0); gbc.weightx = 1.0;

        txtName = createStyledField("Tên nhân viên...");
        txtExp = createStyledField("Số năm kinh nghiệm");
        txtSal = createStyledField("Mức lương cơ bản");
        txtAbsent = createStyledField("Số ngày vắng");

        // Gán giá trị mặc định cho "Quốc Huy" như yêu cầu ban đầu
        txtName.setText("Quốc Huy"); txtExp.setText("6"); txtSal.setText("15000000"); txtAbsent.setText("0");

        int r = 0;
        addFormField(pnlForm, "Họ và tên", txtName, gbc, r++);
        addFormField(pnlForm, "Kinh nghiệm (năm)", txtExp, gbc, r++);
        addFormField(pnlForm, "Lương (VNĐ)", txtSal, gbc, r++);
        addFormField(pnlForm, "Ngày nghỉ", txtAbsent, gbc, r++);

        JButton btnRun = new JButton("CHẠY SUY LUẬN");
        btnRun.putClientProperty(FlatClientProperties.STYLE, "background: #2e7d32; foreground: #ffffff; arc: 12; margin: 12,0,12,0");
        gbc.gridy = r++ * 2; gbc.insets = new Insets(10, 0, 15, 0);
        pnlForm.add(btnRun, gbc);

        lblRes = new JLabel("Trạng thái: Sẵn sàng", JLabel.CENTER);
        lblRes.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gbc.gridy = r++ * 2; pnlForm.add(lblRes, gbc);

        pnlExpert.add(pnlEditor, BorderLayout.CENTER);
        pnlExpert.add(pnlForm, BorderLayout.EAST);

        // --- TAB 2: DATA LIST ---
        JPanel pnlData = new JPanel(new BorderLayout(0, 20));
        pnlData.setBorder(new EmptyBorder(25, 25, 25, 25));

        String[] cols = {"ID", "Nhân viên", "Kinh nghiệm", "Lương", "Vắng", "Thưởng", "Phạt", "Xếp hạng"};
        tableModel = new DefaultTableModel(cols, 0);
        tblEmployee = new JTable(tableModel);
        tblEmployee.setRowHeight(38);
        tblEmployee.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblEmployee.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollTable = new JScrollPane(tblEmployee);
        scrollTable.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1,#E0E0E0");
        pnlData.add(scrollTable, BorderLayout.CENTER);

        JButton btnReload = new JButton("LÀM MỚI DỮ LIỆU");
        btnReload.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,30,8,30");
        pnlData.add(btnReload, BorderLayout.SOUTH);

        tabbedPane.addTab("Cấu hình luật Expert", pnlExpert);
        tabbedPane.addTab("Danh sách nhân sự", pnlData);
        add(tabbedPane, BorderLayout.CENTER);

        // Event Listeners
        btnDeploy.addActionListener(e -> deployRules());
        btnRun.addActionListener(e -> runInference());
        btnReload.addActionListener(e -> loadTableData());
    }

    private JTextField createStyledField(String hint) {
        JTextField f = new JTextField();
        f.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
        f.putClientProperty(FlatClientProperties.STYLE, "margin: 6,10,6,10; focusWidth: 2; focusColor: #3f51b5");
        return f;
    }

    private void addFormField(JPanel pnl, String label, JTextField field, GridBagConstraints gbc, int row) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        l.setForeground(new Color(100, 100, 100));
        gbc.gridy = row * 2; gbc.insets = new Insets(0, 0, 4, 0);
        pnl.add(l, gbc);
        gbc.gridy = row * 2 + 1; gbc.insets = new Insets(0, 0, 15, 0);
        pnl.add(field, gbc);
    }

    private void deployRules() {
        try {
            bus.reloadRules(txtRule.getText());
            JOptionPane.showMessageDialog(this, "Đã nạp tri thức thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
            lblRes.setText("<html><center>Hạng: <b style='color:#1976D2'>" + result.getRank() + "</b><br>Phạt: " + String.format("%,.0f", result.getFine()) + " VNĐ</center></html>");
            loadTableData();
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu đầu vào: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void loadTableData() {
        List<EmployeeDTO> list = bus.getAllEmployees();
        tableModel.setRowCount(0);
        for (EmployeeDTO e : list) {
            tableModel.addRow(new Object[]{
                e.getId(), e.getName(), e.getExperience() + " năm",
                String.format("%,.0f", e.getSalary()), e.getAbsentDays(),
                String.format("%,.0f", e.getBonus()), String.format("%,.0f", e.getFine()), e.getRank()
            });
        }
    }

    private void showError(String msg) {
        JTextArea area = new JTextArea(msg);
        area.setRows(10); area.setColumns(40); area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Lỗi cú pháp DRL", JOptionPane.ERROR_MESSAGE);
    }
}
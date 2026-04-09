package CNTT.GUI;

import com.formdev.flatlaf.FlatIntelliJLaf;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class MainFrame extends JFrame {
    private JPanel pnlContent;
    private CardLayout cardLayout;

    public MainFrame(HRPanel hrPanel, FinancePanel financePanel, ProductionPanel productionPanel) { 
        setupLaf();
        setTitle("KMS - ENTERPRISE EXPERT SYSTEM (ERP v1.0)");
        setSize(1400, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 1. Sidebar ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setLayout(new BoxLayout(pnlSidebar, BoxLayout.Y_AXIS));
        pnlSidebar.setBackground(new Color(33, 37, 41));
        pnlSidebar.setPreferredSize(new Dimension(260, 0));

        // Header Logo
        JLabel lblBrand = new JLabel("KMS ENGINE", JLabel.CENTER);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setForeground(new Color(0, 120, 215));
        lblBrand.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // FIX: Gọi trực tiếp từ lớp Component để không bao giờ lỗi
        lblBrand.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT); 
        pnlSidebar.add(lblBrand);

        // --- 2. Content Area ---
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        
        pnlContent.add(hrPanel, "HR");
        pnlContent.add(financePanel, "FINANCE");
        pnlContent.add(productionPanel, "PRODUCTION");

        // --- 3. Menu Buttons ---
        pnlSidebar.add(createMenuBtn(" NHÂN SỰ (HR)", "HR"));
        pnlSidebar.add(createMenuBtn(" TÀI CHÍNH", "FINANCE"));
        pnlSidebar.add(createMenuBtn(" SẢN XUẤT", "PRODUCTION"));

        setLayout(new BorderLayout());
        add(pnlSidebar, BorderLayout.WEST);
        add(pnlContent, BorderLayout.CENTER);
    }

    private JButton createMenuBtn(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(260, 60));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(220, 220, 220));
        btn.setBackground(new Color(33, 37, 41));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // FIX: Căn giữa nút bấm trong BoxLayout
        btn.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT); 
        
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        btn.addActionListener(e -> cardLayout.show(pnlContent, cardName));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 52, 54));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 37, 41));
                btn.setForeground(new Color(220, 220, 220));
            }
        });
        
        return btn;
    }

    private void setupLaf() {
        try { 
            UIManager.setLookAndFeel(new FlatIntelliJLaf()); 
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
        } catch (Exception ignored) {}
    }
}
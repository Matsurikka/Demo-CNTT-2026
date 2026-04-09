package CNTT;

import CNTT.GUI.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import java.awt.EventQueue;

@SpringBootApplication
public class ExpertSystemApplication {
    public static void main(String[] args) {
        // Ép dùng trình biên dịch Eclipse để tránh lỗi Drools trên một số máy ảo Java
        System.setProperty("drools.dialect.java.compiler", "ECLIPSE");

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(ExpertSystemApplication.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            // Chạy khung giao diện chính tích hợp Menu bên trái
            MainFrame mainFrame = ctx.getBean(MainFrame.class);
            mainFrame.setVisible(true);
        });
    }
}
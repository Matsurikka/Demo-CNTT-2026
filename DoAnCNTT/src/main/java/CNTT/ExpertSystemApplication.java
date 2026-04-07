package CNTT;

import CNTT.GUI.ExpertSystemGUI;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import java.awt.EventQueue;

@SpringBootApplication
public class ExpertSystemApplication {
    public static void main(String[] args) {
        //Ép toàn bộ hệ thống (bao gồm Drools và MVEL) dùng trình biên dịch Eclipse
        System.setProperty("drools.dialect.java.compiler", "ECLIPSE");

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(ExpertSystemApplication.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            ExpertSystemGUI gui = ctx.getBean(ExpertSystemGUI.class);
            gui.setVisible(true);
        });
    }
}
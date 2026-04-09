package CNTT.BUS;

import CNTT.DAO.FinanceDAO;
import CNTT.DTO.FinanceDTO;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FinanceBUS {
    @Autowired private FinanceDAO dao;
    private KieContainer kieContainer;

    public void reloadRules(String drl) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        
        // Ghi vào đường dẫn ảo
        kfs.write("src/main/resources/rules/finance.drl", drl);
        
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Lỗi cú pháp luật: " + kb.getResults().toString());
        }
        
        // FIX: Quan trọng nhất là ClassLoader ở đây
        this.kieContainer = ks.newKieContainer(
            ks.getRepository().getDefaultReleaseId(), 
            this.getClass().getClassLoader()
        );
    }

    public FinanceDTO solve(FinanceDTO fin) {
        if (kieContainer == null) throw new RuntimeException("Chưa nạp luật tài chính!");
        
        KieSession session = kieContainer.newKieSession();
        try {
            session.insert(fin);
            int fired = session.fireAllRules();
            System.out.println("Số luật đã thực thi: " + fired); // In ra để bạn debug
        } finally { 
            session.dispose(); 
        }
        
        // Sau khi fireAllRules, các setter trong DRL đã thay đổi giá trị của 'fin'
        return dao.save(fin);
    }

    public List<FinanceDTO> getAll() { return dao.findAll(); }
}
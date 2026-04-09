package CNTT.BUS;

import CNTT.DAO.ProductionDAO;
import CNTT.DTO.ProductionDTO;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductionBUS {
    @Autowired private ProductionDAO dao;
    private KieContainer kieContainer;

    public void reloadRules(String drl) {
    KieServices ks = KieServices.Factory.get();
    KieFileSystem kfs = ks.newKieFileSystem();
    kfs.write("src/main/resources/rules/production.drl", drl);
    
    KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
    if (kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
        throw new RuntimeException("Lỗi cú pháp Rule: " + kb.getResults().toString());
    }
    
    // FIX: Thêm ClassLoader để Drools nhận diện DTO chính xác
    this.kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId(), 
                                           this.getClass().getClassLoader());
}

    public ProductionDTO solve(ProductionDTO pro) {
    if (kieContainer == null) throw new RuntimeException("Chưa nạp luật sản xuất!");
    KieSession session = kieContainer.newKieSession();
    try {
        session.insert(pro);
        session.fireAllRules();
    } finally {
        session.dispose();
    }
    return dao.save(pro);
}

    public List<ProductionDTO> getAll() { return dao.findAll(); }
}
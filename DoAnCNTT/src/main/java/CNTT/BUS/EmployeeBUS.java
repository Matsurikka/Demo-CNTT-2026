package CNTT.BUS;

import CNTT.DTO.EmployeeDTO;
import CNTT.DAO.EmployeeDAO;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeBUS {

    @Autowired
    private EmployeeDAO employeeDAO;
    
    private KieContainer kieContainer;

    /**
     * Nạp Rules động và kiểm tra lỗi cú pháp DRL
     * @param drlContent Nội dung quy tắc từ giao diện
     */
    public void reloadRules(String drlContent) {
        System.setProperty("drools.dialect.java.compiler", "ECLIPSE");
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        
        // Đường dẫn ảo để Drools nhận diện file rule
        String path = "src/main/resources/rules/temp.drl";
        kfs.write(path, drlContent);
        
        // Khởi tạo Builder để biên dịch nội dung DRL
        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        
        // Kiểm tra kết quả biên dịch (Inference Engine Validation)
        Results results = kb.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            List<Message> errors = results.getMessages(Message.Level.ERROR);
            StringBuilder sb = new StringBuilder("Hệ thống phát hiện lỗi trong Rule Base:\n");
            
            for (Message msg : errors) {
                sb.append(">> Dòng ").append(msg.getLine())
                  .append(": ").append(msg.getText()).append("\n");
            }
            
            // Ném ngoại lệ để GUI bắt được và hiển thị cho người dùng
            throw new RuntimeException(sb.toString());
        }

        // Nếu biên dịch thành công, cập nhật Container để sẵn sàng suy luận
        this.kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
    }

    /**
     * Thực hiện suy luận (Inference) và lưu kết quả vào Database
     * @param emp Đối tượng nhân viên cần xử lý
     * @return Đối tượng sau khi đã áp dụng các quy tắc và lưu xuống MySQL
     */
    public EmployeeDTO solve(EmployeeDTO emp) {
        if (kieContainer == null) {
            throw new RuntimeException("Hệ thống chưa có tri thức! Vui lòng nạp Rules trước.");
        }
        
        KieSession session = kieContainer.newKieSession();
        try {
            // Đưa dữ liệu vào Working Memory (Fact Base)
            session.insert(emp);
            
            // Kích hoạt động cơ suy luận (Fire all rules)
            session.fireAllRules(); 
            
        } finally {
            // Giải phóng bộ nhớ session sau khi suy luận xong
            session.dispose();
        }
        
        // Lưu xuống MySQL thông qua DAO (Spring Data JPA)
        return employeeDAO.save(emp);
    }
    public List<EmployeeDTO> getAllEmployees() {
        return employeeDAO.findAll();
    }
}
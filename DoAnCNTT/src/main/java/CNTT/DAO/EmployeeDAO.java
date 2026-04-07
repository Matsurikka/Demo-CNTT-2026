package CNTT.DAO;

import CNTT.DTO.EmployeeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDAO extends JpaRepository<EmployeeDTO, Long> {
    // Tự động xử lý lưu trữ vào MySQL XAMPP
}
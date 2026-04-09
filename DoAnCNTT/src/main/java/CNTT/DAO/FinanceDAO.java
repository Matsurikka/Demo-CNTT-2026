package CNTT.DAO;
import CNTT.DTO.FinanceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceDAO extends JpaRepository<FinanceDTO, Long> {}
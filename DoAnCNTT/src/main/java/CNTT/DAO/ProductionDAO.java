package CNTT.DAO;
import CNTT.DTO.ProductionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionDAO extends JpaRepository<ProductionDTO, Long> {}
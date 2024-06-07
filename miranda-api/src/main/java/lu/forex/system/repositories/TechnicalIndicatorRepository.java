package lu.forex.system.repositories;

import java.util.UUID;
import lu.forex.system.entities.TechnicalIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicalIndicatorRepository extends JpaRepository<TechnicalIndicator, UUID>, JpaSpecificationExecutor<TechnicalIndicator> {

}
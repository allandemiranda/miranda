package lu.forex.system.repositories;

import java.util.UUID;
import lu.forex.system.entities.MovingAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovingAverageRepository extends JpaRepository<MovingAverage, UUID>, JpaSpecificationExecutor<MovingAverage> {

}
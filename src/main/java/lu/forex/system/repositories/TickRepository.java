package lu.forex.system.repositories;

import java.util.UUID;
import lu.forex.system.model.Tick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TickRepository extends JpaRepository<Tick, UUID> {

}

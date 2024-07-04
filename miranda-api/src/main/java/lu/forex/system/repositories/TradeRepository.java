package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.UUID;
import lu.forex.system.entities.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, UUID>, JpaSpecificationExecutor<Trade> {

  @NotNull
  @Query("select t from Trade t where t.isActivate = ?5 and t.scope.id = ?1 and t.spreadMax >= ?2 and t.slotWeek = ?3 and ?4 between t.slotStart and t.slotEnd")
  Collection<Trade> findTradeToOpenOrder(@NonNull UUID scopeId, @NonNull int spread, @NonNull DayOfWeek week, @NonNull LocalTime time, boolean isActivate);

  Collection<Trade> findByScope_Symbol_Id(@NonNull UUID id);

  @Query("select t from Trade t where t.isActivate = ?1")
  Collection<Trade> findByIsActivate(@NonNull boolean isActivate);
}
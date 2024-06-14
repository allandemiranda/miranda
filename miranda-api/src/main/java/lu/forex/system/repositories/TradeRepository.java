package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lu.forex.system.entities.Scope;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, UUID>, JpaSpecificationExecutor<Trade> {

  @NotNull
  @Query("select t from Trade t where t.scope = ?1 and t.spreadMax >= ?2 and t.slotWeek = ?3 and ?4 between t.slotStart and t.slotEnd")
  Collection<Trade> findTradeToOpenOrder(@NonNull Scope scope, @NonNull int spread, @NonNull DayOfWeek week, @NonNull LocalTime time);

  // REMOVA TODA A CADEIA DE METHODOS QUE USA ESSE AQUI
  List<Trade> findByScope_Symbol(@NonNull Symbol symbol);


}
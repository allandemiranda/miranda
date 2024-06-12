package lu.forex.system.repositories;

import java.util.List;
import java.util.UUID;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, UUID>, JpaSpecificationExecutor<Trade> {

  List<Trade> findByScope_SymbolOrderByBalanceDesc(@NonNull Symbol symbol);


}
package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;
import lu.forex.system.entities.CurrencyPair;
import lu.forex.system.entities.Order;
import lu.forex.system.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

  @NotNull
  @Query("select o from Order o where o.openTick.symbol.currencyPair.name = ?1 and o.orderStatus = ?2")
  Collection<@NotNull Order> findBySymbolNameAndOrderStatus(@NonNull String symbolName, @NonNull OrderStatus orderStatus);

}
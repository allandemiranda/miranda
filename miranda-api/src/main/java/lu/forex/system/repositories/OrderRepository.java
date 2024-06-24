package lu.forex.system.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lu.forex.system.entities.Order;
import lu.forex.system.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

  @Query("select o from Order o where o.openTick.symbol.currencyPair.name = ?1 and o.orderStatus = ?2")
  Collection<Order> findBySymbolNameAndOrderStatus(@NonNull String symbolName, @NonNull OrderStatus orderStatus);

  @Query("select o from Order o where o.openTick.symbol.currencyPair.name = ?1")
  Collection<Order> findBySymbolName(@NonNull String symbolName);

  @Query("select o from Order o where o.openTick.symbol.id = ?1 and o.orderStatus = ?2 and o.closeTick.timestamp > ?3")
  Collection<Order> findByOpenTick_Symbol_IdAndOrderStatusAndCloseTick_TimestampAfterAllIgnoreCase(@NonNull UUID id, @NonNull OrderStatus orderStatus, @NonNull LocalDateTime timestamp);

  List<Order> findByOpenTick_Symbol_IdAndOrderStatusOrderByOpenTick_TimestampAsc(@NonNull UUID id, @NonNull OrderStatus orderStatus);
}
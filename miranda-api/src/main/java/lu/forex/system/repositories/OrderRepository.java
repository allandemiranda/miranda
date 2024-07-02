package lu.forex.system.repositories;

import java.util.List;
import java.util.UUID;
import lu.forex.system.entities.Order;
import lu.forex.system.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

  List<Order> findByOpenTick_Symbol_IdAndOrderStatusOrderByOpenTick_TimestampAsc(@NonNull UUID id, @NonNull OrderStatus orderStatus);
}
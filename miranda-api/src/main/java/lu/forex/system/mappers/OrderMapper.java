package lu.forex.system.mappers;

import lu.forex.system.dtos.OrderDto;
import lu.forex.system.entities.Order;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {TickMapper.class, TickMapper.class,
    OrderProfitMapper.class})
public interface OrderMapper {

  @Mapping(source = "tradeIsActivate", target = "trade.activate")
  @Mapping(source = "tradeTakeProfit", target = "trade.takeProfit")
  @Mapping(source = "tradeStopLoss", target = "trade.stopLoss")
  @Mapping(source = "tradeId", target = "trade.id")
  Order toEntity(OrderDto orderDto);

  @InheritInverseConfiguration(name = "toEntity")
  OrderDto toDto(Order order);
}
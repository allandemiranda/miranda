package lu.forex.system.mappers;

import lu.forex.system.dtos.OrderDto;
import lu.forex.system.entities.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {TickMapper.class, TickMapper.class})
public interface OrderMapper {

  @Mapping(source = "tradeBalance", target = "trade.balance")
  @Mapping(source = "tradeIsActivate", target = "trade.activate")
  @Mapping(source = "tradeSlotEnd", target = "trade.slotEnd")
  @Mapping(source = "tradeSlotStart", target = "trade.slotStart")
  @Mapping(source = "tradeSpreadMax", target = "trade.spreadMax")
  @Mapping(source = "tradeTakeProfit", target = "trade.takeProfit")
  @Mapping(source = "tradeStopLoss", target = "trade.stopLoss")
  @Mapping(source = "tradeId", target = "trade.id")
  Order toEntity(OrderDto orderDto);

  @InheritInverseConfiguration(name = "toEntity")
  OrderDto toDto(Order order);

  @InheritConfiguration(name = "toEntity")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Order partialUpdate(
      OrderDto orderDto, @MappingTarget Order order);
}
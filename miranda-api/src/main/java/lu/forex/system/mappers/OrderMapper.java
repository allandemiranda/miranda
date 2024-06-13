package lu.forex.system.mappers;

import lu.forex.system.dtos.OrderDto;
import lu.forex.system.entities.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {TickMapper.class, TickMapper.class,
    OrderProfitMapper.class})
public interface OrderMapper {

  Order toEntity(OrderDto orderDto);

  OrderDto toDto(Order order);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Order partialUpdate(OrderDto orderDto,
      @MappingTarget Order order);
}
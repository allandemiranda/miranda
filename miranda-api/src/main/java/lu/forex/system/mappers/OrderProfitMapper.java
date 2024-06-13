package lu.forex.system.mappers;

import lu.forex.system.dtos.OrderProfitDto;
import lu.forex.system.entities.OrderProfit;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface OrderProfitMapper {

  OrderProfit toEntity(OrderProfitDto orderProfitDto);

  OrderProfitDto toDto(OrderProfit orderProfit);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  OrderProfit partialUpdate(OrderProfitDto orderProfitDto,
      @MappingTarget OrderProfit orderProfit);
}
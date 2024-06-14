package lu.forex.system.mappers;

import lu.forex.system.dtos.TradeDto;
import lu.forex.system.entities.Trade;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {ScopeMapper.class, OrderMapper.class})
public interface TradeMapper {

  Trade toEntity(TradeDto tradeDto);

  TradeDto toDto(Trade trade);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Trade partialUpdate(TradeDto tradeDto, @MappingTarget Trade trade);
}
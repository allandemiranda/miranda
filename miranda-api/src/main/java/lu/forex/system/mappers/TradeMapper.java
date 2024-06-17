package lu.forex.system.mappers;

import lu.forex.system.dtos.TradeDto;
import lu.forex.system.entities.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {ScopeMapper.class, OrderMapper.class})
public interface TradeMapper {

  Trade toEntity(TradeDto tradeDto);

  TradeDto toDto(Trade trade);
}
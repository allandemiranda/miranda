package lu.forex.system.mappers;

import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {ScopeMapper.class})
public interface CandlestickMapper {

  Candlestick toEntity(CandlestickDto candlestickDto);

  CandlestickDto toDto(Candlestick candlestick);
}
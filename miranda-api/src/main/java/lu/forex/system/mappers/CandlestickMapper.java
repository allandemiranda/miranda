package lu.forex.system.mappers;

import lu.forex.system.dtos.CandlestickIndicatorsDto;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface CandlestickMapper {

  CandlestickResponseDto toDto(Candlestick candlestick);

  CandlestickIndicatorsDto toDtoIndicator(Candlestick candlestick);
}
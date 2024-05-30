package lu.forex.system.mappers;

import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)public interface CandlestickMapper {

  Candlestick toEntity(CandlestickResponseDto candlestickResponseDto);

  CandlestickResponseDto toDto(Candlestick candlestick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)Candlestick partialUpdate(
      CandlestickResponseDto candlestickResponseDto, @MappingTarget Candlestick candlestick);
}
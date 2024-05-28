package lu.forex.system.mappers;

import lu.forex.system.dtos.CandlestickIndicatorDto;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {SymbolMapper.class, AcIndicatorMapper.class,
    AdxIndicatorMapper.class, EmaStatisticMapper.class, MacdIndicatorMapper.class})
public interface CandlestickMapper {

  CandlestickResponseDto toDto(Candlestick candlestick);

  Candlestick toEntity(CandlestickResponseDto candlestickResponseDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Candlestick partialUpdate(CandlestickResponseDto candlestickResponseDto, @MappingTarget Candlestick candlestick);

  Candlestick toEntity(CandlestickIndicatorDto candlestickIndicatorDto);

  CandlestickIndicatorDto toDto1(Candlestick candlestick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Candlestick partialUpdate(
      CandlestickIndicatorDto candlestickIndicatorDto, @MappingTarget Candlestick candlestick);
}
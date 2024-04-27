package lu.forex.system.mappers;

import lu.forex.system.dtos.CandlestickCreateDto;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.CandlestickUpdateDto;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface CandlestickMapper {

  Candlestick toEntity(CandlestickDto candlestickDto);

  CandlestickDto toDto(Candlestick candlestick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Candlestick partialUpdate(CandlestickDto candlestickDto, @MappingTarget Candlestick candlestick);

  Candlestick toEntity(CandlestickUpdateDto candlestickUpdateDto);

  CandlestickUpdateDto toDto1(Candlestick candlestick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Candlestick partialUpdate(CandlestickUpdateDto candlestickUpdateDto, @MappingTarget Candlestick candlestick);

  Candlestick toEntity(CandlestickCreateDto candlestickCreateDto);

  CandlestickCreateDto toDto2(Candlestick candlestick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Candlestick partialUpdate(CandlestickCreateDto candlestickCreateDto, @MappingTarget Candlestick candlestick);
}
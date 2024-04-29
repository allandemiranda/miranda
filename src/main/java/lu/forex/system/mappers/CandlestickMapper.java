package lu.forex.system.mappers;

import lu.forex.system.dtos.CandlestickCreateDto;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.dtos.CandlestickUpdateDto;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface CandlestickMapper {

  @Mapping(target = "high", source = "candlestickCreateDto.price")
  @Mapping(target = "low", source = "candlestickCreateDto.price")
  @Mapping(target = "open", source = "candlestickCreateDto.price")
  @Mapping(target = "close", source = "candlestickCreateDto.price")
  Candlestick toEntity(CandlestickCreateDto candlestickCreateDto);


  Candlestick toEntity(CandlestickUpdateDto candlestickUpdateDto);

  CandlestickResponseDto toDto(Candlestick candlestick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Candlestick partialUpdate(
      CandlestickUpdateDto candlestickUpdateDto, @MappingTarget Candlestick candlestick);
}
package lu.forex.system.mappers;

import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {ScopeMapper.class})
public interface CandlestickMapper {

  Candlestick toEntity(CandlestickDto candlestickDto);

  @AfterMapping
  default void linkMovingAverages(@MappingTarget Candlestick candlestick) {
    candlestick.getMovingAverages().forEach(movingAverage -> movingAverage.setCandlestick(candlestick));
  }

  @AfterMapping
  default void linkTechnicalIndicators(@MappingTarget Candlestick candlestick) {
    candlestick.getTechnicalIndicators().forEach(technicalIndicator -> technicalIndicator.setCandlestick(candlestick));
  }

  CandlestickDto toDto(Candlestick candlestick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Candlestick partialUpdate(
      CandlestickDto candlestickDto, @MappingTarget Candlestick candlestick);
}
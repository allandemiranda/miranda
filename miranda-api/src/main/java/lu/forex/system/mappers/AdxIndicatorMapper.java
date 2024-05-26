package lu.forex.system.mappers;

import lu.forex.system.dtos.AdxIndicatorDto;
import lu.forex.system.entities.AdxIndicator;
import lu.forex.system.entities.Candlestick;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {CandlestickMapper.class})
public interface AdxIndicatorMapper {

  AdxIndicator toEntity(AdxIndicatorDto adxIndicatorDto);

  @AfterMapping
  default void linkCandlestick(@MappingTarget AdxIndicator adxIndicator) {
    Candlestick candlestick = adxIndicator.getCandlestick();
    if (candlestick != null) {
      candlestick.setAdxIndicator(adxIndicator);
    }
  }

  AdxIndicatorDto toDto(AdxIndicator adxIndicator);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  AdxIndicator partialUpdate(AdxIndicatorDto adxIndicatorDto,
      @MappingTarget AdxIndicator adxIndicator);
}
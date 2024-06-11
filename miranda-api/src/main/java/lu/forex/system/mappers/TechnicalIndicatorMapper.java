package lu.forex.system.mappers;

import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.TechnicalIndicator;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface TechnicalIndicatorMapper {

  TechnicalIndicator toEntity(TechnicalIndicatorDto technicalIndicatorDto);

  TechnicalIndicatorDto toDto(TechnicalIndicator technicalIndicator);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  TechnicalIndicator partialUpdate(
      TechnicalIndicatorDto technicalIndicatorDto, @MappingTarget TechnicalIndicator technicalIndicator);
}
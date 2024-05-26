package lu.forex.system.mappers;

import lu.forex.system.dtos.EmaIndicatorDto;
import lu.forex.system.entities.EmaIndicator;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface EmaIndicatorMapper {

  EmaIndicator toEntity(EmaIndicatorDto emaIndicatorDto);

  EmaIndicatorDto toDto(EmaIndicator emaIndicator);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  EmaIndicator partialUpdate(EmaIndicatorDto emaIndicatorDto,
      @MappingTarget EmaIndicator emaIndicator);
}
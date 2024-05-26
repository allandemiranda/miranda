package lu.forex.system.mappers;

import lu.forex.system.dtos.AcIndicatorDto;
import lu.forex.system.entities.AcIndicator;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface AcIndicatorMapper {

  AcIndicator toEntity(AcIndicatorDto acIndicatorDto);

  AcIndicatorDto toDto(AcIndicator acIndicator);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  AcIndicator partialUpdate(AcIndicatorDto acIndicatorDto,
      @MappingTarget AcIndicator acIndicator);
}
package lu.forex.system.mappers;

import lu.forex.system.dtos.MacdIndicatorDto;
import lu.forex.system.entities.MacdIndicator;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface MacdIndicatorMapper {

  MacdIndicator toEntity(MacdIndicatorDto macdIndicatorDto);

  MacdIndicatorDto toDto(MacdIndicator macdIndicator);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  MacdIndicator partialUpdate(
      MacdIndicatorDto macdIndicatorDto, @MappingTarget MacdIndicator macdIndicator);
}
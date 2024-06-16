package lu.forex.system.mappers;

import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.TechnicalIndicator;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface TechnicalIndicatorMapper {

  TechnicalIndicator toEntity(TechnicalIndicatorDto technicalIndicatorDto);

  TechnicalIndicatorDto toDto(TechnicalIndicator technicalIndicator);
}
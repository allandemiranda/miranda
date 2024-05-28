package lu.forex.system.mappers;

import lu.forex.system.dtos.EmaStatisticDto;
import lu.forex.system.entities.EmaStatistic;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface EmaStatisticMapper {

  EmaStatistic toEntity(EmaStatisticDto emaStatisticDto);

  EmaStatisticDto toDto(EmaStatistic emaStatistic);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  EmaStatistic partialUpdate(EmaStatisticDto emaStatisticDto,
      @MappingTarget EmaStatistic emaStatistic);
}
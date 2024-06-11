package lu.forex.system.mappers;

import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.entities.MovingAverage;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface MovingAverageMapper {

  MovingAverage toEntity(MovingAverageDto movingAverageDto);

  MovingAverageDto toDto(MovingAverage movingAverage);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  MovingAverage partialUpdate(
      MovingAverageDto movingAverageDto, @MappingTarget MovingAverage movingAverage);

  MovingAverage toEntity(NewMovingAverageDto newMovingAverageDto);

  NewMovingAverageDto toDto1(MovingAverage movingAverage);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  MovingAverage partialUpdate(
      NewMovingAverageDto newMovingAverageDto, @MappingTarget MovingAverage movingAverage);
}
package lu.forex.system.mappers;

import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.entities.MovingAverage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface MovingAverageMapper {

  MovingAverage toEntity(MovingAverageDto movingAverageDto);

  MovingAverageDto toDto(MovingAverage movingAverage);

  MovingAverage toEntity(NewMovingAverageDto newMovingAverageDto);

  NewMovingAverageDto toNewDto(MovingAverage movingAverage);
}
package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.entities.MovingAverage;

public interface MovingAverageMapper {

  @NotNull
  MovingAverage toEntity(final @NotNull MovingAverageDto movingAverageDto);

  @NotNull
  MovingAverageDto toDto(final @NotNull MovingAverage movingAverage);

  @NotNull
  MovingAverage toEntity(final @NotNull NewMovingAverageDto newMovingAverageDto);

  @NotNull
  NewMovingAverageDto toNewDto(final @NotNull MovingAverage movingAverage);
}
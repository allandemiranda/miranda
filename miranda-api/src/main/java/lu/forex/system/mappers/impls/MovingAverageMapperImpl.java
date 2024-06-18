package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.mappers.MovingAverageMapper;
import org.springframework.stereotype.Component;

@Component
public class MovingAverageMapperImpl implements MovingAverageMapper {

  @Override
  public @NotNull MovingAverage toEntity(final @NotNull MovingAverageDto movingAverageDto) {
    final var movingAverage = new MovingAverage();
    movingAverage.setId(movingAverageDto.id());
    movingAverage.setType(movingAverageDto.type());
    movingAverage.setPeriod(movingAverageDto.period());
    movingAverage.setPriceType(movingAverageDto.priceType());
    movingAverage.setValue(movingAverageDto.value());
    return movingAverage;
  }

  @Override
  public @NotNull MovingAverageDto toDto(final @NotNull MovingAverage movingAverage) {
    final var id = movingAverage.getId();
    final var type = movingAverage.getType();
    final var period = movingAverage.getPeriod();
    final var priceType = movingAverage.getPriceType();
    final var value = movingAverage.getValue();
    return new MovingAverageDto(id, type, period, priceType, value);
  }

  @Override
  public @NotNull MovingAverage toEntity(final @NotNull NewMovingAverageDto newMovingAverageDto) {
    final var movingAverage = new MovingAverage();
    movingAverage.setType(newMovingAverageDto.type());
    movingAverage.setPeriod(newMovingAverageDto.period());
    movingAverage.setPriceType(newMovingAverageDto.priceType());
    return movingAverage;
  }

  @Override
  public @NotNull NewMovingAverageDto toNewDto(final @NotNull MovingAverage movingAverage) {
    final var type = movingAverage.getType();
    final var period = movingAverage.getPeriod();
    final var priceType = movingAverage.getPriceType();
    return new NewMovingAverageDto(type, period, priceType);
  }
}

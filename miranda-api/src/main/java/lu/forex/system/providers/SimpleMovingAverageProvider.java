package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.repositories.MovingAverageRepository;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Service("simpleMovingAverage")
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class SimpleMovingAverageProvider implements MovingAverageService {

  @Getter(AccessLevel.PUBLIC)
  private final MovingAverageRepository movingAverageRepository;
  @Getter(AccessLevel.PUBLIC)
  private final MovingAverageMapper movingAverageMapper;
  private final CandlestickMapper candlestickMapper;

  @Override
  public MovingAverageType getMovingAverageType() {
    return MovingAverageType.SMA;
  }

  @Override
  public @NotNull Collection<MovingAverageDto> calculateMovingAverage(final @NotNull List<@NotNull CandlestickDto> candlestickDtos) {
    final Candlestick currentCandlestick = this.getCandlestickMapper().toEntity(candlestickDtos.getFirst());
    return currentCandlestick.getMovingAverages().stream().filter(ma -> this.getMovingAverageType().equals(ma.getType())).map(movingAverage -> {
      final Collection<Double> prices = candlestickDtos.stream().limit(movingAverage.getPeriod()).map(c -> movingAverage.getPriceType().getPrice(c)).toList();
      movingAverage.setValue(MathUtils.getMed(prices));
      return this.getMovingAverageRepository().save(movingAverage);
    }).map(movingAverage -> this.getMovingAverageMapper().toDto(movingAverage)).toList();
  }
}

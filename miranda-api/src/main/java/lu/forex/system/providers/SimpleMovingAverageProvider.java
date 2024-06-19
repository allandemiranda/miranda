package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.entities.MovingAverage;
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
  public void calculateMovingAverage(final @NotNull List<@NotNull CandlestickDto> candlestickDtos) {
    final CandlestickDto candlestickDtosFirst = candlestickDtos.getFirst();
    final Collection<MovingAverage> collection = candlestickDtosFirst.movingAverages().parallelStream()
        .filter(ma -> this.getMovingAverageType().equals(ma.type())).map(movingAverageDto -> {
          final Collection<CandlestickDto> collectionLimited = candlestickDtos.stream().limit(movingAverageDto.period()).toList();
          final Collection<Double> prices = collectionLimited.parallelStream().map(c -> movingAverageDto.priceType().getPrice(c)).toList();
          final MovingAverage movingAverage = this.getMovingAverageMapper().toEntity(movingAverageDto);
          movingAverage.setValue(MathUtils.getMed(prices));
          return movingAverage;
        }).toList();
    if (!collection.isEmpty()) {
      this.getMovingAverageRepository().saveAll(collection);
      collection.forEach(movingAverage -> candlestickDtosFirst.movingAverages().removeIf(ma -> ma.id().equals(movingAverage.getId())));
      final Collection<MovingAverageDto> updateDto = collection.parallelStream()
          .map(movingAverage -> this.getMovingAverageMapper().toDto(movingAverage)).toList();
      candlestickDtosFirst.movingAverages().addAll(updateDto);
    }
  }
}

package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.repositories.MovingAverageRepository;
import lu.forex.system.services.MovingAverageService;
import lu.forex.system.utils.MathUtils;
import org.springframework.stereotype.Service;

@Getter
@Service("simpleMovingAverage")
@RequiredArgsConstructor
public class SimpleMovingAverageProvider implements MovingAverageService {

  private final MovingAverageRepository movingAverageRepository;
  private final MovingAverageMapper movingAverageMapper;

  @Override
  public MovingAverageType getMovingAverageType() {
    return MovingAverageType.SMA;
  }

  @Override
  public void calculateMovingAverage(final @NotNull CandlestickDto @NotNull [] candlesticksDesc) {
    final Collection<MovingAverage> collection = candlesticksDesc[0].movingAverages().stream()
        .filter(ma -> this.getMovingAverageType().equals(ma.type())).filter(movingAverageDto -> candlesticksDesc.length >= movingAverageDto.period())
        .map(movingAverageDto -> {
          final MovingAverage movingAverage = this.getMovingAverageMapper().toEntity(movingAverageDto);
          movingAverage.setValue(MathUtils.getMed(IntStream.range(0, movingAverageDto.period()).parallel().mapToObj(i -> movingAverageDto.priceType().getPrice(candlesticksDesc[i])).toList()));
          return movingAverage;
        }).toList();
    this.getMovingAverageRepository().saveAll(collection);
  }
}

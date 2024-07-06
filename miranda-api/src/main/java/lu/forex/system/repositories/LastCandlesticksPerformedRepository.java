package lu.forex.system.repositories;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.services.TechnicalIndicatorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Getter(AccessLevel.PRIVATE)
public class LastCandlesticksPerformedRepository {

  private final CandlestickRepository candlestickRepository;
  private final ScopeRepository scopeRepository;
  private final TechnicalIndicatorService acceleratorOscillatorService;
  private final TechnicalIndicatorService averageDirectionalIndexService;
  private final TechnicalIndicatorService movingAverageConvergenceDivergenceService;
  private final ConcurrentHashMap<UUID, LinkedList<UUID>> lastCandlesticksMap = new ConcurrentHashMap<>();

  public LastCandlesticksPerformedRepository(final CandlestickRepository candlestickRepository, final ScopeRepository scopeRepository,
      @Qualifier("acceleratorOscillator") final TechnicalIndicatorService acceleratorOscillatorService,
      @Qualifier("averageDirectionalIndex") final TechnicalIndicatorService averageDirectionalIndexService,
      @Qualifier("movingAverageConvergenceDivergence") final TechnicalIndicatorService movingAverageConvergenceDivergenceService) {
    this.candlestickRepository = candlestickRepository;
    this.scopeRepository = scopeRepository;
    this.acceleratorOscillatorService = acceleratorOscillatorService;
    this.averageDirectionalIndexService = averageDirectionalIndexService;
    this.movingAverageConvergenceDivergenceService = movingAverageConvergenceDivergenceService;
  }

  @PostConstruct
  private void init() {
    final Collection<TechnicalIndicatorService> indicatorServices = List.of(this.getAcceleratorOscillatorService(), this.getAverageDirectionalIndexService(), this.getMovingAverageConvergenceDivergenceService());
    final int technicalIndicatorSize = indicatorServices.stream().mapToInt(TechnicalIndicatorService::getNumberOfCandlesticksToCalculate).max().orElse(0);
    this.getScopeRepository().findAll().forEach(scope -> {
      final LinkedList<UUID> candlesticks = this.getCandlestickRepository().findByScope_IdOrderByTimestampDescWithLimit(scope.getId(), technicalIndicatorSize + 1).stream().map(Candlestick::getId).collect(Collectors.toCollection(LinkedList::new));
      this.getLastCandlesticksMap().put(scope.getId(), candlesticks);
    });
  }

  @NotNull
  public UUID[] getLastCandlesticksNotIncludingFirst(final @NotNull UUID scopeId) {
    return this.getLastCandlesticksMap().getOrDefault(scopeId, new LinkedList<>()).stream().skip(1).toArray(UUID[]::new);
  }

  @NotNull
  public Optional<UUID> getRealLastCandlestickId(final @NotNull UUID uuid) {
    final LinkedList<UUID> candlesticks = this.getLastCandlesticksMap().getOrDefault(uuid, new LinkedList<>());
    return candlesticks.isEmpty() ? Optional.empty() : Optional.of(candlesticks.getFirst());
  }

  public void addNextCandlestick(final @NotNull Candlestick candlestick) {
    final LinkedList<UUID> candlesticks = this.getLastCandlesticksMap().getOrDefault(candlestick.getScope().getId(), new LinkedList<>());
    if (!candlesticks.isEmpty()) {
      candlesticks.removeLast();
      candlesticks.addFirst(candlestick.getId());
    }
  }
}

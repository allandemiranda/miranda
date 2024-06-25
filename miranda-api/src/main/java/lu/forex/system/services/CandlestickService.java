package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface CandlestickService {

  @Transactional(readOnly = true)
  @NotNull
  List<@NotNull CandlestickDto> findCandlesticksDescWithLimit(final @NotNull UUID scopeId, final @Positive int limit);

  @Transactional()
  @NotNull
  CandlestickDto processingCandlestick(final @NotNull TickDto tickDto, final @NotNull ScopeDto scopeDto);

  @Transactional()
  @NotNull
  CandlestickDto addingTechnicalIndicators(final @NotNull Collection<TechnicalIndicatorDto> technicalIndicators, final @NotNull UUID candlestickId);

  @Transactional()
  @NotNull
  CandlestickDto addingMovingAverages(final @NotNull Collection<MovingAverageDto> movingAverages, final @NotNull UUID candlestickId);

  @Transactional
  @NotNull
  CandlestickDto processSignalIndicatorByCandlestickId(final @NotNull UUID candlestickId);

  @Transactional(readOnly = true)
  Collection<CandlestickDto> getAllCandlestickByScopeIdAsync(final @NotNull UUID scopeId);

  @Transactional(readOnly = true)
  List<CandlestickDto> getAllCandlestickByScopeIdDesc(final @NotNull UUID scopeId);

  @Transactional(readOnly = true)
  Collection<CandlestickDto> getAllCandlestickNotNeutralByScopeIdAsync(final @NotNull UUID scopeId);

  @Async
  void readTicksToGenerateCandlesticks(final @NotNull ScopeDto scopeDto, final @NotNull Collection<TickDto> tickDtoList);

  @Async
  void initIndicatorsOnCandlesticks(final @NotNull Collection<CandlestickDto> candlesticksDto, final @NotNull Collection<TechnicalIndicatorService> indicatorServices);

  @Async
  void initAveragesToCandlesticks(final @NotNull Collection<SimpleEntry<Collection<MovingAverageDto>, UUID>> candlesticksToSave);

  @Async
  void computingIndicatorsByInit(final @NotNull Collection<TechnicalIndicatorService> indicatorServices, final @NotNull Collection<MovingAverageService> movingAverageServices, final @NotNull Map<UUID, List<List<UUID>>> scopeIdByCandlestickDtosId);
}

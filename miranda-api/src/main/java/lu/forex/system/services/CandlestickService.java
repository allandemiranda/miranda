package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.dtos.TickDto;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface CandlestickService {

  @Transactional(readOnly = true)
  @NotNull
  CandlestickDto getCandlestick(@NotNull UUID id);

  @Transactional(readOnly = true)
  @NotNull
  List<@NotNull CandlestickDto> findCandlesticksDescWithLimit(final @NotNull UUID scopeId, final @Positive int limit);

  @Transactional(readOnly = true)
  @NotNull
  CandlestickDto @NotNull [] findCandlesticksDescLimited(final @NotNull UUID scopeId);

  @Transactional(readOnly = true)
  @NotNull
  List<@NotNull CandlestickDto> findCandlesticksAsc(final @NotNull UUID scopeId);

  @Transactional()
  @NotNull
  CandlestickDto processingCandlestick(final @NotNull TickDto tickDto, final @NotNull ScopeDto scopeDto);

  @Transactional
  CandlestickDto computingSignal(final @NotNull UUID candlestickId);

  @Transactional
  @NotNull
  CandlestickDto addingTechnicalIndicatorsAndMovingAverage(final @NotNull Stream<TechnicalIndicatorDto> technicalIndicators, final @NotNull Stream<MovingAverageDto> movingAverages, final @NotNull UUID candlestickId);

  @Transactional
  void batchInitIndicatorsAndAveragesOnCandlesticks(final Stream<Triple<UUID, Stream<TechnicalIndicatorDto>, Stream<MovingAverageDto>>> candlesticksToProcess);

  @Transactional
  @NotNull UUID @NotNull [] batchReadTicksToGenerateCandlesticks(final @NotNull ScopeDto scopeDto, final @NotNull TickDto @NotNull [] ticksDto);
}

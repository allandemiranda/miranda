package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickIndicatorDto;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.AcIndicator;
import lu.forex.system.entities.AdxIndicator;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.EmaStatistic;
import lu.forex.system.entities.MacdIndicator;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.CandlestickApply;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.repositories.AcIndicatorRepository;
import lu.forex.system.repositories.CandlestickRepository;
import lu.forex.system.repositories.EmaIndicatorRepository;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.utils.MathUtils;
import lu.forex.system.utils.TimeFrameUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickProvider implements CandlestickService {

  private final CandlestickRepository candlestickRepository;
  private final CandlestickMapper candlestickMapper;
  private final SymbolRepository symbolRepository;
  private final EmaIndicatorRepository emaIndicatorRepository;
  private final AcIndicatorRepository acIndicatorRepository;

  @NotNull
  @Override
  public Stream<CandlestickResponseDto> getCandlesticks(@NotNull final String symbolName, @NotNull final TimeFrame timeFrame) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampAsc(symbol, timeFrame).map(this.getCandlestickMapper()::toDto);
  }

  @Override
  public void createOrUpdateCandlestickByPrice(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      @NotNull final LocalDateTime timestamp, final @NotNull TimeFrame timeFrame, final double price) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    final LocalDateTime localTimestamp = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final Optional<Candlestick> lastCandlestickOptional = this.getCandlestickRepository()
        .findFirstBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame);
    final Candlestick candlestick =
        (lastCandlestickOptional.isPresent() && lastCandlestickOptional.get().getTimestamp().equals(localTimestamp)) ? updateCandlestick(price,
            lastCandlestickOptional.get()) : createCandlestick(timeFrame, price, localTimestamp, symbol);
    this.getCandlestickRepository().save(candlestick);

    this.calculatingIndicators(candlestick);
    this.getCandlestickRepository().save(candlestick);
  }

  private @NotNull Candlestick createCandlestick(final @NotNull TimeFrame timeFrame, final double price, final LocalDateTime timestamp,
      final Symbol symbol) {
    final Candlestick candlestick = new Candlestick();

    candlestick.setTimestamp(timestamp);
    candlestick.setTimeFrame(timeFrame);
    candlestick.setSymbol(symbol);
    candlestick.setHigh(price);
    candlestick.setLow(price);
    candlestick.setOpen(price);
    candlestick.setClose(price);

    candlestick.setAcIndicator(new AcIndicator());
    this.getAcIndicatorRepository().getFirstByCandlestick_SymbolAndCandlestick_TimeFrameOrderByCandlestick_TimestampDesc(symbol, timeFrame)
        .ifPresent(acIndicator -> {
          candlestick.getAcIndicator().setLestAc(acIndicator.getAc());
          candlestick.getAcIndicator().setLestColor(acIndicator.getColor());
        });
    candlestick.setAdxIndicator(new AdxIndicator());
    candlestick.getEmaStatistics().add(emaIndicatorInit(timeFrame, timestamp, symbol.getName(), 12, CandlestickApply.CLOSE));
    candlestick.getEmaStatistics().add(emaIndicatorInit(timeFrame, timestamp, symbol.getName(), 26, CandlestickApply.CLOSE));
    candlestick.setMacdIndicator(new MacdIndicator());

    return candlestick;
  }

  private @NotNull EmaStatistic emaIndicatorInit(final @NotNull TimeFrame timeFrame, final LocalDateTime timestamp, final String symbolName,
      final int period, final @NotNull CandlestickApply candlestickApply) {
    final EmaStatistic emaStatistic = new EmaStatistic();
    emaStatistic.setPeriod(period);
    emaStatistic.setCandlestickApply(candlestickApply);
    emaStatistic.setSymbolName(symbolName);
    emaStatistic.setTimeFrame(timeFrame);
    emaStatistic.setTimestamp(timestamp);
    emaStatistic.setLastEma(null);
    this.getEmaIndicatorRepository().getFirstByPeriodAndCandlestickApplyAndSymbolNameAndTimeFrameOrderByTimestampDesc(emaStatistic.getPeriod(),
        emaStatistic.getCandlestickApply(), symbolName, timeFrame).ifPresent(indicator -> emaStatistic.setLastEma(indicator.getEma()));
    return emaStatistic;
  }

  private void calculatingIndicators(final @NotNull Candlestick candlestick) {
    candlestick.getEmaStatistics()
        .forEach(emaIndicator -> emaIndicator.setEma(MathUtils.getEma(emaIndicator, candlestickRepository, emaIndicatorRepository, candlestick)));
    this.getCandlestickRepository().save(candlestick);

    this.calculateAcIndicator(candlestick);
    this.calculateAdxIndicator(candlestick);
    this.calculateMacdIndicator(candlestick);
  }

  private @NotNull Candlestick updateCandlestick(final double price, final @NotNull Candlestick candlestick) {
    if (candlestick.getHigh() < price) {
      candlestick.setHigh(price);
    } else if (candlestick.getLow() > price) {
      candlestick.setLow(price);
    }
    candlestick.setClose(price);

    return candlestick;
  }

  private void calculateAcIndicator(final @NotNull Candlestick candlestick) {
    final Symbol symbol = candlestick.getSymbol();
    final TimeFrame timeFrame = candlestick.getTimeFrame();

    // set the MP value
    final double mp = CandlestickApply.TYPICAL_PRICE.getPrice(candlestick);
    candlestick.getAcIndicator().setMp(mp);

    // get SMA(MP,34)
    final Collection<Double> collectionSmaMp34 = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, 34)
        .map(c -> c.getAcIndicator().getMp()).toList();
    if (collectionSmaMp34.size() != 34) {
      candlestick.getAcIndicator().setAo(null);
      candlestick.getAcIndicator().setAc(null);
    } else {
      final double smaMp34 = MathUtils.getMed(collectionSmaMp34);

      // get SMA(MP,5)
      final double smaMp5 = MathUtils.getMed(
          this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, 5).map(c -> c.getAcIndicator().getMp())
              .toList());

      // get SMA(MP,5) - SMA(MP,34)
      final double ao = BigDecimal.valueOf(smaMp5).subtract(BigDecimal.valueOf(smaMp34)).doubleValue();
      candlestick.getAcIndicator().setAo(ao);

      // get SMA(ao,5)
      final Collection<Double> collectionSmaAo5 = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, 5)
          .filter(c -> !c.getId().equals(candlestick.getId())).filter(c -> Objects.nonNull(c.getAcIndicator().getAo()))
          .map(c -> c.getAcIndicator().getAo()).collect(Collectors.toCollection(ArrayList::new));
      if (collectionSmaAo5.size() == 4) {
        collectionSmaAo5.add(ao);
        final double smaAo5 = MathUtils.getMed(collectionSmaAo5);

        // get ao - SMA(ao,5)
        final double ac = BigDecimal.valueOf(ao).subtract(BigDecimal.valueOf(smaAo5)).doubleValue();
        candlestick.getAcIndicator().setAc(ac);
      } else {
        candlestick.getAcIndicator().setAc(null);
      }
    }
  }

  private void calculateAdxIndicator(final @NotNull Candlestick candlestick) {
    final Symbol symbol = candlestick.getSymbol();
    final TimeFrame timeFrame = candlestick.getTimeFrame();

    final Collection<Candlestick> collection = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, 2)
        .filter(c -> !c.getId().equals(candlestick.getId())).toList();
    if (collection.size() == 1) {
      final Candlestick lastCandlestick = collection.iterator().next();

      // get TR1
      final double tr1 = MathUtils.getMax(BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(candlestick.getLow())).doubleValue(),
          BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(candlestick.getClose())).doubleValue(),
          Math.abs(BigDecimal.valueOf(candlestick.getLow()).subtract(BigDecimal.valueOf(lastCandlestick.getClose())).doubleValue()));
      candlestick.getAdxIndicator().setTrOne(tr1);

      // get +DM1
      final double pDm1 = BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(lastCandlestick.getHigh()))
                              .compareTo(BigDecimal.valueOf(lastCandlestick.getLow()).subtract(BigDecimal.valueOf(candlestick.getLow()))) > 0
          ? MathUtils.getMax(BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(lastCandlestick.getHigh())).doubleValue(), 0d)
          : 0d;
      candlestick.getAdxIndicator().setPDmOne(pDm1);

      // get -DM1
      final double nDm1 = BigDecimal.valueOf(lastCandlestick.getLow()).subtract(BigDecimal.valueOf(candlestick.getLow()))
                              .compareTo(BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(lastCandlestick.getHigh()))) > 0
          ? MathUtils.getMax(BigDecimal.valueOf(lastCandlestick.getLow()).subtract(BigDecimal.valueOf(candlestick.getLow())).doubleValue(), 0d) : 0d;
      candlestick.getAdxIndicator().setNDmOne(nDm1);

      final Collection<double[]> collectionOne = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, 14)
          .filter(c -> !c.getId().equals(candlestick.getId())).filter(
              c -> Objects.nonNull(c.getAdxIndicator().getTrOne()) && Objects.nonNull(c.getAdxIndicator().getPDmOne()) && Objects.nonNull(
                  c.getAdxIndicator().getNDmOne()))
          .map(c -> new double[]{c.getAdxIndicator().getTrOne(), c.getAdxIndicator().getPDmOne(), c.getAdxIndicator().getNDmOne()})
          .collect(Collectors.toCollection(ArrayList::new));
      if (collectionOne.size() == 13) {
        collectionOne.add(new double[]{tr1, pDm1, nDm1});
        // get TR(P)
        final double trP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[0]).toList());

        // get +DM(P)
        final double pDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[1]).toList());

        // get -DM(P)
        final double nDmP = MathUtils.getSum(collectionOne.stream().map(doubles -> doubles[2]).toList());

        // get +DI(P)
        final double pDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(pDmP, trP));
        candlestick.getAdxIndicator().setPDiP(pDiP);

        // get -DI(P)
        final double nDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(nDmP, trP));
        candlestick.getAdxIndicator().setNDiP(nDiP);

        // get DI diff
        final double diDiff = Math.abs(BigDecimal.valueOf(pDiP).subtract(BigDecimal.valueOf(nDiP)).doubleValue());

        // get DI sum
        final double diSum = BigDecimal.valueOf(pDiP).add(BigDecimal.valueOf(nDiP)).doubleValue();

        // get DX
        final double dx = MathUtils.getMultiplication(100, MathUtils.getDivision(diDiff, diSum));
        candlestick.getAdxIndicator().setDx(dx);

        final Collection<Double> collectionDx = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, 14)
            .filter(c -> !c.getId().equals(candlestick.getId())).filter(c -> Objects.nonNull(c.getAdxIndicator().getDx()))
            .map(c -> c.getAdxIndicator().getDx()).collect(Collectors.toCollection(ArrayList::new));
        if (collectionDx.size() == 13) {
          collectionDx.add(dx);
          // get ADX
          final double adx = MathUtils.getMed(collectionDx);
          candlestick.getAdxIndicator().setAdx(adx);
        }
      }
    }
  }

  private void calculateMacdIndicator(final @NotNull Candlestick candlestick) {
    final EmaStatistic ema12 = candlestick.getEmaStatistics().stream().filter(ema -> ema.getPeriod() == 12 && Objects.nonNull(ema.getEma()))
        .findFirst().orElse(null);
    final EmaStatistic ema26 = candlestick.getEmaStatistics().stream().filter(ema -> ema.getPeriod() == 26 && Objects.nonNull(ema.getEma()))
        .findFirst().orElse(null);
    if (Objects.nonNull(ema12) && Objects.nonNull(ema26)) {
      final Symbol symbol = candlestick.getSymbol();
      final TimeFrame timeFrame = candlestick.getTimeFrame();

      final double macd = MathUtils.getSubtract(ema12.getEma(), ema26.getEma());
      candlestick.getMacdIndicator().setMacd(macd);

      final Collection<Double> collectionMacd = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, 9)
          .filter(c -> !c.getId().equals(candlestick.getId())).filter(c -> Objects.nonNull(c.getMacdIndicator().getMacd()))
          .map(c -> c.getMacdIndicator().getMacd()).collect(Collectors.toCollection(ArrayList::new));
      if (collectionMacd.size() == 8) {
        collectionMacd.add(macd);
        final double signal = MathUtils.getMed(collectionMacd);
        candlestick.getMacdIndicator().setSignal(signal);
      }
    }
  }

  @Override
  public @NotNull Stream<CandlestickIndicatorDto> getLastCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      @NotNull final TimeFrame timeFrame, final int limit) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame, limit)
        .sorted(Comparator.comparing(Candlestick::getTimestamp)).map(candlestickMapper::toDto1);
  }
}

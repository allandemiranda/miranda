package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lu.forex.system.dtos.CandlestickIndicatorDto;
import lu.forex.system.dtos.CandlestickResponseDto;
import lu.forex.system.entities.AcIndicator;
import lu.forex.system.entities.AdxIndicator;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.exceptions.SymbolNotFoundException;
import lu.forex.system.mappers.CandlestickMapper;
import lu.forex.system.repositories.CandlestickRepository;
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
    final LocalDateTime localTimesFrame = TimeFrameUtils.getCandlestickDateTime(timestamp, timeFrame);
    final Optional<Candlestick> lastCandlestickOptional = this.getCandlestickRepository()
        .findFirstBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame);
    if (lastCandlestickOptional.isPresent() && lastCandlestickOptional.get().getTimestamp().equals(localTimesFrame)) {
      final Candlestick candlestick = updateCandlestick(timeFrame, price, lastCandlestickOptional.get(), symbol);
      this.getCandlestickRepository().save(candlestick);
    } else {
      final Candlestick candlestick = createCandlestick(timeFrame, price, localTimesFrame, symbol);
      this.getCandlestickRepository().save(candlestick);
    }
  }

  private @NotNull Candlestick createCandlestick(final @NotNull TimeFrame timeFrame, final double price, final LocalDateTime localTimesFrame,
      final Symbol symbol) {
    final Candlestick candlestick = new Candlestick();

    candlestick.setTimestamp(localTimesFrame);
    candlestick.setTimeFrame(timeFrame);
    candlestick.setSymbol(symbol);
    candlestick.setHigh(price);
    candlestick.setLow(price);
    candlestick.setOpen(price);
    candlestick.setClose(price);

    candlestick.setAcIndicator(new AcIndicator());
    calculateAcIndicator(timeFrame, candlestick, symbol);
    candlestick.setAdxIndicator(new AdxIndicator());
    return candlestick;
  }

  private @NotNull Candlestick updateCandlestick(final @NotNull TimeFrame timeFrame, final double price, final @NotNull Candlestick candlestick,
      final Symbol symbol) {
    if (candlestick.getHigh() < price) {
      candlestick.setHigh(price);
    } else if (candlestick.getLow() > price) {
      candlestick.setLow(price);
    }
    candlestick.setClose(price);

    calculateAcIndicator(timeFrame, candlestick, symbol);
    return candlestick;
  }

  private void calculateAcIndicator(final @NotNull TimeFrame timeFrame, final @NotNull Candlestick candlestick, final Symbol symbol) {
    // set the MP value
    final double mp = BigDecimal.valueOf(candlestick.getHigh() + candlestick.getLow()).divide(BigDecimal.TWO, 10, RoundingMode.HALF_UP).doubleValue();
    candlestick.getAcIndicator().setMp(mp);

    // get SMA(MP,34)
    final Collection<Double> collectionSmaMp34 = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame)
        .limit(34).map(c -> c.getAcIndicator().getMp()).toList();
    if (collectionSmaMp34.size() != 34) {
      candlestick.getAcIndicator().setAo(null);
      candlestick.getAcIndicator().setAc(null);
    } else {
      final double smaMp34 = MathUtils.getSMA(collectionSmaMp34);

      // get SMA(MP,5)
      final double smaMp5 = MathUtils.getSMA(this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(5)
          .map(c -> c.getAcIndicator().getMp()).toList());

      // get SMA(MP,5) - SMA(MP,34)
      final double ao = BigDecimal.valueOf(smaMp5).subtract(BigDecimal.valueOf(smaMp34)).doubleValue();
      candlestick.getAcIndicator().setAo(ao);

      // get SMA(ao,5)
      final Collection<Double> collectionSmaAo5 = this.getCandlestickRepository()
          .streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(5).filter(c -> Objects.nonNull(c.getAcIndicator().getAo()))
          .map(c -> c.getAcIndicator().getAo()).toList();
      if (collectionSmaAo5.size() != 5) {
        candlestick.getAcIndicator().setAc(null);
      } else {
        final double smaAo5 = MathUtils.getSMA(collectionSmaAo5);

        // get ao - SMA(ao,5)
        final double ac = BigDecimal.valueOf(candlestick.getAcIndicator().getAo()).subtract(BigDecimal.valueOf(smaAo5)).doubleValue();
        candlestick.getAcIndicator().setAc(ac);
      }
    }
  }

  private void calculateAdxIndicator(final @NotNull TimeFrame timeFrame, final @NotNull Candlestick candlestick, final Symbol symbol) {
    // set TR1

  }

  @Override
  public @NotNull Stream<CandlestickIndicatorDto> getLastCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      @NotNull final TimeFrame timeFrame, final int last) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(last)
        .sorted(Comparator.comparing(Candlestick::getTimestamp)).map(candlestickMapper::toDto1);
  }
}

package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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
import lu.forex.system.enums.CandlestickApply;
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
    final Candlestick candlestick = (lastCandlestickOptional.isPresent() && lastCandlestickOptional.get().getTimestamp().equals(localTimesFrame)) ? updateCandlestick(price, lastCandlestickOptional.get()) : createCandlestick(timeFrame, price, localTimesFrame, symbol);
    this.getCandlestickRepository().save(candlestick);

    this.calculatingIndicators(candlestick);
    this.getCandlestickRepository().save(candlestick);
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
    candlestick.setAdxIndicator(new AdxIndicator());

    return candlestick;
  }

  private void calculatingIndicators(final @NotNull Candlestick candlestick) {
    this.calculateAcIndicator(candlestick);
    this.calculateAdxIndicator(candlestick);
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
    final double mp = CandlestickApply.TYPICAL_PRICE.price(candlestick);
    candlestick.getAcIndicator().setMp(mp);

    // get SMA(MP,34)
    final Collection<Double> collectionSmaMp34 = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame)
        .limit(34).map(c -> c.getAcIndicator().getMp()).toList();
    if (collectionSmaMp34.size() != 34) {
      candlestick.getAcIndicator().setAo(null);
      candlestick.getAcIndicator().setAc(null);
    } else {
      final double smaMp34 = MathUtils.getMed(collectionSmaMp34);

      // get SMA(MP,5)
      final double smaMp5 = MathUtils.getMed(this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(5)
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
        final double smaAo5 = MathUtils.getMed(collectionSmaAo5);

        // get ao - SMA(ao,5)
        final double ac = BigDecimal.valueOf(candlestick.getAcIndicator().getAo()).subtract(BigDecimal.valueOf(smaAo5)).doubleValue();
        candlestick.getAcIndicator().setAc(ac);
      }
    }
  }

  private void calculateAdxIndicator(final @NotNull Candlestick candlestick) {
    final Symbol symbol = candlestick.getSymbol();
    final TimeFrame timeFrame = candlestick.getTimeFrame();

    final Collection<Candlestick> collection = this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(2).filter(c -> !c.getId().equals(candlestick.getId())).toList();
    if(collection.size() == 1) {
      final Candlestick lastCandlestick = collection.iterator().next();

      // get TR1
      final double tr1 = MathUtils.getMax(
          BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(candlestick.getLow())).doubleValue(),
          BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(candlestick.getClose())).doubleValue(),
          Math.abs(BigDecimal.valueOf(candlestick.getLow()).subtract(BigDecimal.valueOf(lastCandlestick.getClose())).doubleValue())
      );
      candlestick.getAdxIndicator().setTrOne(tr1);

      // get +DM1
      final double pDm1 = BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(lastCandlestick.getHigh())).compareTo(BigDecimal.valueOf(lastCandlestick.getLow()).subtract(BigDecimal.valueOf(candlestick.getLow()))) > 0  ?
          MathUtils.getMax(BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(lastCandlestick.getHigh())).doubleValue(), 0d) : 0d;
      candlestick.getAdxIndicator().setPDmOne(pDm1);

      // get -DM1
      final double nDm1 = BigDecimal.valueOf(lastCandlestick.getLow()).subtract(BigDecimal.valueOf(candlestick.getLow())).compareTo(BigDecimal.valueOf(candlestick.getHigh()).subtract(BigDecimal.valueOf(lastCandlestick.getHigh()))) > 0  ?
          MathUtils.getMax(BigDecimal.valueOf(lastCandlestick.getLow()).subtract(BigDecimal.valueOf(candlestick.getLow())).doubleValue(), 0d) : 0d;
      candlestick.getAdxIndicator().setNDmOne(nDm1);

      final Collection<Double> collectionTrP = this.getCandlestickRepository()
          .streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(14).filter(c -> Objects.nonNull(c.getAdxIndicator().getTrOne())).map(c -> c.getAdxIndicator().getTrOne()).toList();
      if(collectionTrP.size() == 14) {
        // get TR(P)
        final double trP = MathUtils.getSum(collectionTrP);

        // get +DM(P)
        final double pDmP = MathUtils.getSum(this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(14).map(c -> c.getAdxIndicator().getPDmOne()).toList());

        // get -DM(P)
        final double nDmP = MathUtils.getSum(this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(14).map(c -> c.getAdxIndicator().getNDmOne()).toList());

        // get +DI(P)
        final double pDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(pDmP, trP));

        // get -DI(P)
        final double nDiP = MathUtils.getMultiplication(100, MathUtils.getDivision(nDmP, trP));

        // get DI diff
        final double diDiff = Math.abs(BigDecimal.valueOf(pDiP).subtract(BigDecimal.valueOf(nDiP)).doubleValue());

        // get DI sum
        final double diSum = BigDecimal.valueOf(pDiP).add(BigDecimal.valueOf(nDiP)).doubleValue();

        // get DX
        final double dx = MathUtils.getMultiplication(100, MathUtils.getDivision(diDiff, diSum));
        candlestick.getAdxIndicator().setDx(dx);

        final Collection<Double> collectionDx = this.getCandlestickRepository()
            .streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(14).filter(c -> Objects.nonNull(c.getAdxIndicator().getDx())).map(c -> c.getAdxIndicator().getDx()).toList();
        if(collectionDx.size() == 14) {
          // get ADX
          final double adx = MathUtils.getMed(collectionDx);
          candlestick.getAdxIndicator().setAdx(adx);
        }
      }
    }
  }

  @Override
  public @NotNull Stream<CandlestickIndicatorDto> getLastCandlesticks(final @Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName,
      @NotNull final TimeFrame timeFrame, final int last) {
    final Symbol symbol = this.getSymbolRepository().findFirstByName(symbolName).orElseThrow(() -> new SymbolNotFoundException(symbolName));
    return this.getCandlestickRepository().streamBySymbolAndTimeFrameOrderByTimestampDesc(symbol, timeFrame).limit(last)
        .sorted(Comparator.comparing(Candlestick::getTimestamp)).map(candlestickMapper::toDto1);
  }
}

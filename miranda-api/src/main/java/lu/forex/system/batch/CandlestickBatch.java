//package lu.forex.system.batch;
//
//import jakarta.validation.constraints.NotNull;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.Collection;
//import java.util.Objects;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lu.forex.system.entities.Candlestick;
//import lu.forex.system.entities.Tick;
//import lu.forex.system.enums.TimeFrame;
//import lu.forex.system.models.Ac;
//import lu.forex.system.services.CandlestickService;
//import org.springframework.stereotype.Component;
//
//@Component
//@Getter(AccessLevel.PRIVATE)
//@AllArgsConstructor
//public class CandlestickBatch {
//
//  private final CandlestickService candlestickService;
//
//  public void processingNewTick(final @NotNull Tick tick) {
//    // Use bid price for generate candlesticks and for make statistic calculations
//    // for (TimeFrame timeFrame : TimeFrame.values()) {
//      this.getCandlestickService().createOrUpdateCandlestickByPrice(tick.getSymbol(), tick.getTimestamp(), TimeFrame.M15, tick.getBid());
//    //}
//  }
//
//  public @NotNull Ac processingIndicatorsAc(final @NotNull Candlestick candlestick) {
//    final Ac ac = new Ac(candlestick.getClose());
//
//    // set the MP value
//    ac.setMp(BigDecimal.valueOf(candlestick.getHigh() + candlestick.getLow()).divide(BigDecimal.TWO, 10, RoundingMode.HALF_UP).doubleValue());
//
//    // get SMA(MP,34)
//    final Collection<BigDecimal> collectionSmaMp34 = this.getCandlestickService()
//        .getLastCandlesticks(candlestick.getSymbol(), candlestick.getTimeFrame(), 34).map(candlestickIndicatorsDto -> BigDecimal.valueOf(candlestickIndicatorsDto.ac().getMp())).toList();
//    if (collectionSmaMp34.size() < 34) {
//      ac.setAo(null);
//      ac.setAcValue(null);
//      return ac;
//    }
//    final BigDecimal smaMp34 = collectionSmaMp34.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(34), 10, RoundingMode.HALF_UP);
//
//    // get SMA(MP,5)
//    final BigDecimal smaMp5 = this.getCandlestickService()
//        .getLastCandlesticks(candlestick.getSymbol(), candlestick.getTimeFrame(), 5).map(candlestickIndicatorsDto -> BigDecimal.valueOf(candlestickIndicatorsDto.ac().getMp())).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(5), 10, RoundingMode.HALF_UP);
//
//    // get SMA(MP,5) - SMA(MP,34)
//    final BigDecimal ao = smaMp5.subtract(smaMp34);
//    ac.setAo(ao.doubleValue());
//
//    // get SMA(ao,5)
//    final Collection<Double> collectionSmaAo5 = this.getCandlestickService().getLastCandlesticks(candlestick.getSymbol(), candlestick.getTimeFrame(), 5).filter(candlestickIndicatorsDto -> Objects.nonNull(candlestickIndicatorsDto.ac().getAo())).map(candlestickIndicatorsDto -> candlestickIndicatorsDto.ac().getAo()).toList();
//    if (collectionSmaAo5.size() < 5) {
//      ac.setAcValue(null);
//      return ac;
//    }
//    final BigDecimal smaAo5 = collectionSmaAo5.stream().map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(5), 10, RoundingMode.HALF_UP);
//
//    // get ao - SMA(ao,5)
//    final BigDecimal acValue = BigDecimal.valueOf(ac.getAo()).subtract(smaAo5);
//    ac.setAcValue(acValue.doubleValue());
//
//    return ac;
//  }
//
//}

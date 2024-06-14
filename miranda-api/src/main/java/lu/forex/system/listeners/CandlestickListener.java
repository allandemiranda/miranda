package lu.forex.system.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.SignalIndicator;
import org.springframework.stereotype.Component;

@Component
public class CandlestickListener {

  @PrePersist
  public void prePersist(final @NotNull Candlestick candlestick) {
    candlestick.setSignalIndicator(this.getSignalIndicator(candlestick.getTechnicalIndicators()));
  }

  @PreUpdate
  public void preUpdate(final @NotNull Candlestick candlestick) {
    final CandlestickBody body = candlestick.getBody();
    final double price = body.getClose();
    if (price > body.getHigh()) {
      body.setHigh(price);
    }
    if (price < body.getLow()) {
      body.setLow(price);
    }
    candlestick.setSignalIndicator(this.getSignalIndicator(candlestick.getTechnicalIndicators()));
  }

  @NotNull
  private SignalIndicator getSignalIndicator(final @NotNull Collection<TechnicalIndicator> technicalIndicators) {
    final int power = technicalIndicators.stream().mapToInt(ti -> switch (ti.getSignal()) {
      case NEUTRAL -> 0;
      case BULLISH -> 1;
      case BEARISH -> -1;
    }).sum();
    if (Math.abs(power) >= (technicalIndicators.size() / 2)) {
      if (power < 0) {
        return SignalIndicator.BEARISH;
      } else if (power > 0) {
        return SignalIndicator.BULLISH;
      }
    }
    return SignalIndicator.NEUTRAL;
  }

}

package lu.forex.system.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.enums.SignalIndicator;
import org.springframework.stereotype.Component;

@Component
public class CandlestickListener {

  @PrePersist
  public void prePersist(final @NotNull Candlestick candlestick) {
    // candlestick.setSignalIndicator(OrderUtils.getSignalIndicator(candlestick.getTechnicalIndicators()));
    candlestick.setSignalIndicator(SignalIndicator.NEUTRAL);
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
    // candlestick.setSignalIndicator(OrderUtils.getSignalIndicator(candlestick.getTechnicalIndicators()));
  }

}

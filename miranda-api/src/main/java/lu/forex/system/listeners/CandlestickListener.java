package lu.forex.system.listeners;

import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import lu.forex.system.entities.Candlestick;
import lu.forex.system.entities.CandlestickBody;
import org.springframework.stereotype.Component;

@Component
public class CandlestickListener {

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
  }

}

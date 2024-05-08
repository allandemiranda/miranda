package lu.forex.system.listeners;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.entities.Tick;
import lu.forex.system.services.CandlestickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Getter(AccessLevel.PRIVATE)
public class TickListener {

  private final CandlestickService candlestickService;

  @Autowired
  public TickListener(final @Lazy CandlestickService candlestickService) {
    this.candlestickService = candlestickService;
  }

  @PrePersist
  public void prePersist(final @NotNull Tick tick) {
    this.getCandlestickService().createOrUpdateCandlestick(tick.getSymbol(), tick.getTimestamp(), tick.getAsk());
  }

}

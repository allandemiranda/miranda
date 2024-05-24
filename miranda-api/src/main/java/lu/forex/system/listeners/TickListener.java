package lu.forex.system.listeners;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.entities.Tick;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.services.CandlestickService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Getter(AccessLevel.PRIVATE)
public class TickListener {

  private final CandlestickService candlestickService;

  public TickListener(final @Lazy CandlestickService candlestickService) {
    this.candlestickService = candlestickService;
  }

  @PrePersist
  @Transactional()
  public void prePersist(final @NotNull Tick tick) {
    // Use bid price for generate candlesticks and for make statistic calculations
    // for (TimeFrame timeFrame : TimeFrame.values()) {
    this.getCandlestickService().createOrUpdateCandlestickByPrice(tick.getSymbol().getName(), tick.getTimestamp(), TimeFrame.M15, tick.getBid());
    //}
  }

}

package lu.forex.system.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lu.forex.system.entities.Tick;
import org.springframework.stereotype.Component;

@Component
public class TickListener {

  @PrePersist
  @PreUpdate
  public void prePersistOrUpdate(@NotNull Tick tick) {
    final double spread = this.getSpread(tick);
    tick.setSpread(spread);
  }

  private double getSpread(@NotNull Tick tick) {
    final BigDecimal price = BigDecimal.valueOf(tick.getAsk()).subtract(BigDecimal.valueOf(tick.getBid()));
    final BigDecimal pow = BigDecimal.valueOf(Math.pow(10, tick.getSymbol().getDigits()));
    return price.multiply(pow).doubleValue();
  }

}

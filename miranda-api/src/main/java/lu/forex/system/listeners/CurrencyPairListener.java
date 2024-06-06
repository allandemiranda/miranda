package lu.forex.system.listeners;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotNull;
import lu.forex.system.entities.CurrencyPair;
import org.springframework.stereotype.Component;

@Component
public class CurrencyPairListener {

  @PrePersist
  public void prePersist(final @NotNull CurrencyPair currencyPair) {
    currencyPair.setName(this.getName(currencyPair));
  }

  private @NotNull String getName(final @NotNull CurrencyPair currencyPair) {
    return currencyPair.getBase().getName().concat(currencyPair.getQuote().getName());
  }
}

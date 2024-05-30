package lu.forex.system.utils;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.experimental.UtilityClass;
import lu.forex.system.entities.Candlestick;

@UtilityClass
public class BatchUtils {

  public static Candlestick getCurrentCandlestick(final @NotNull List<Candlestick> candlesticksDesc) {
    return candlesticksDesc.getFirst();
  }

  public static Candlestick getLastCandlestick(final @NotNull List<Candlestick> candlesticksDesc) {
    return candlesticksDesc.get(1);
  }

}

package lu.forex.system.dtos;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import lu.forex.system.entities.CandlestickBody;
import lu.forex.system.entities.CandlestickHeader;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.Indicator;

/**
 * DTO for {@link lu.forex.system.entities.Candlestick}
 */
public record CandlestickResponseDto(CandlestickHeader header, CandlestickBody body, Set<MovingAverage> movingAverages,
                                     Map<Indicator, TechnicalIndicator> indicators) implements Serializable {

  @Serial
  private static final long serialVersionUID = 7172155089598071933L;
}
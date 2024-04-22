package lu.forex.system.dtos;

import java.time.DayOfWeek;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
@ToString
public class SwapDto {

  //@formatter:off
  private final double longTax;
  private final double shortTax;
  private final @NonNull DayOfWeek rateTriple;
  //@formatter:on

}

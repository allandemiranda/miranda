package lu.forex.system.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lu.forex.system.enums.Currency;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
@ToString
public class SymbolDto {

  //@formatter:off
  private final @NonNull @NotBlank String name;
  private final @NonNull Currency margin;
  private final @NonNull Currency profit;
  private final int digits;
  private final @NonNull SwapDto swap;
  //@formatter:on

}

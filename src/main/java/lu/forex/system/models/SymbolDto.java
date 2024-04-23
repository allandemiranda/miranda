package lu.forex.system.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lu.forex.system.enums.Currency;

@RequiredArgsConstructor
@Getter
@ToString
public class SymbolDto {

  //@formatter:off
  private final UUID id;
  private final @NonNull @NotBlank @Size(max = 6, min = 6) String name;
  private final @NonNull Currency margin;
  private final @NonNull Currency profit;
  private final int digits;
  private final @NonNull SwapDto swap;
  //@formatter:on

}

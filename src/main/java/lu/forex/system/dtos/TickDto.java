package lu.forex.system.dtos;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public class TickDto {

  //@formatter:off
  private final @NonNull LocalDateTime dateTime;
  private final double bid;
  private final double ask;
  private final @NonNull SymbolDto symbol;
  private final @NonNull @NotBlank String timeFrame;
  //@formatter:on

}

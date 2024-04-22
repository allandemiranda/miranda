package lu.forex.system.dtos;

import java.time.LocalDateTime;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public record TickDto(@NonNull LocalDateTime dateTime, double bid, double ask, @NonNull SymbolDto symbol) {

}

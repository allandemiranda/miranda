package lu.forex.system.models;

import java.time.LocalDateTime;
import lombok.NonNull;

public record TickDto(@NonNull LocalDateTime dateTime, double bid, double ask, @NonNull SymbolDto symbol) {

}

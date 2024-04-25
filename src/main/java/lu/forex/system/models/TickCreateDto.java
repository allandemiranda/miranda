package lu.forex.system.models;

import java.time.LocalDateTime;
import lombok.NonNull;

public record TickCreateDto(@NonNull LocalDateTime dateTime, double bid, double ask) {

}

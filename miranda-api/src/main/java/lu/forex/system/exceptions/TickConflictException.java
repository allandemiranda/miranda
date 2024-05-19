package lu.forex.system.exceptions;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.CONFLICT)
public class TickConflictException extends RuntimeException {

  public TickConflictException(final String symbolName, final @NotNull LocalDateTime currentTimestamp, final @NotNull LocalDateTime lastTimestamp) {
    super(String.format("Tick %s %s is older that last one %s", symbolName, currentTimestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        lastTimestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
  }
}

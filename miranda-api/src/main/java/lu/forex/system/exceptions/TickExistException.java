package lu.forex.system.exceptions;

import jakarta.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.entities.Symbol;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TickExistException extends RuntimeException {

  public TickExistException(final @NotNull TickCreateDto tickCreateDto, final @NotNull Symbol symbol) {
    super(String.format("Tick %s - %s exist", symbol.getName(), tickCreateDto.timestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
  }
}

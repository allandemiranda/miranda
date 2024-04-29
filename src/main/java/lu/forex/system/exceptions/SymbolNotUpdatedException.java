package lu.forex.system.exceptions;

import lombok.experimental.StandardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@StandardException
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SymbolNotUpdatedException extends RuntimeException {

  public SymbolNotUpdatedException(final String symbolName) {
    super(String.format("Symbol %s not updated", symbolName));
  }
}

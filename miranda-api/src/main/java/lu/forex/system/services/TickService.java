package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.ResponseTickDto;
import org.springframework.transaction.annotation.Transactional;

public interface TickService {

  @Transactional()
  @Nonnull
  ResponseTickDto addTickBySymbolName(@NotNull NewTickDto tickDto, @NotNull @NotBlank String symbolName);

  @Transactional(readOnly = true)
  @Nonnull
  Collection<@NotNull ResponseTickDto> getTicksBySymbolName(@Nonnull @NotBlank @Size(min = 6, max = 6) String symbolName);
}

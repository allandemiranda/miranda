package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface TickService {

  @Nonnull
  Collection<@NotNull TickResponseDto> getTicks(@Nonnull String symbolName);

  @Nonnull
  TickResponseDto addTick(@Nonnull TickCreateDto tickCreateDto, @Nonnull String symbolName);
}

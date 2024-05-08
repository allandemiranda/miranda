package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface TickService {

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  @Nonnull
  Collection<@NotNull TickResponseDto> getTicks(@Nonnull String symbolName);


  @Transactional(propagation = Propagation.REQUIRED)
  @Nonnull
  TickResponseDto addTick(@Nonnull TickCreateDto tickCreateDto, @Nonnull String symbolName);
}

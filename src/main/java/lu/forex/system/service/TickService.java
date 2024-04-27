package lu.forex.system.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.stereotype.Service;

@Service
public interface TickService {

  @NotNull
  TickDto save(final @NotNull TickCreateDto tickCreateDto, final @NotNull @NotBlank String symbolName);

  @NotNull
  Collection<TickDto> findAllBySymbolNameOrderByTimestampTimestampDesc(final @NotNull @NotBlank String symbolName);
}

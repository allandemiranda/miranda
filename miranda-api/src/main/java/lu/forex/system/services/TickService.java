package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface TickService {

  @Transactional()
  @Nonnull
  TickDto addTickBySymbol(final @NotNull NewTickDto newTickDto, final @NotNull SymbolDto symbolDto);

  @Transactional(readOnly = true)
  @NotNull
  List<@NotNull TickDto> getTicksBySymbolName(final @NotNull @NotBlank String symbolName);

  @Transactional(readOnly = true)
  @NotNull
  Collection<@NotNull TickDto> getTicksBySymbolNameNotOrdered(final @NotNull @NotBlank String symbolName);

  @Transactional(readOnly = true)
  @NotNull
  Optional<@NotNull TickDto> getLestTickBySymbolName(final @NotNull @NotBlank String symbolName);

  @Async
  void readPreDataBase(final @NotNull SymbolDto symbolDto, final @NotNull File inputFile);
}

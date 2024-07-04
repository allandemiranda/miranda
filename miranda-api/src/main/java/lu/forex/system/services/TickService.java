package lu.forex.system.services;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface TickService {

  @Transactional
  @Nonnull
  TickDto addTickBySymbol(final @NotNull NewTickDto newTickDto, final @NotNull SymbolDto symbolDto);

  @Transactional(readOnly = true)
  @NotNull
  List<@NotNull TickDto> getTicksBySymbolName(final @NotNull @NotBlank String symbolName);

  @Transactional()
  void addLastTickPerformed(final @NotNull TickDto tickDto);

  @Transactional
  @NotNull TickDto @NotNull [] batchReadPreDataBase(final @NotNull SymbolDto symbolDto, final @NotNull File inputFile);
}

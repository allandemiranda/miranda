package lu.forex.system.providers;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.repositories.SymbolRepository;
import lu.forex.system.services.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter(AccessLevel.PRIVATE)
public class SymbolProvider implements SymbolService {

  private final SymbolRepository symbolRepository;
  private final SymbolMapper symbolMapper;

  @Autowired
  public SymbolProvider(final SymbolRepository symbolRepository, final SymbolMapper symbolMapper) {
    this.symbolRepository = symbolRepository;
    this.symbolMapper = symbolMapper;
  }

  @Override
  public @Nonnull Collection<@NotNull SymbolResponseDto> getSymbols() {
    return this.getSymbolRepository().findAll().stream().map(symbolMapper::toDto).toList();
  }

  @Override
  public @Nonnull Optional<@NotNull SymbolResponseDto> getSymbol(final @Nonnull String name) {
    return this.getSymbolRepository().findFirstByNameOrderByNameAsc(name).map(symbolMapper::toDto);
  }

  @Override
  public @Nonnull SymbolResponseDto addSymbol(final @Nonnull SymbolCreateDto symbolCreateDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolCreateDto);
    final Symbol saved = this.getSymbolRepository().save(symbol);
    return this.getSymbolMapper().toDto(saved);
  }

  @Override
  @Transactional
  public void updateSymbol(final @Nonnull SymbolUpdateDto symbolUpdateDto, final @Nonnull String name) {
    this.getSymbolRepository()
        .updateDigitsAndSwapLongAndSwapShortByName(symbolUpdateDto.digits(), symbolUpdateDto.swapLong(), symbolUpdateDto.swapShort(), name);
  }

  @Override
  @Transactional
  public void deleteSymbol(final @Nonnull String name) {
    this.getSymbolRepository().deleteByName(name);
  }
}

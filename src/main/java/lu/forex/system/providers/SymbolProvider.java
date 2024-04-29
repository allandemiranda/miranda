package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.exceptions.SymbolNotFoundException;
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
  public @NotNull Collection<@NotNull SymbolResponseDto> getSymbols() {
    return this.getSymbolRepository().findAll().stream().map(symbolMapper::toDto).toList();
  }

  @Override
  public @NotNull Optional<@NotNull SymbolResponseDto> getSymbol(final @NotNull String name) {
    return this.getSymbolRepository().findFirstByNameOrderByNameAsc(name).map(symbolMapper::toDto);
  }

  @Override
  public @NotNull SymbolResponseDto addSymbol(final @NotNull SymbolCreateDto symbolCreateDto) {
    final Symbol symbol = symbolMapper.toEntity(symbolCreateDto);
    final Symbol saved = this.getSymbolRepository().save(symbol);
    return symbolMapper.toDto(saved);
  }

  @Override
  @Transactional
  public @NotNull SymbolResponseDto updateSymbol(final @NotNull SymbolUpdateDto symbolUpdateDto, final @NotNull String name) {
    final Symbol symbol = this.getSymbolRepository().findFirstByNameOrderByNameAsc(name).orElseThrow(() -> new SymbolNotFoundException(name));
    symbol.setDigits(symbolUpdateDto.digits());
    symbol.setSwapShort(symbolUpdateDto.swapShort());
    symbol.setSwapLong(symbolUpdateDto.swapLong());
    return symbolMapper.toDto(this.getSymbolRepository().save(symbol));
  }

  @Override
  @Transactional
  public boolean deleteSymbol(final @NotNull String name) {
    return this.getSymbolRepository().deleteByName(name) > 0L;
  }
}

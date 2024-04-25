package lu.forex.system.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.entities.Symbol;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.models.SymbolDto;
import lu.forex.system.models.SymbolUpdateDto;
import lu.forex.system.repositories.SymbolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
public class SymbolService {

  private final SymbolRepository symbolRepository;

  @Autowired
  public SymbolService(final SymbolRepository symbolRepository) {
    this.symbolRepository = symbolRepository;
  }

  public Collection<SymbolDto> findAll() {
    return this.getSymbolRepository().findAll().stream().map(SymbolMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
  }

  public Optional<SymbolDto> findByName(final @NonNull @NotBlank String name) {
    final Symbol symbol = this.getSymbolRepository().findByName(name);
    if (Objects.isNull(symbol)) {
      return Optional.empty();
    } else {
      return Optional.of(SymbolMapper.toDto(symbol));
    }
  }

  @Transactional
  public SymbolDto save(final @NonNull SymbolDto symbolDto) {
    final Symbol symbolTmp = SymbolMapper.toEntity(symbolDto);
    final Symbol symbol = this.getSymbolRepository().save(symbolTmp);
    return SymbolMapper.toDto(symbol);
  }

  @Transactional
  public Optional<SymbolDto> updateSymbolByName(final @NonNull SymbolUpdateDto symbolUpdateDto, final @NonNull @NotBlank String name) {
    final Symbol symbol = this.getSymbolRepository().findByName(name);
    if (Objects.nonNull(symbol)) {
      symbol.setDigits(symbolUpdateDto.digits());
      symbol.setSwapLong(symbolUpdateDto.swapLong());
      symbol.setSwapShort(symbolUpdateDto.swapShort());
      final Symbol updatedSymbol = this.getSymbolRepository().save(symbol);
      return Optional.of(SymbolMapper.toDto(updatedSymbol));
    } else {
      return Optional.empty();
    }
  }

  @Transactional
  public boolean deleteByName(final @NonNull @NotBlank String name) {
    this.getSymbolRepository().deleteSymbolByName(name);
    return Optional.ofNullable(this.getSymbolRepository().findByName(name)).isPresent();
  }

}

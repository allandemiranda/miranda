package lu.forex.system.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.entities.Swap;
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
  private final SymbolMapper symbolMapper;

  @Autowired
  public SymbolService(final SymbolRepository symbolRepository, final SymbolMapper symbolMapper) {
    this.symbolRepository = symbolRepository;
    this.symbolMapper = symbolMapper;
  }

  public List<SymbolDto> findAll() {
    return this.getSymbolRepository().findAll().stream().map(this.getSymbolMapper()::toDto).toList();
  }

  public Optional<SymbolDto> findByName(final @NonNull @NotBlank String name) {
    final Symbol symbol = this.getSymbolRepository().findByName(name);
    if (Objects.isNull(symbol)) {
      return Optional.empty();
    } else {
      return Optional.of(this.getSymbolMapper().toDto(symbol));
    }
  }

  @Transactional
  public SymbolDto save(final @NonNull SymbolDto symbolDto) {
    final Symbol symbolTmp = this.getSymbolMapper().toEntity(symbolDto);
    final Symbol symbol = this.getSymbolRepository().save(symbolTmp);
    return this.getSymbolMapper().toDto(symbol);
  }

  @Transactional
  public SymbolDto updateSymbolByName(final @NonNull SymbolUpdateDto symbolUpdateDto, final @NonNull String name) {
    final Swap swap = this.getSymbolRepository().findByName(name).getSwap();
    swap.setLongTax(symbolUpdateDto.swap().longTax());
    swap.setShortTax(symbolUpdateDto.swap().shortTax());
    swap.setRateTriple(symbolUpdateDto.swap().rateTriple());

    final Symbol symbol = this.getSymbolRepository().findByName(name);
    symbol.setMargin(symbolUpdateDto.margin());
    symbol.setProfit(symbolUpdateDto.profit());
    symbol.setDigits(symbolUpdateDto.digits());
    symbol.setSwap(swap);

    return this.getSymbolMapper().toDto(symbol);
  }

  @Transactional
  public void deleteByName(final @NonNull String name) {
    this.getSymbolRepository().deleteSymbolByName(name);
  }

}

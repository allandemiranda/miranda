package lu.forex.system.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.models.SymbolDto;
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

  @Transactional
  public SymbolDto save(final @NonNull SymbolDto symbol) {
    return this.getSymbolMapper().toDto(this.getSymbolRepository().save(this.getSymbolMapper().toEntity(symbol)));
  }

  @Transactional
  public void delete(final @NonNull UUID id) {
    this.getSymbolRepository().deleteById(id);
  }

  public List<SymbolDto> findAll() {
    return this.getSymbolRepository().findAll().stream().map(this.getSymbolMapper()::toDto).toList();
  }

  public SymbolDto findByName(final @NonNull @NotBlank String name) {
    return this.getSymbolMapper().toDto(this.getSymbolRepository().findByName(name));
  }

}

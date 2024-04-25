package lu.forex.system.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lu.forex.system.entities.Tick;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.models.SymbolDto;
import lu.forex.system.models.TickCreateDto;
import lu.forex.system.models.TickDto;
import lu.forex.system.repositories.TickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
public class TickService {

  private final TickRepository tickRepository;

  @Autowired
  public TickService(final TickRepository tickRepository) {
    this.tickRepository = tickRepository;
  }

  public Collection<Tick> getTicksBySymbol(final @NonNull @NotBlank String symbolName) {
    return this.getTickRepository().findBySymbolNameOrderByDateTimeAsc(symbolName);
  }

  @Transactional
  public TickDto save(final @NonNull @NotBlank TickCreateDto tickCreateDto, final @NonNull SymbolDto symbolDto) {
    final TickDto tickDto = new TickDto(tickCreateDto.dateTime(), tickCreateDto.bid(), tickCreateDto.ask(), symbolDto);
    final Tick tickTmp = TickMapper.toEntity(tickDto);
    final Tick tick = this.getTickRepository().save(tickTmp);
    return TickMapper.toDto(tick);
  }


}

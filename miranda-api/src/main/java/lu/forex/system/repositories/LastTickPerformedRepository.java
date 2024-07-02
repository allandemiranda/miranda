package lu.forex.system.repositories;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.entities.Tick;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class LastTickPerformedRepository {

  private final TickRepository tickRepository;
  private final SymbolRepository symbolRepository;
  private final ConcurrentHashMap<UUID, Optional<Tick>> lastTickMap = new ConcurrentHashMap<>();

  @PostConstruct
  private void init() {
    this.getSymbolRepository().findAll().forEach(symbol -> {
      final Optional<Tick> getLast = this.getTickRepository().getFirstBySymbol_IdOrderByTimestampDesc(symbol.getId());
      this.getLastTickMap().put(symbol.getId(), getLast);
    });
  }

  public @NotNull Optional<Tick> getLastTick(@NotNull final UUID symbolId) {
    return this.getLastTickMap().getOrDefault(symbolId, Optional.empty());
  }

  public void addLastTick(@NotNull final Tick tick) {
    this.getLastTickMap().remove(tick.getSymbol().getId());
    this.getLastTickMap().put(tick.getSymbol().getId(), Optional.of(tick));
  }
}

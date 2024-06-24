package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import lu.forex.system.exceptions.TickTimestampOlderException;
import lu.forex.system.mappers.SymbolMapper;
import lu.forex.system.mappers.TickMapper;
import lu.forex.system.repositories.TickRepository;
import lu.forex.system.services.TickService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class TickProvider implements TickService {

  private final TickRepository tickRepository;
  private final TickMapper tickMapper;
  private final SymbolMapper symbolMapper;

  @NotNull
  @Override
  public TickDto addTickBySymbol(@NotNull final NewTickDto newTickDto, final @NotNull SymbolDto symbolDto) {
    final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
    final boolean valid = this.getTickRepository().getFirstBySymbol_IdOrderByTimestampDesc(symbol.getId())
        .map(tick -> tick.getTimestamp().isBefore(newTickDto.timestamp())).orElse(true);
    if (valid) {
      final Tick tick = this.getTickMapper().toEntity(newTickDto, symbol);
      final Tick saved = this.getTickRepository().save(tick);
      return this.getTickMapper().toDto(saved);
    } else {
      throw new TickTimestampOlderException(newTickDto.timestamp(), symbolDto.currencyPair().name());
    }
  }

  @Override
  public @NotNull List<@NotNull TickDto> getTicksBySymbolName(final @NotNull String symbolName) {
    return this.getTickRepository().findBySymbol_CurrencyPair_NameOrderByTimestampAsc(symbolName).stream().map(this.getTickMapper()::toDto).toList();
  }

  @Override
  public @NotNull Collection<@NotNull TickDto> getTicksBySymbolNameNotOrdered(final @NotNull String symbolName) {
    return  this.getTickRepository().findBySymbol_CurrencyPair_Name(symbolName).parallelStream().map(this.getTickMapper()::toDto).toList();
  }

  @Override
  public @NotNull Optional<@NotNull TickDto> getLestTickBySymbolName(final @NotNull String symbolName) {
    final List<Tick> collection = this.getTickRepository().findBySymbolNameOrderByTimestampDescLimitTwo(symbolName);
    if (collection.size() == 2) {
      final Tick tick = collection.getLast();
      final TickDto tickDto = this.getTickMapper().toDto(tick);
      return Optional.of(tickDto);
    } else {
      return Optional.empty();
    }
  }

  @SneakyThrows
  @Async
  @Override
  public void readPreDataBase(final @NotNull SymbolDto symbolDto, final @NotNull File inputFile) {
    log.info(" Starting readPreDataBase({})", inputFile.getAbsolutePath());
    final var symbol = this.getSymbolMapper().toEntity(symbolDto);
    try (final var fileReader = new FileReader(inputFile); final var csvParser = CSVFormat.TDF.builder().build().parse(fileReader)) {

      final double[] tmpBidAsk = new double[]{0D, 0D};

      final var ticks = StreamSupport.stream(csvParser.spliterator(), true)
        .map(csvRecord -> {
          try {
            return this.getDataTick(csvRecord, symbol);
          } catch (Exception e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(Tick::getTimestamp, tick -> tick, (o, o2) -> o.getBid() > 0D && o.getAsk() > 0D ? o : o2)).values().stream()
        .sorted(Comparator.comparing(Tick::getTimestamp))
        .map(tick -> {
          if(tick.getBid() > 0D) {
            tmpBidAsk[0] = tick.getBid();
          }
          if(tick.getAsk() > 0D) {
            tmpBidAsk[1] = tick.getAsk();
          }
          if(tmpBidAsk[0] > 0D && tmpBidAsk[1] > 0D) {
            if (tick.getBid() == 0D) {
              tick.setBid(tmpBidAsk[0]);
            }
            if (tick.getAsk() == 0D) {
              tick.setAsk(tmpBidAsk[1]);
            }
          }
          return tick;
        })
       .filter(tick -> tick.getBid() > 0D && tick.getAsk() > 0D && tick.getAsk() >= tick.getBid())
       .toList();

      this.getTickRepository().saveAll(ticks);
    }

    log.info(" End readPreDataBase()");
  }

  @Override
  public @NotNull TickDto getFirstOrNextTick(final @NotNull UUID symbolId, final @NotNull LocalDateTime timestamp) {
    final Tick firstAndNextTick = this.getTickRepository().findFirstBySymbol_IdAndTimestampGreaterThanEqualOrderByTimestampAsc(symbolId, timestamp).orElseThrow();
    return this.getTickMapper().toDto(firstAndNextTick);
  }

  private @NotNull Tick getDataTick(final @NotNull CSVRecord csvRecord, final @NotNull Symbol symbol) {
    final var tick = new Tick();
    tick.setSymbol(symbol);

    final var date = csvRecord.get(0).replace(".", "-");
    final var time = csvRecord.get(1);
    final var dataTime = date.concat("T").concat(time);
    final var localDateTime = LocalDateTime.parse(dataTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    final var bid = csvRecord.get(2).isEmpty() ? 0D : Double.parseDouble(csvRecord.get(2));
    final var ask = csvRecord.get(3).isEmpty() ? 0D : Double.parseDouble(csvRecord.get(3));

    tick.setTimestamp(localDateTime);
    tick.setAsk(ask);
    tick.setBid(bid);

    return tick;
  }

}

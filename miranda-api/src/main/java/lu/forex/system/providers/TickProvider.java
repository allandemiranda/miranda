package lu.forex.system.providers;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
import lu.forex.system.repositories.LastTickPerformedRepository;
import lu.forex.system.services.TickService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class TickProvider implements TickService {

  public static final String TIME_CONCAT = "T";
  public static final String TARGET = ".";
  public static final String REPLACEMENT = "-";
  private final TickRepository tickRepository;
  private final TickMapper tickMapper;
  private final SymbolMapper symbolMapper;
  private final LastTickPerformedRepository lastTickPerformedRepository;

  @NotNull
  @Override
  public TickDto addTickBySymbol(@NotNull final NewTickDto newTickDto, final @NotNull SymbolDto symbolDto) {
    final boolean valid = this.getLastTickPerformedRepository().getLastTick(symbolDto.id()).map(tick -> tick.getTimestamp().isBefore(newTickDto.timestamp())).orElse(true);
    if (valid) {
      final Symbol symbol = this.getSymbolMapper().toEntity(symbolDto);
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
  public void addLastTickPerformed(final @NotNull TickDto tickDto) {
    final var tick = this.getTickMapper().toEntity(tickDto);
    this.getLastTickPerformedRepository().addLastTick(tick);
  }

  @Override
  public @NotNull TickDto @NotNull [] batchReadPreDataBase(final @NotNull SymbolDto symbolDto, final @NotNull File inputFile) {
    log.info("Reading file to generate ticks");
    final var symbol = this.getSymbolMapper().toEntity(symbolDto);
    Collection<Tick> ticks;
    try (final var fileReader = new FileReader(inputFile); final var csvParser = CSVFormat.TDF.builder().build().parse(fileReader)) {
      final double[] tmpBidAsk = new double[]{0D, 0D};
      ticks = StreamSupport.stream(csvParser.spliterator(), false)
        .map(csvRecord -> {
          try {
            return this.getDataTick(csvRecord, symbol);
          } catch (Exception e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
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
       .collect(Collectors.toMap(Tick::getTimestamp, tick -> tick, (t, t2) -> t)).values();
    } catch (IOException e) {
      log.error("Error read file", e);
      return new TickDto[0];
    }
    log.info("Saving ticks");
    final List<Tick> saved = this.getTickRepository().saveAllAndFlush(ticks);
    log.info("Sorting ticks");
    saved.sort(Comparator.comparing(Tick::getTimestamp));
    log.info("Mapping ticks");
    final TickDto[] tickDtos = new TickDto[saved.size()];
    IntStream.range(0, saved.size()).parallel().forEach(i -> tickDtos[i] = this.getTickMapper().toDto(saved.get(i)));
    return tickDtos;
  }

  private @NotNull Tick getDataTick(final @NotNull CSVRecord csvRecord, final @NotNull Symbol symbol) {
    final var tick = new Tick();
    tick.setSymbol(symbol);

    final var date = csvRecord.get(0).replace(TARGET, REPLACEMENT);
    final var time = csvRecord.get(1);
    final var dataTime = date.concat(TIME_CONCAT).concat(time);
    final var localDateTime = LocalDateTime.parse(dataTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    final var bid = csvRecord.get(2).isEmpty() ? 0D : Double.parseDouble(csvRecord.get(2));
    final var ask = csvRecord.get(3).isEmpty() ? 0D : Double.parseDouble(csvRecord.get(3));

    tick.setTimestamp(localDateTime);
    tick.setAsk(ask);
    tick.setBid(bid);

    return tick;
  }
}

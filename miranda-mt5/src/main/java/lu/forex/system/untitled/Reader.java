package lu.forex.system.untitled;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

@Slf4j
@Getter
public class Reader {

  private static final String CSV = " - Copia (2e5m).csv";
  private final Collection<Order> orderRepository = new ArrayList<>();
  private final List<SimpleEntry<LocalDateTime, Double>> balanceHistoric = new ArrayList<>();

  @SneakyThrows
  public void start(final String symbol) {
    final String root = "C:\\Users\\AllanDeMirandaSilva\\Downloads\\processing\\";
    final var fileName = root + symbol + CSV;
    log.info("[{}] Reading file {}",LocalDateTime.now(),fileName);
    final var inputFile = new File(fileName);
    final var bidH = new AtomicReference<>(0D);
    final var askH = new AtomicReference<>(0D);
    final var lastUpdate = new AtomicReference<>(LocalDateTime.MIN);

    var numLines = new AtomicLong(0L);
    try (final var lines = Files.lines(inputFile.toPath())) {
      numLines.set(lines.count() - 1L);
    }
    var lineNow = new AtomicLong(1);
    var lastPercentage = new AtomicLong(-1);

    log.info("[{}] Starting...", LocalDateTime.now());
    try (final var fileReader = new FileReader(inputFile); final var csvParser = CSVFormat.TDF.builder().build().parse(fileReader)) {
      StreamSupport.stream(csvParser.spliterator(), false).skip(1).map(this::getDataTick).forEachOrdered(tick -> {
        final long percentage = lineNow.addAndGet(1) * 100L / numLines.get();
        if (percentage != lastPercentage.get()) {
          log.info("[{}] {}% read", LocalDateTime.now(), percentage);
          lastPercentage.set(percentage);
        }
        if (tick.getBid() > 0D) {
          bidH.set(tick.getBid());
        }
        if (tick.getAsk() > 0D) {
          askH.set(tick.getAsk());
        }
        if ((bidH.get() > 0D) && (askH.get() > 0D) && tick.getTime().isAfter(lastUpdate.get())) {
          tick.setBid(bidH.get());
          tick.setAsk(askH.get());
          this.sent(symbol, tick);
          lastUpdate.set(tick.getTime());
        }
      });
    }

    log.info("[{}] Writing balance...", LocalDateTime.now());
    try (final FileWriter fileWriter = new FileWriter(new File(new File(root), "balance.csv"));
        final CSVWriter csvWriter = new CSVWriter(fileWriter)) {
      this.getBalanceHistoric().stream().map(entry -> new String[]{entry.getKey().toString(), String.valueOf(entry.getValue())})
          .forEachOrdered(csvWriter::writeNext);
    }
  }

  private @NotNull Tick getDataTick(final @NotNull CSVRecord csvRecord) {
    final var date = csvRecord.get(0).replace(".", "-");
    final var time = csvRecord.get(1);
    final var dataTime = date.concat("T").concat(time);
    final var localDateTime = LocalDateTime.parse(dataTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    final var bid = csvRecord.get(2).isEmpty() ? 0D : Double.parseDouble(csvRecord.get(2));
    final var ask = csvRecord.get(3).isEmpty() ? 0D : Double.parseDouble(csvRecord.get(3));
    return Tick.builder().time(localDateTime).bid(bid).ask(ask).build();
  }

  @SneakyThrows
  private void sent(final String symbol, @NotNull final Tick tick) {
    try (final var httpClient = HttpClient.newHttpClient()) {
      final var body = "{\n  \"timestamp\": \"" + tick.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\",\n  \"bid\": " + tick.getBid() + ",\n  \"ask\": " + tick.getAsk() + "\n}";
      final var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/ticks/" + symbol))
          .header("Content-Type", "application/json")
          .header("User-Agent", "insomnia/9.0.0")
          .method("POST", HttpRequest.BodyPublishers.ofString(body))
          .build();
      final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      switch (response.statusCode()) {
        case 200: {
          log.warn("[{}] Return 200 not expected", LocalDateTime.now());
          break;
        }
        case 201: {
          if (!response.body().isEmpty()) {
            log.info("--> Received order: {}", response.body());
            final Collection<Order> orders = Arrays.stream(response.body().split(","))
                .map(order -> order.split(" "))
                .map(order -> Order.builder().tp(Integer.parseInt(order[3])).sl(Integer.parseInt(order[4])).openTick(tick).closeTick(tick)
                    .type(order[2].equals("BUY") ? Type.BUY : Type.SELL).status(Status.OPEN).build())
                .toList();
            log.info("--> Created orders:");
            orders.forEach(order -> log.info("{}", order));
            this.getOrderRepository().addAll(orders);
            this.getBalanceHistoric().add(new SimpleEntry<>(tick.getTime(), this.calculateBalance(tick)));
            log.info("--> Tmp balance: {}", this.getBalanceHistoric().getLast().getValue());
          }
          break;
        }
        case 400: {
          log.warn("[{}] Return 400 not expected: {}", LocalDateTime.now(), response.body());
          break;
        }
        case 500: {
          log.error("[{}] Return 500 not expected: {}", LocalDateTime.now(), response.body());
          break;
        }
        default: log.error("[{}] Return unknown error code {}: {}", response.statusCode(), LocalDateTime.now(), response.body());
      }
    }
  }

  private double calculateBalance(final @NotNull Tick tick) {
    this.getOrderRepository().parallelStream().filter(order -> Status.OPEN.equals(order.getStatus())).forEach(order -> order.setCloseTick(tick));
    return this.getOrderRepository().stream().mapToDouble(Order::getBalance).sum();
  }
}

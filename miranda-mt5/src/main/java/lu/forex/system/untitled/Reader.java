package lu.forex.system.untitled;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class Reader {

  private static final String CSV = "_201112190000_202404262358.csv";

  @SneakyThrows
  public void start(final String symbol) {
    try (final var httpClient = HttpClient.newHttpClient()) {
      final var fileName = "C:\\Users\\AllanDeMirandaSilva\\Downloads\\" + symbol + CSV;
      log.info("[{}] Reading file {}", LocalDateTime.now(), fileName);
      final var inputFile = new File(fileName);
      final var bidH = new AtomicReference<>(0D);
      final var askH = new AtomicReference<>(0D);
      final var lastUpdate = new AtomicReference<>(LocalDateTime.MIN);

      var numLines = new AtomicLong(0L);
      try(var lines = Files.lines(inputFile.toPath())) {
        numLines.set(lines.count() - 1L);
      }
      var lineNow = new AtomicLong(1);
      var lastPercentage = new AtomicLong(-1);

      log.info("[{}] Starting...", LocalDateTime.now());
      try (final var fileReader = new FileReader(inputFile); final var csvParser = CSVFormat.TDF.builder().build().parse(fileReader)) {
        StreamSupport.stream(csvParser.spliterator(), false).skip(1).map(this::getDataTick).forEachOrdered(tick -> {
          final long percentage = lineNow.addAndGet(1) * 100L / numLines.get();
          if(percentage != lastPercentage.get()) {
            log.warn("[{}] {}% read", LocalDateTime.now(), percentage);
            lastPercentage.set(percentage);
          }
          if (tick.getBid() != bidH.get() || tick.getAsk() != askH.get()) {
            if (tick.getBid() > 0d) {
              bidH.set(tick.getBid());
            }
            if (tick.getAsk() > 0d) {
              askH.set(tick.getAsk());
            }
            if ((bidH.get() > 0d) && (askH.get() > 0d) && tick.getTime().isAfter(lastUpdate.get())) {
              sent(httpClient, symbol, tick.getTime(), bidH.get(), askH.get());
              lastUpdate.set(tick.getTime());
            }
          }
        });
      }
    }
  }

  @SneakyThrows
  public void start(final String symbol, final TimeFrame timeFrame) {
    try (final var httpClient = HttpClient.newHttpClient()) {
      final var fileName = "C:\\Users\\AllanDeMirandaSilva\\Downloads\\" + symbol + CSV;
      log.info("[{}] Reading file {}", LocalDateTime.now(), fileName);
      final var inputFile = new File(fileName);
      final var bidH = new AtomicReference<>(0D);
      final var askH = new AtomicReference<>(0D);
      var lastTime = new AtomicReference<LocalDateTime>(LocalDateTime.MIN);
      var timeF = new AtomicReference<LocalDateTime>(LocalDateTime.MIN);

      var numLines = new AtomicLong(0L);
      try(var lines = Files.lines(inputFile.toPath())) {
        numLines.set(lines.count() - 1L);
      }
      var lineNow = new AtomicLong(1);
      var lastPercentage = new AtomicLong(-1);

      log.info("[{}] Starting...", LocalDateTime.now());
      try (final var fileReader = new FileReader(inputFile); final var csvParser = CSVFormat.TDF.builder().build().parse(fileReader)) {
        StreamSupport.stream(csvParser.spliterator(), false).skip(1).map(this::getDataTick).forEachOrdered(tick -> {
          final long percentage = lineNow.addAndGet(1) * 100L / numLines.get();
          if(percentage != lastPercentage.get()) {
            log.warn("[{}] {}% read", LocalDateTime.now(), percentage);
            lastPercentage.set(percentage);
          }

          final var forNow = TimeFrameUtils.getCandlestickTimestamp(tick.getTime(), timeFrame);
          if(!forNow.equals(timeF.get())) {
            if ((bidH.get() > 0d) && (askH.get() > 0d)) {
              sent(httpClient, symbol, lastTime.get(), bidH.get(), askH.get());
            }
            timeF.set(forNow);
          }
          lastTime.set(tick.getTime());
          if (tick.getBid() > 0d) {
            bidH.set(tick.getBid());
          }
          if (tick.getAsk() > 0d) {
            askH.set(tick.getAsk());
          }
        });
      }
    }
  }

  private @NotNull Tick getDataTick(final @NotNull CSVRecord csvRecord) {
    final String date = csvRecord.get(0).replace(".", "-");
    final String time = csvRecord.get(1);
    final String dataTime = date.concat("T").concat(time);
    final LocalDateTime localDateTime = LocalDateTime.parse(dataTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    final double bid = csvRecord.get(2).isEmpty() ? 0d : Double.parseDouble(csvRecord.get(2));
    final double ask = csvRecord.get(3).isEmpty() ? 0d : Double.parseDouble(csvRecord.get(3));
    return new Tick(localDateTime, bid, ask);
  }

  @SneakyThrows
  private synchronized void sent(final @NotNull HttpClient httpClient, final String symbol, final @NotNull LocalDateTime localDateTime, final double bid, final double ask) {
    final var timestamp = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    final var body = "{\n  \"timestamp\": \"" + timestamp + "\",\n  \"bid\": " + bid + ",\n  \"ask\": " + ask + "\n}";
    final var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/ticks/" + symbol))
        .header("Content-Type", "application/json")
        .header("User-Agent", "insomnia/9.0.0")
        .method("POST", HttpRequest.BodyPublishers.ofString(body))
        .build();
    final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() == 500) {
      log.error(LocalDateTime.now().toString());
      Arrays.stream(response.body().split("\r\n\tat")).forEachOrdered(log::error);
      throw new IllegalStateException(String.valueOf(response.statusCode()));
    }

    if (!response.body().isEmpty()) {
      log.info("[{}] {}", LocalDateTime.now(), response.body());
    }
  }
}

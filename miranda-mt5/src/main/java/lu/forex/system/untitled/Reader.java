package lu.forex.system.untitled;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;

public class Reader {

  @SneakyThrows
  public void start(final String symbol) {
    try (final HttpClient httpClient = HttpClient.newHttpClient()) {
      final String fileName = "C:\\Users\\AllanDeMirandaSilva\\Downloads\\" + symbol + "_201112190000_202404262358.csv";
      final File inputFile = new File(fileName);
      final AtomicReference<Double> bidH = new AtomicReference<>(0D);
      final AtomicReference<Double> askH = new AtomicReference<>(0D);
      final AtomicReference<LocalDateTime> lastUpdate = new AtomicReference<>(LocalDateTime.MIN);
      try (final FileReader fileReader = new FileReader(inputFile); final CSVParser csvParser = CSVFormat.TDF.builder().build().parse(fileReader)) {
        StreamSupport.stream(csvParser.spliterator(), false).skip(1).map(this::getDataTick).forEachOrdered(tick -> {
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
  private synchronized void sent(final @NotNull HttpClient httpClient, final String symbol, final @NotNull LocalDateTime localDateTime,
      final double bid, final double ask) {
    final String s = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    final String s1 = String.valueOf(bid);
    final String s2 = String.valueOf(ask);
    final String body = "{\n  \"timestamp\": \"" + s + "\",\n  \"bid\": " + s1 + ",\n  \"ask\": " + s2 + "\n}";
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/ticks/" + symbol)).header("Content-Type", "application/json")
        .header("User-Agent", "insomnia/9.0.0").method("POST", HttpRequest.BodyPublishers.ofString(body)).build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//    if (response.statusCode() != 201) {
//      if (response.statusCode() == 409) {
//        System.err.println("Conflict: " + body);
//        System.err.println("Response: " + response.body());
//      } else {
//        System.err.println("Error " + response.statusCode());
//        System.err.println("body: " + body);
//      }
//    }
  }


}

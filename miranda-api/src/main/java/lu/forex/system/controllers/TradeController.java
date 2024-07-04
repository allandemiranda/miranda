package lu.forex.system.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.TradeDto;
import lu.forex.system.enums.OrderStatus;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.TradeOperation;
import lu.forex.system.services.SymbolService;
import lu.forex.system.services.TradeService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class TradeController implements TradeOperation {

  private final TradeService tradeService;
  private final SymbolService symbolService;

  @Value("${init.filePath}")
  private String filePathInit;

  @Override
  public Collection<TradeDto> getTrades(final String symbolName) {
    final UUID symbolId = this.getSymbolService().getSymbol(symbolName).id();
    final List<TradeDto> trades = new ArrayList<>(this.getTradeService().getTrades(symbolId));

    final Map<TimeFrame, Map<Triple<Integer, Integer, Integer>, Map<DayOfWeek, Map<Pair<LocalTime, LocalTime>, TradeDto>>>> mappingTrades = trades.stream()
        .collect(Collectors.groupingBy(t -> t.scope().timeFrame(), Collectors.groupingBy(t -> Triple.of(t.stopLoss(), t.takeProfit(), t.spreadMax()),
            Collectors.groupingBy(TradeDto::slotWeek, Collectors.toMap(t -> Pair.of(t.slotStart(), t.slotEnd()), t -> t)))));
    try (final Workbook workbook = new XSSFWorkbook()) {
      final Sheet sheet = workbook.createSheet("trades_ALL");
      final Row header = sheet.createRow(0);
      final String[] headerNames = new String[]{"ID", "TimeFrame", "Stop Loss", "Take Profit", "Spread Max", "Slot Week", "Slot Time Start",
          "Slot Time End", "Balance", "Number of Stop Loss", "Number of Take Profit", "Number of Close Orders", "Number of Orders", "Is Activate"};
      IntStream.range(0, headerNames.length).forEach(i -> header.createCell(i).setCellValue(headerNames[i]));
      IntStream.range(1, trades.size()+1).forEach(i -> {
        final var tradeDto = trades.get(i-1);
        final Object[] data = new Object[]{tradeDto.id().toString(),tradeDto.scope().timeFrame().name(), tradeDto.stopLoss(), tradeDto.takeProfit(), tradeDto.spreadMax(),
            tradeDto.slotWeek().toString(), tradeDto.slotStart().toString(), tradeDto.slotEnd().toString(), tradeDto.balance(),
            tradeDto.orders().stream().filter(orderDto -> OrderStatus.STOP_LOSS.equals(orderDto.orderStatus())).count(),
            tradeDto.orders().stream().filter(orderDto -> OrderStatus.TAKE_PROFIT.equals(orderDto.orderStatus())).count(),
            tradeDto.orders().stream().filter(orderDto -> !OrderStatus.OPEN.equals(orderDto.orderStatus())).count(), tradeDto.orders().size(),
            String.valueOf(tradeDto.isActivate())};
        final Row row = sheet.createRow(i);
        IntStream.range(0, data.length).forEach(j -> {
          final var cell = row.createCell(j);
          switch (data[j]) {
            case String text -> cell.setCellValue(text);
            case Integer integer -> cell.setCellValue(integer);
            case Double doubles -> cell.setCellValue(doubles);
            case Long longs -> cell.setCellValue(longs);
            default -> throw new IllegalStateException("Unexpected value: " + data[j] + " on " + Arrays.toString(data));
          }
        });
      });


      mappingTrades.forEach((timeFrame, entryValue) -> {

        final Sheet sheetTrades = workbook.createSheet("trades_".concat(timeFrame.name()));
        final Row headerTrades = sheetTrades.createRow(0);
        final String[] headerTradesNames = new String[]{"ID", "Stop Loss", "Take Profit", "Spread Max", "Slot Week", "Slot Time Start",
            "Slot Time End", "Balance", "Number of Stop Loss", "Number of Take Profit", "Number of Close Orders", "Number of Orders", "Is Activate"};
        IntStream.range(0, headerTradesNames.length).forEach(i -> {
          final var cell = headerTrades.createCell(i);
          cell.setCellValue(headerTradesNames[i]);
        });

        final Object[][] tmpTradesDto = entryValue.entrySet().stream().flatMap(
            tripleMapEntry -> tripleMapEntry.getValue().entrySet().stream()
                .flatMap(dayOfWeekListEntry -> dayOfWeekListEntry.getValue().values().stream())).map(
            tradeDto -> new Object[]{tradeDto.id().toString(), tradeDto.stopLoss(), tradeDto.takeProfit(), tradeDto.spreadMax(),
                tradeDto.slotWeek().toString(), tradeDto.slotStart().toString(), tradeDto.slotEnd().toString(), tradeDto.balance(),
                tradeDto.orders().stream().filter(orderDto -> OrderStatus.STOP_LOSS.equals(orderDto.orderStatus())).count(),
                tradeDto.orders().stream().filter(orderDto -> OrderStatus.TAKE_PROFIT.equals(orderDto.orderStatus())).count(),
                tradeDto.orders().stream().filter(orderDto -> !OrderStatus.OPEN.equals(orderDto.orderStatus())).count(), tradeDto.orders().size(),
                String.valueOf(tradeDto.isActivate())}).toArray(Object[][]::new);
        IntStream.range(1, tmpTradesDto.length + 1).forEach(i -> {
          final Row tradesRowData = sheetTrades.createRow(i);
          IntStream.range(0, tmpTradesDto[i - 1].length).forEach(j -> {
            final var cell = tradesRowData.createCell(j);
            switch (tmpTradesDto[i - 1][j]) {
              case String text -> cell.setCellValue(text);
              case Integer integer -> cell.setCellValue(integer);
              case Double doubles -> cell.setCellValue(doubles);
              case Long longs -> cell.setCellValue(longs);
              default -> throw new IllegalStateException("Unexpected value: " + tmpTradesDto[i - 1][j]);
            }
          });
        });

        entryValue.forEach((tripleSlTpSm, tripleMapEntryValue) -> {

          final String sheetNameBalance = "windows_balance_".concat(tripleSlTpSm.getLeft().toString()).concat("_")
              .concat(tripleSlTpSm.getMiddle().toString()).concat("_").concat(tripleSlTpSm.getRight().toString()).concat("_")
              .concat(timeFrame.getName());
          final Sheet sheetBalance = workbook.createSheet(sheetNameBalance);
          final String sheetNameWindows = "windows_TP_SL_SM_".concat(tripleSlTpSm.getLeft().toString()).concat("_")
              .concat(tripleSlTpSm.getMiddle().toString()).concat("_").concat(tripleSlTpSm.getRight().toString()).concat("_")
              .concat(timeFrame.getName());
          final Sheet sheetWindows = workbook.createSheet(sheetNameWindows);

          final DayOfWeek[] weeks = tripleMapEntryValue.keySet().toArray(DayOfWeek[]::new);
          final Row headerBalance = sheetBalance.createRow(0);
          IntStream.range(0, weeks.length).forEach(i -> {
            final var cell = headerBalance.createCell(i + 1);
            cell.setCellValue(weeks[i].toString());
          });
          final Row headerWindows = sheetWindows.createRow(0);
          IntStream.range(0, weeks.length).forEach(i -> {
            final var cell = headerWindows.createCell(i + 1);
            cell.setCellValue(weeks[i].toString());
          });

          final Pair<LocalTime, LocalTime>[] timesPair = tripleMapEntryValue.get(weeks[0]).keySet().stream().sorted(Entry.comparingByKey()).toArray(Pair[]::new);
          IntStream.range(1, timesPair.length + 1).forEach(i -> {
            final Row balanceRowData = sheetBalance.createRow(i);
            IntStream.range(0, weeks.length + 1).forEach(j -> {
              final var cell = balanceRowData.createCell(j);
              if (j == 0) {
                cell.setCellValue(timesPair[i - 1].toString());
              } else {
                final var t = tripleMapEntryValue.get(weeks[j - 1]).get(timesPair[i - 1]);
                cell.setCellValue(t.balance());
              }
            });
          });
          IntStream.range(1, timesPair.length + 1).forEach(i -> {
            final Row balanceRowData = sheetWindows.createRow(i);
            IntStream.range(0, weeks.length + 1).forEach(j -> {
              final var cell = balanceRowData.createCell(j);
              if (j == 0) {
                cell.setCellValue(timesPair[i - 1].toString());
              } else {
                final var t = tripleMapEntryValue.get(weeks[j - 1]).get(timesPair[i - 1]);
                cell.setCellValue(t.balance());
              }
            });
          });
        });
      });

      final var root = new File(this.getFilePathInit());
      final var fileXlsx = new File(root, symbolName.concat("_data.xlsx"));
      workbook.write(new FileOutputStream(fileXlsx));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return List.of();
  }
}

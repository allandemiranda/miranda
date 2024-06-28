package lu.forex.system.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.enums.TimeFrame;
import lu.forex.system.operations.CandlestickOperation;
import lu.forex.system.services.CandlestickService;
import lu.forex.system.services.ScopeService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class CandlestickController implements CandlestickOperation {

  private final CandlestickService candlestickService;
  private final ScopeService scopeService;

  @Value("${init.filePath}")
  private String filePathInit;

  @Override
  public Collection<CandlestickDto> getCandlesticks(final String symbolName, final TimeFrame timeFrame) {
    final ScopeDto scopeDto = this.getScopeService().getScope(symbolName, timeFrame);
    final List<CandlestickDto> candlestickDtoList = this.getCandlestickService().findCandlesticksAsc(scopeDto.id());
    try (final Workbook workbook = new XSSFWorkbook()) {
      final Sheet sheet = workbook.createSheet("trades_".concat(timeFrame.name()));

      IntStream.range(0, candlestickDtoList.size()).forEach(i -> {
        final Row row = sheet.createRow(i);
        row.createCell(0).setCellValue(candlestickDtoList.get(i).timestamp().toString().replace("T", " "));
        row.createCell(1).setCellValue(candlestickDtoList.get(i).body().open());
        row.createCell(2).setCellValue(candlestickDtoList.get(i).body().high());
        row.createCell(3).setCellValue(candlestickDtoList.get(i).body().low());
        row.createCell(4).setCellValue(candlestickDtoList.get(i).body().close());
        candlestickDtoList.get(i).movingAverages().stream().filter(movingAverageDto -> MovingAverageType.EMA.equals(movingAverageDto.type()) && movingAverageDto.period() == 12).findFirst().ifPresent(ema -> {
          if(ema.value() != null) {
            row.createCell(5).setCellValue(ema.value());
          }
        });
        candlestickDtoList.get(i).movingAverages().stream().filter(movingAverageDto -> MovingAverageType.EMA.equals(movingAverageDto.type()) && movingAverageDto.period() == 26).findFirst().ifPresent(ema -> {
          if(ema.value() != null) {
            row.createCell(6).setCellValue(ema.value());
          }
        });
        candlestickDtoList.get(i).technicalIndicators().stream().filter(technicalIndicatorDto -> Indicator.ADX.equals(technicalIndicatorDto.indicator())).findFirst().ifPresent(adx -> {
          if(adx.data().get("+di(P)") != null) {
            row.createCell(7).setCellValue(adx.data().get("+di(P)"));
          }
          if(adx.data().get("-di(P)") != null) {
            row.createCell(8).setCellValue(adx.data().get("-di(P)"));
          }
          if(adx.data().get("adx") != null){
            row.createCell(9).setCellValue(adx.data().get("adx"));
          }
          row.createCell(10).setCellValue(adx.signal().name());
        });
        candlestickDtoList.get(i).technicalIndicators().stream().filter(technicalIndicatorDto -> Indicator.AC.equals(technicalIndicatorDto.indicator())).findFirst().ifPresent(ac -> {
          if(ac.data().get("mp") != null) {
            row.createCell(11).setCellValue(ac.data().get("mp"));
          }
          if(ac.data().get("ao") != null) {
            row.createCell(12).setCellValue(ac.data().get("ao"));
          }
          if(ac.data().get("ac") != null) {
            row.createCell(13).setCellValue(ac.data().get("ac"));
          }
          row.createCell(14).setCellValue(ac.signal().name());
        });
        candlestickDtoList.get(i).technicalIndicators().stream().filter(technicalIndicatorDto -> Indicator.MACD.equals(technicalIndicatorDto.indicator())).findFirst().ifPresent(macd -> {
          if(macd.data().get("macd") != null) {
            row.createCell(15).setCellValue(macd.data().get("macd"));
          }
          if(macd.data().get("signal") != null) {
            row.createCell(16).setCellValue(macd.data().get("signal"));
          }
          row.createCell(17).setCellValue(macd.signal().name());
        });
        row.createCell(18).setCellValue(candlestickDtoList.get(i).signalIndicator().name());
      });

      final var root = new File(this.getFilePathInit());
      final var fileXlsx = new File(root, symbolName.concat("_candlesticks.xlsx"));
      workbook.write(new FileOutputStream(fileXlsx));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // NEED BE UPDATE TO THE FRONT END WITH PAGINABLE
    return this.getCandlestickService().findCandlesticksDescWithLimit(scopeDto.id(), 5);
  }

}

package lu.forex.system.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lu.forex.system.dtos.CandlestickCreateDto;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.enums.TimeFrame;
import org.springframework.stereotype.Service;

@Service
public interface CandlestickService {

  @NotNull
  CandlestickDto save(@NotNull CandlestickCreateDto candlestickCreateDto, @NotNull @NotBlank String symbolName);

  @NotNull
  Collection<CandlestickDto> findAllBySymbolNameAndTimeFrameOrderByTimestampAsc(@NotNull @NotBlank String symbolName, @NotNull TimeFrame timeFrame);

  @NotNull
  CandlestickDto findOneBySymbolNameAndTimeFrameOrderByTimeFrameAsc(@NotNull @NotBlank String symbolName, @NotNull TimeFrame timeFrame);

}

package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.mappers.TechnicalIndicatorMapper;
import lu.forex.system.repositories.TechnicalIndicatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface TechnicalIndicatorService {

  TechnicalIndicatorRepository getTechnicalIndicatorRepository();

  TechnicalIndicatorMapper getTechnicalIndicatorMapper();

  Indicator getIndicator();

  @Transactional(readOnly = true)
  int getNumberOfCandlesticksToCalculate();

  @Transactional
  @NotNull
  default TechnicalIndicatorDto initTechnicalIndicator() {
    final TechnicalIndicator indicator = new TechnicalIndicator();
    indicator.setIndicator(this.getIndicator());
    indicator.setSignal(SignalIndicator.NEUTRAL);
    final TechnicalIndicator saved = this.getTechnicalIndicatorRepository().save(indicator);
    return this.getTechnicalIndicatorMapper().toDto(saved);
  }

  @Transactional
  default Collection<NewMovingAverageDto> generateMAs() {
    return List.of();
  }

  @Transactional
  void calculateTechnicalIndicator(final @NotNull List<@NotNull CandlestickDto> candlestickDtos);

}

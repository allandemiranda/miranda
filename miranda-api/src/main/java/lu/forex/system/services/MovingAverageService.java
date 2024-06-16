package lu.forex.system.services;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lu.forex.system.dtos.CandlestickDto;
import lu.forex.system.dtos.MovingAverageDto;
import lu.forex.system.dtos.NewMovingAverageDto;
import lu.forex.system.entities.MovingAverage;
import lu.forex.system.enums.MovingAverageType;
import lu.forex.system.mappers.MovingAverageMapper;
import lu.forex.system.repositories.MovingAverageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface MovingAverageService {

  MovingAverageRepository getMovingAverageRepository();
  MovingAverageMapper getMovingAverageMapper();
  MovingAverageType getMovingAverageType();

  @Transactional
  @NotNull
  default MovingAverageDto createMovingAverage(final @NotNull NewMovingAverageDto newMovingAverageDto) {
    final MovingAverage movingAverage = this.getMovingAverageMapper().toEntity(newMovingAverageDto);
    final MovingAverage savedMovingAverage = this.getMovingAverageRepository().save(movingAverage);
    return this.getMovingAverageMapper().toDto(savedMovingAverage);
  }

  @Transactional
  void calculateMovingAverage(final @NotNull List<@NotNull CandlestickDto> candlestickDtos);

}

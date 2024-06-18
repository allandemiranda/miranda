package lu.forex.system.mappers;

import jakarta.validation.constraints.NotNull;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.TechnicalIndicator;

public interface TechnicalIndicatorMapper {

  @NotNull
  TechnicalIndicator toEntity(final @NotNull TechnicalIndicatorDto technicalIndicatorDto);

  @NotNull
  TechnicalIndicatorDto toDto(final @NotNull TechnicalIndicator technicalIndicator);
}
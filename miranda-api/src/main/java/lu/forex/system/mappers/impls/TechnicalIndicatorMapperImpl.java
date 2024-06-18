package lu.forex.system.mappers.impls;

import jakarta.validation.constraints.NotNull;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lu.forex.system.dtos.TechnicalIndicatorDto;
import lu.forex.system.entities.TechnicalIndicator;
import lu.forex.system.mappers.TechnicalIndicatorMapper;
import org.springframework.stereotype.Component;

@Component
public class TechnicalIndicatorMapperImpl implements TechnicalIndicatorMapper {

  @Override
  public @NotNull TechnicalIndicator toEntity(final @NotNull TechnicalIndicatorDto technicalIndicatorDto) {
    final var technicalIndicator = new TechnicalIndicator();
    technicalIndicator.setId(technicalIndicatorDto.id());
    technicalIndicator.setIndicator(technicalIndicatorDto.indicator());
    final var data = technicalIndicatorDto.data().entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    technicalIndicator.setData(data);
    technicalIndicator.setSignal(technicalIndicatorDto.signal());
    return technicalIndicator;
  }

  @Override
  public @NotNull TechnicalIndicatorDto toDto(final @NotNull TechnicalIndicator technicalIndicator) {
    final var id = technicalIndicator.getId();
    final var indicator = technicalIndicator.getIndicator();
    final var data = technicalIndicator.getData().entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    final var signal = technicalIndicator.getSignal();

    return new TechnicalIndicatorDto(id, indicator, data, signal);
  }
}


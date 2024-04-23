package lu.forex.system.mappers;

import lombok.NonNull;
import lu.forex.system.entities.Swap;
import lu.forex.system.models.SwapDto;
import org.springframework.stereotype.Component;

@Component
public class SwapMapper {

  // To DTO
  public @NonNull SwapDto toDto(final @NonNull Swap swap) {
    return new SwapDto(swap.getLongTax(), swap.getShortTax(), swap.getRateTriple());
  }

  // To Entity
  public @NonNull Swap toEntity(final @NonNull SwapDto swapDto) {
    return new Swap(swapDto.longTax(), swapDto.shortTax(), swapDto.rateTriple());
  }
}

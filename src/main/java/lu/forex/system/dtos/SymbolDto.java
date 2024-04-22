package lu.forex.system.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lu.forex.system.enums.Currency;

@Accessors(fluent = true)
public record SymbolDto(@NonNull @NotBlank String value, @NonNull Currency margin, @NonNull Currency profit, int digits, @NonNull SwapDto swap) {

}

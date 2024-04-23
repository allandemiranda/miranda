package lu.forex.system.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import lu.forex.system.enums.Currency;

public record SymbolDto(@NonNull @NotBlank @Size(max = 6, min = 6) String name, @NonNull Currency margin, @NonNull Currency profit, int digits,
                        @NonNull SwapDto swap) {

}

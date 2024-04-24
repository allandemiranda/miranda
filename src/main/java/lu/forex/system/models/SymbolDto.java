package lu.forex.system.models;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record SymbolDto(@NonNull @NotBlank String name, @NonNull @NotBlank String description, @NonNull @NotBlank String margin,
                        @NonNull @NotBlank String profit, int digits, double swapLong, double swapShort) {

}

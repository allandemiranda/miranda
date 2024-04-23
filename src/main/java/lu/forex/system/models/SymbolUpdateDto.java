package lu.forex.system.models;

import lombok.NonNull;
import lu.forex.system.enums.Currency;

public record SymbolUpdateDto(@NonNull Currency margin, @NonNull Currency profit, int digits, @NonNull SwapDto swap) {

}

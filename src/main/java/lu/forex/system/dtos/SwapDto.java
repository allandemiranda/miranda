package lu.forex.system.dtos;

import java.time.DayOfWeek;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public record SwapDto(double longTax, double shortTax, @NonNull DayOfWeek rateTriple) {

}

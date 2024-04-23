package lu.forex.system.models;

import java.time.DayOfWeek;
import lombok.NonNull;

public record SwapDto(double longTax, double shortTax, @NonNull DayOfWeek rateTriple) {

}

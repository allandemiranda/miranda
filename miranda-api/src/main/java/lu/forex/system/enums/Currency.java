package lu.forex.system.enums;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
  //@formatter:off
  EUR("EUR", "Euro"),
  USD("USD", "US Dollar"),
  GBP("GBP", "Pound Sterling"),
  CHF("CHF", "Swiss Franc"),
  JPY("JPY", "Yen"),
  NZD("NZD", "New Zealand dollar"),
  AUD("AUD", "Australian dollar"),
  CAD("CAD", "Canadian dollar"),
  CNH("CNH", "Chinese Yuan Offshore"),
  SEK("SEK", "Swedish krona");
  //@formatter:on

  @NotNull
  @NotBlank
  private final String name;
  @NotNull
  @NotBlank
  private final String description;
}

package lu.forex.system.enums;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
  //@formatter:off
  EUR("EUR"),
  USD("USD"),
  GBP("GBP"),
  CHF("CHF"),
  JPY("JPY"),
  NZD("NZD"),
  AUD("AUD"),
  CAD("CAD"),
  CNH("CNH"),
  SEK("SEK");
  //@formatter:on

  @NotBlank
  private final String name;
}

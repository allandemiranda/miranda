package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lu.forex.system.enums.Currency;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "symbols", indexes = {@Index(name = "idx_symbols_name", columnList = "name")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_symbols_name", columnNames = {"name"})})
public class Symbol implements Serializable {

  @Serial
  private static final long serialVersionUID = -7531266602862670746L;

  @NotNull
  @NotBlank
  @Id
  @Column(name = "name", nullable = false, length = 6)
  private String name;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "currency_base", nullable = false, length = 3)
  private Currency currencyBase;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "currency_quote", nullable = false, length = 3)
  private Currency currencyQuote;

  @Positive
  @Min(1)
  @Column(name = "digits", nullable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int digits;

  @Column(name = "swap_long", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double swapLong;

  @Column(name = "swap_short", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double swapShort;

  public @NotNull String getDescription() {
    return this.getCurrencyBase().getDescription().concat(" vs ").concat(this.getCurrencyQuote().getDescription());
  }

}
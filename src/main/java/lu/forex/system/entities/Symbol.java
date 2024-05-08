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
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.annotations.SymbolCurrencyRepresentation;
import lu.forex.system.enums.Currency;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@Entity
@Table(name = "symbol", indexes = {@Index(name = "idx_symbol_name", columnList = "name, currency_base, currency_quote")}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_symbol_name_currency_base", columnNames = {"name", "currency_base", "currency_quote"})})
@SymbolCurrencyRepresentation
public class Symbol implements Serializable {

  @Serial
  private static final long serialVersionUID = -7531266602862670746L;

  @Id
  @NotNull
  @NotBlank
  @Column(name = "name", nullable = false, unique = true, length = 6)
  private String name;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "currency_base", nullable = false, length = 3, updatable = false)
  private Currency currencyBase;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "currency_quote", nullable = false, length = 3, updatable = false)
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

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Symbol symbol = (Symbol) o;
    return Objects.equals(this.getName(), symbol.getName()) && this.getCurrencyBase() == symbol.getCurrencyBase()
           && this.getCurrencyQuote() == symbol.getCurrencyQuote();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getName(), this.getCurrencyBase(), this.getCurrencyQuote());
  }
}
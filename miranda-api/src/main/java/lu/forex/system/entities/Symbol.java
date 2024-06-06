package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "symbol", indexes = {@Index(name = "idx_symbol_unq", columnList = "currency_pair_id", unique = true)})
public class Symbol implements Serializable {

  @Serial
  private static final long serialVersionUID = -8701110002032902053L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false, orphanRemoval = true, targetEntity = CurrencyPair.class)
  @JoinColumn(name = "currency_pair_id", nullable = false, unique = true, updatable = false)
  @Exclude
  private CurrencyPair currencyPair;

  @Positive
  @Column(name = "digits", nullable = false)
  @JdbcTypeCode(SqlTypes.SMALLINT)
  private int digits;

  @NotNull
  @Embedded
  private Swap swap;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Symbol symbol = (Symbol) o;
    return Objects.equals(getCurrencyPair(), symbol.getCurrencyPair());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getCurrencyPair());
  }
}
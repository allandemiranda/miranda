package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.Currency;
import lu.forex.system.listeners.CurrencyPairListener;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@EntityListeners({CurrencyPairListener.class})
@Table(name = "currency_pair", indexes = {
    @Index(name = "idx_currencypair_name_unq", columnList = "name", unique = true)
}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_currencypair_base_quote", columnNames = {"base", "quote", "name"})
})
public class CurrencyPair implements Serializable {

  @Serial
  private static final long serialVersionUID = 8532390452215860289L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "base", nullable = false, length = 3, updatable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private Currency base;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "quote", nullable = false, length = 3, updatable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private Currency quote;

  @NotNull
  @NotBlank
  @Size(max = 6, min = 6)
  @Column(name = "name", nullable = false, unique = true, length = 6, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private String name;

  @Exclude
  @OneToOne(mappedBy = "currencyPair", cascade = CascadeType.ALL, optional = false, orphanRemoval = true, targetEntity = Symbol.class)
  private Symbol symbol;

  @NotNull
  @NotBlank
  @Transient
  public String getDescription() {
    return this.getBase().getDescription().concat(" vs ").concat(this.getQuote().getDescription());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final CurrencyPair that = (CurrencyPair) o;
    return Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getName());
  }
}

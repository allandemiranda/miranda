package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "symbols", uniqueConstraints = {@UniqueConstraint(name = "uc_symbol_id_name", columnNames = {"id", "name"})})
public class Symbol {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true)
  private UUID id;

  @NotNull
  @NotBlank
  @Size(min = 6, max = 6)
  @Column(name = "name", nullable = false, unique = true, length = 6)
  @JdbcTypeCode(SqlTypes.CHAR)
  private String name;

  @NotNull
  @NotBlank
  @Column(name = "description", nullable = false)
  @JdbcTypeCode(SqlTypes.CHAR)
  private String description;

  @NotNull
  @NotBlank
  @Size(min = 3, max = 3)
  @Column(name = "currency_base", nullable = false, length = 3)
  @JdbcTypeCode(SqlTypes.CHAR)
  private String currencyBase;

  @NotNull
  @NotBlank
  @Size(min = 3, max = 3)
  @Column(name = "currency_quote", nullable = false, length = 3)
  @JdbcTypeCode(SqlTypes.CHAR)
  private String currencyQuote;

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


}
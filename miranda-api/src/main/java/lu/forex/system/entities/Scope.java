package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.TimeFrame;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "scope", indexes = {
    @Index(name = "idx_scope_symbol_id_unq", columnList = "symbol_id, time_frame", unique = true)}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_scope_symbol_id_time_frame", columnNames = {"symbol_id", "time_frame"})})
public class Scope implements Serializable {

  @Serial
  private static final long serialVersionUID = -2777335827353556499L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Exclude
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "symbol_id", nullable = false, updatable = false)
  private Symbol symbol;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "time_frame", nullable = false, length = 3, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private TimeFrame timeFrame;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Scope scope = (Scope) o;
    return Objects.equals(getSymbol(), scope.getSymbol()) && getTimeFrame() == scope.getTimeFrame();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSymbol(), getTimeFrame());
  }
}

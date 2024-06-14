package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import lu.forex.system.enums.SignalIndicator;
import lu.forex.system.listeners.CandlestickListener;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@EntityListeners({CandlestickListener.class})
@Table(name = "candlestick", indexes = {
    @Index(name = "idx_candlestick_scope_id_unq", columnList = "scope_id, timestamp", unique = true)}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_candlestick_scope_id", columnNames = {"scope_id", "timestamp"})})
public class Candlestick implements Serializable {

  @Serial
  private static final long serialVersionUID = 3872595660375685420L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Exclude
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false, targetEntity = Scope.class)
  @JoinColumn(name = "scope_id", nullable = false, updatable = false)
  private Scope scope;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "timestamp", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @NotNull
  @Embedded
  private CandlestickBody body;

  @Exclude
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "candlestick_id")
  private Set<MovingAverage> movingAverages = new LinkedHashSet<>();

  @Exclude
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "candlestick_id")
  private Set<TechnicalIndicator> technicalIndicators = new LinkedHashSet<>();

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "signal_indicator", nullable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private SignalIndicator signalIndicator;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Candlestick that = (Candlestick) o;
    return Objects.equals(getScope(), that.getScope()) && Objects.equals(getTimestamp(), that.getTimestamp());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getScope(), getTimestamp());
  }

}
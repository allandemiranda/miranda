package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
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
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lu.forex.system.enums.TimeFrame;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "candlestick", indexes = {
    @Index(name = "idx_candlestick_symbol_name", columnList = "symbol_name, time_frame")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_candlestick_symbol_name", columnNames = {"symbol_name", "time_frame", "timestamp"})
})
public class Candlestick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8655855891835745603L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, unique = true)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "symbol_name", nullable = false)
  private Symbol symbol;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "time_frame", nullable = false, length = 3)
  private TimeFrame timeFrame;

  @NotNull
  @Column(name = "timestamp", nullable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @Positive
  @Column(name = "high", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double high;

  @Positive
  @Column(name = "low", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double low;

  @Positive
  @Column(name = "open", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double open;

  @Positive
  @Column(name = "close", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double close;

}
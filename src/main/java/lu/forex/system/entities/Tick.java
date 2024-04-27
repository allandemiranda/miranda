package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "ticks", indexes = {@Index(name = "idx_tick_symbol_id", columnList = "symbol_id")})
public class Tick {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "timestamp", nullable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @Positive
  @Column(name = "bid", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double bid;

  @Positive
  @Column(name = "ask", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double ask;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
  @JoinColumn(name = "symbol_id", nullable = false)
  private Symbol symbol;


}
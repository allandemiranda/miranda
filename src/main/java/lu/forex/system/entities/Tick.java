package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "tick", indexes = {@Index(name = "idx_tick_symbol_name", columnList = "symbol_name")})
public class Tick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8640594898040399917L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @ManyToOne(cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "symbol_name", nullable = false)
  private Symbol symbol;

  @NotNull
  @Column(name = "timestamp", nullable = false)
  @JdbcTypeCode(SqlTypes.TIMESTAMP)
  private LocalDateTime timestamp;

  @Positive
  @Column(name = "high", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double bid;

  @Positive
  @Column(name = "low", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double ask;

}
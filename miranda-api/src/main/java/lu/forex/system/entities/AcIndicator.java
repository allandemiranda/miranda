package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
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
@Table(name = "ac_indicator")
public class AcIndicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -780377920070703541L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @PositiveOrZero
  @Column(name = "mp", nullable = false)
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private double mp;

  @Column(name = "ao")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double ao;

  @Column(name = "ac")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double ac;

  @Exclude
  @OneToOne(mappedBy = "acIndicator", cascade = CascadeType.ALL, optional = false)
  private Candlestick candlestick;

}

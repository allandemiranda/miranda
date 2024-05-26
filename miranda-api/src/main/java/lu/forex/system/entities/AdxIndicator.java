package lu.forex.system.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "adx_indicator")
public class AdxIndicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -9215994383059260651L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @Column(name = "tr_one")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double trOne;

  @Column(name = "p_dm_one")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double pDmOne;

  @Column(name = "n_dm_one")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double nDmOne;

  @Column(name = "dx")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double dx;

  @Column(name = "adx")
  @JdbcTypeCode(SqlTypes.DOUBLE)
  private Double adx;

  @Exclude
  @OneToOne(mappedBy = "adxIndicator", cascade = CascadeType.ALL, optional = false)
  private Candlestick candlestick;

}

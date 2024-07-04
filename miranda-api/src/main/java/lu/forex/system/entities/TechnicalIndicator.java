package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.converters.DataTechnicalIndicatorConverter;
import lu.forex.system.enums.Indicator;
import lu.forex.system.enums.SignalIndicator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "technical_indicator")
public class TechnicalIndicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -780377920070703541L;

  @Id
  @NotNull
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false, updatable = false, unique = true)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "indicator", nullable = false, updatable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private Indicator indicator;

  @NotNull
  @Column(name = "data_ti", nullable = false)
  @Convert(converter = DataTechnicalIndicatorConverter.class)
  private Map<String, Double> data = new HashMap<>();

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "signal", nullable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private SignalIndicator signal;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final TechnicalIndicator that = (TechnicalIndicator) o;
    return getIndicator() == that.getIndicator();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getIndicator());
  }
}

package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lu.forex.system.enums.SignalIndicatorStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "technical_indicator", indexes = {
    @Index(name = "idx_technicalindicator_id_unq", columnList = "id", unique = true)}, uniqueConstraints = {
    @UniqueConstraint(name = "uc_technicalindicator_id", columnNames = {"id"})})
public class TechnicalIndicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -780377920070703541L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  @ElementCollection
  private Map<String, Double> dataMap = new HashMap<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "signal_status", nullable = false)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  private SignalIndicatorStatus signalStatus;

}

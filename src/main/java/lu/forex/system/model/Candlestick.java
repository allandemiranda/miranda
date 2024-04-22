package lu.forex.system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Candlestick implements Serializable {

  @Serial
  private static final long serialVersionUID = 3707234909834765373L;

  @Id
  @GeneratedValue()
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private UUID id;
  @Column(nullable = false)
  private double high;
  @Column(nullable = false)
  private double low;
  @Column(nullable = false)
  private double open;
  @Column(nullable = false)
  private double close;
  @JoinColumn(nullable = false)
  @ManyToOne
  private Symbol symbol;

  public Candlestick(final double high, final double low, final double open, final double close, final Symbol symbol) {
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
    this.symbol = symbol;
  }
}

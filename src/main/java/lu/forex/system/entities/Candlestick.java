package lu.forex.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  @NonNull
  private LocalDateTime dateTime;
  @JoinColumn(nullable = false)
  @ManyToOne
  @NonNull
  private Symbol symbol;
  @Column(nullable = false)
  private double high;
  @Column(nullable = false)
  private double low;
  @Column(nullable = false)
  private double open;
  @Column(nullable = false)
  private double close;

  public Candlestick(final @NonNull Symbol symbol, final double high, final double low, final double open, final double close) {
    this.symbol = symbol;
    this.high = high;
    this.low = low;
    this.open = open;
    this.close = close;
  }
}

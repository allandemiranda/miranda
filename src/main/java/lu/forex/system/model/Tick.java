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
import java.time.LocalDateTime;
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
public class Tick implements Serializable {

  @Serial
  private static final long serialVersionUID = 8689684652979027409L;

  @Id
  @GeneratedValue()
  @Column(unique = true, nullable = false)
  @Setter(AccessLevel.PROTECTED)
  private UUID id;
  @Column(nullable = false)
  private LocalDateTime dateTime;
  @Column(nullable = false)
  private double bid;
  @Column(nullable = false)
  private double ask;
  @JoinColumn(nullable = false)
  @ManyToOne
  private Symbol symbol;

  public Tick(final LocalDateTime dateTime, final double bid, final double ask, final Symbol symbol) {
    this.dateTime = dateTime;
    this.bid = bid;
    this.ask = ask;
    this.symbol = symbol;
  }
}

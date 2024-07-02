package lu.forex.system.untitled;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Historic implements Serializable {

  @Serial
  private static final long serialVersionUID = -1086991510397130146L;

  private LocalDateTime timestamp;
  private double balance;
  private long openOrders;
  private long tpOrders;
  private long slOrders;

  public long getTotalOrders() {
    return this.getOpenOrders() + this.getTpOrders() + this.getSlOrders();
  }
}

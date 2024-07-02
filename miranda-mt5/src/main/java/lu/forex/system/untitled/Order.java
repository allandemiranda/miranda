package lu.forex.system.untitled;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order implements Serializable {

  @Serial
  private static final long serialVersionUID = 350941209128306182L;

  private final Tick openTick;
  private final int tp;
  private final int sl;
  private Tick closeTick;
  private Type type;
  private Status status;

  public double getBalance() {
    return switch (this.getType()) {
      case BUY -> BigDecimal.valueOf(this.getCloseTick().getBid()).subtract(BigDecimal.valueOf(this.getOpenTick().getAsk())).multiply(BigDecimal.valueOf(Math.pow(10, 5))).doubleValue();
      case SELL -> BigDecimal.valueOf(this.getOpenTick().getBid()).subtract(BigDecimal.valueOf(this.getCloseTick().getAsk())).multiply(BigDecimal.valueOf(Math.pow(10, 5))).doubleValue();
    };
  }

  public double getOpenPrice() {
    return switch (this.getType()) {
      case BUY -> this.getOpenTick().getAsk();
      case SELL -> this.getOpenTick().getBid();
    };
  }

  public double getClosePrice() {
    return switch (this.getType()) {
      case BUY -> this.getCloseTick().getBid();
      case SELL -> this.getCloseTick().getAsk();
    };
  }

  public void setCloseTick(final Tick closeTick){
    this.closeTick = closeTick;
    if(this.getBalance() >= this.getTp()){
      this.setStatus(Status.TAKE_PROFIT);
    } else if(this.getBalance() < 0 && Math.abs(this.getBalance()) >= this.getSl()) {
      this.setStatus(Status.STOP_LOSS);
    }
  }

}

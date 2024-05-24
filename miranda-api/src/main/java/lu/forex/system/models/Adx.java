package lu.forex.system.models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Adx extends Indicator implements Serializable {

  @Serial
  private static final long serialVersionUID = -6872310246122253881L;

  private Double tr;
  private Double pDmOne;
  private Double nDmOne;
  private Double trP;
  private Double pDmP;
  private Double nDmP;
  private Double pDiP;
  private Double nDiP;
  private Double diPDiff;
  private Double diPSun;
  private Double dx;
  private Double adxValue;

  @Override
  public boolean isValidData() {
    return Stream.of(this.getTr(), this.getPDmOne(), this.getNDmOne(), this.getTrP(), this.getPDmP(), this.getNDmP(), this.getPDiP(), this.getNDiP(),
        this.getDiPDiff(), this.getDiPSun(), this.getDx(), this.getAdxValue()).noneMatch(Objects::isNull);
  }
}

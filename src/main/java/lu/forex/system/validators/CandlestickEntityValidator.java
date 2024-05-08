package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lu.forex.system.annotations.CandlestickRepresentation;
import lu.forex.system.entities.Candlestick;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class CandlestickEntityValidator implements ConstraintValidator<CandlestickRepresentation, Candlestick> {

  @Override
  public void initialize(final CandlestickRepresentation constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final Candlestick value, final ConstraintValidatorContext context) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    return value.getHigh() >= value.getLow() && value.getHigh() >= value.getOpen() && value.getHigh() >= value.getClose()
           && value.getLow() <= value.getOpen() && value.getLow() <= value.getClose();

  }
}

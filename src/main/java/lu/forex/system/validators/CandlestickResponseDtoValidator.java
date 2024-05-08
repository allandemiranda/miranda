package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lu.forex.system.annotations.CandlestickRepresentation;
import lu.forex.system.dtos.CandlestickResponseDto;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class CandlestickResponseDtoValidator implements ConstraintValidator<CandlestickRepresentation, CandlestickResponseDto> {

  @Override
  public void initialize(final CandlestickRepresentation constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final CandlestickResponseDto value, final ConstraintValidatorContext context) {
    if (value == null) {
      throw new IllegalArgumentException();
    }
    return value.high() >= value.low() && value.high() >= value.open() && value.high() >= value.close() && value.low() <= value.open()
           && value.low() <= value.close();
  }
}

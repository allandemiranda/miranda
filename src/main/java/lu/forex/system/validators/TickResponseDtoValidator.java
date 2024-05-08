package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lu.forex.system.annotations.TickRepresentation;
import lu.forex.system.dtos.TickResponseDto;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class TickResponseDtoValidator implements ConstraintValidator<TickRepresentation, TickResponseDto> {

  @Override
  public void initialize(final TickRepresentation constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final @NotNull TickResponseDto value, final ConstraintValidatorContext context) {
    return value.ask() >= value.bid();
  }
}

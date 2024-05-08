package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lu.forex.system.annotations.TickRepresentation;
import lu.forex.system.dtos.TickCreateDto;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class TickCreateDtoValidator implements ConstraintValidator<TickRepresentation, TickCreateDto> {

  @Override
  public void initialize(final TickRepresentation constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final @NotNull TickCreateDto value, final ConstraintValidatorContext context) {
    return value.ask() >= value.bid();
  }
}

package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lu.forex.system.annotations.TickRepresentation;
import lu.forex.system.entities.Tick;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class TickEntityValidator implements ConstraintValidator<TickRepresentation, Tick> {

  private static final String BID = "bid";
  private TickRepresentation constraintAnnotation;

  @Override
  public void initialize(final TickRepresentation constraintAnnotation) {
    this.setConstraintAnnotation(constraintAnnotation);
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final @NotNull Tick value, final ConstraintValidatorContext context) {
    if (value.getAsk() >= value.getBid()) {
      return true;
    } else {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(this.getConstraintAnnotation().message()).addPropertyNode(BID).addConstraintViolation();
      return false;
    }
  }
}

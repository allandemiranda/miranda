package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lu.forex.system.annotations.SymbolCurrencyRepresentation;
import lu.forex.system.entities.Symbol;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class SymbolEntityValidator implements ConstraintValidator<SymbolCurrencyRepresentation, Symbol> {

  @Override
  public void initialize(final SymbolCurrencyRepresentation constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final @NotNull Symbol value, final ConstraintValidatorContext context) {
    return !value.getCurrencyBase().equals(value.getCurrencyQuote());
  }
}

package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lu.forex.system.annotations.SymbolCurrencyRepresentation;
import lu.forex.system.dtos.SymbolCreateDto;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class SymbolCreateDtoValidator implements ConstraintValidator<SymbolCurrencyRepresentation, SymbolCreateDto> {

  @Override
  public void initialize(final SymbolCurrencyRepresentation constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final @NotNull SymbolCreateDto value, final ConstraintValidatorContext context) {
    return !value.currencyBase().equals(value.currencyQuote());
  }
}

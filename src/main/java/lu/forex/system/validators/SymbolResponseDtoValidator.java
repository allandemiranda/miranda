package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lu.forex.system.annotations.SymbolCurrencyRepresentation;
import lu.forex.system.dtos.SymbolResponseDto;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class SymbolResponseDtoValidator implements ConstraintValidator<SymbolCurrencyRepresentation, SymbolResponseDto> {

  @Override
  public void initialize(final SymbolCurrencyRepresentation constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final @NotNull SymbolResponseDto value, final ConstraintValidatorContext context) {
    return !value.currencyBase().equals(value.currencyQuote());
  }
}

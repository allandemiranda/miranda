package lu.forex.system.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lu.forex.system.annotations.SymbolCurrencyRepresentation;
import lu.forex.system.entities.Symbol;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class SymbolEntityValidator implements ConstraintValidator<SymbolCurrencyRepresentation, Symbol> {

  private static final String CURRENCY_BASE = "currencyBase";
  private SymbolCurrencyRepresentation constraintAnnotation;

  @Override
  public void initialize(final SymbolCurrencyRepresentation constraintAnnotation) {
    this.setConstraintAnnotation(constraintAnnotation);
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(final @NotNull Symbol value, final ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(this.getConstraintAnnotation().message()).addPropertyNode(CURRENCY_BASE).addConstraintViolation();
    return !value.getCurrencyBase().equals(value.getCurrencyQuote());
  }
}

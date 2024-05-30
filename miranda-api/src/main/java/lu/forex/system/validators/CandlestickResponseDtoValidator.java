//package lu.forex.system.validators;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraintvalidation.SupportedValidationTarget;
//import jakarta.validation.constraintvalidation.ValidationTarget;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.Setter;
//import lu.forex.system.annotations.CandlestickRepresentation;
//import lu.forex.system.dtos.CandlestickResponseDto;
//
//@Getter(AccessLevel.PRIVATE)
//@Setter(AccessLevel.PRIVATE)
//@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
//public class CandlestickResponseDtoValidator implements ConstraintValidator<CandlestickRepresentation, CandlestickResponseDto> {
//
//  private static final String HIGH = "high";
//  private static final String LOW = "low";
//  private CandlestickRepresentation constraintAnnotation;
//
//  @Override
//  public void initialize(final CandlestickRepresentation constraintAnnotation) {
//    this.setConstraintAnnotation(constraintAnnotation);
//    ConstraintValidator.super.initialize(constraintAnnotation);
//  }
//
//  @Override
//  public boolean isValid(final @NotNull CandlestickResponseDto value, final ConstraintValidatorContext context) {
//    if (value.high() < value.low() || value.high() < value.open() || value.high() < value.close()) {
//      context.disableDefaultConstraintViolation();
//      context.buildConstraintViolationWithTemplate(this.getConstraintAnnotation().message()).addPropertyNode(HIGH).addConstraintViolation();
//      return false;
//    }
//    if (value.low() > value.open() || value.low() > value.close()) {
//      context.disableDefaultConstraintViolation();
//      context.buildConstraintViolationWithTemplate(this.getConstraintAnnotation().message()).addPropertyNode(LOW).addConstraintViolation();
//      return false;
//    }
//    return true;
//  }
//}

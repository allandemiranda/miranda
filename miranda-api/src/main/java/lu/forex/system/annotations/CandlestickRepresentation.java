package lu.forex.system.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lu.forex.system.validators.CandlestickEntityValidator;
import lu.forex.system.validators.CandlestickResponseDtoValidator;

@Constraint(validatedBy = {CandlestickResponseDtoValidator.class, CandlestickEntityValidator.class})
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CandlestickRepresentation {

  String message() default "{lu.forex.system.annotations.CandlestickRepresentation}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

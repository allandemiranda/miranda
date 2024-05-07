package lu.forex.system.annotations;

import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import lu.forex.system.validators.CandlestickValidator;

@Constraint(validatedBy = CandlestickValidator.class)
@Target({TYPE_USE, RECORD_COMPONENT})
@Retention(RUNTIME)
@Documented
public @interface CandlestickRepresentation {

  String message() default "Need represent a Candlestick where high >= (open || close) >= low price ";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

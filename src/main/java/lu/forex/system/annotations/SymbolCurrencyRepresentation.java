package lu.forex.system.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lu.forex.system.validators.SymbolCreateDtoValidator;
import lu.forex.system.validators.SymbolEntityValidator;
import lu.forex.system.validators.SymbolResponseDtoValidator;

@Constraint(validatedBy = {SymbolCreateDtoValidator.class, SymbolEntityValidator.class, SymbolResponseDtoValidator.class})
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SymbolCurrencyRepresentation {

  String message() default "Need represent a Symbol where Base and Quote currencies are different";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

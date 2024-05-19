package lu.forex.system.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lu.forex.system.validators.TickCreateDtoValidator;
import lu.forex.system.validators.TickEntityValidator;
import lu.forex.system.validators.TickResponseDtoValidator;

@Constraint(validatedBy = {TickCreateDtoValidator.class, TickEntityValidator.class, TickResponseDtoValidator.class})
@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TickRepresentation {

  String message() default "{lu.forex.system.annotations.TickRepresentation}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

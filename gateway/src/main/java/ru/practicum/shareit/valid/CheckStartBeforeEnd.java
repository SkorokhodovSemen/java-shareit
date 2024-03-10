package ru.practicum.shareit.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateValidator.class)
public @interface CheckStartBeforeEnd {
    String message() default "Старт должен быть до окончания и не равен null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

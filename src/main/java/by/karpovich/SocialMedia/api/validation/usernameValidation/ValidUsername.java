package by.karpovich.SocialMedia.api.validation.usernameValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {

    String message() default "Username already exist!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
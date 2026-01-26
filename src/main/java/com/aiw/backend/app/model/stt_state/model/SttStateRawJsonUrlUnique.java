package com.aiw.backend.app.model.stt_state.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.aiw.backend.app.model.stt_state.service.SttStateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the rawJsonUrl value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = SttStateRawJsonUrlUnique.SttStateRawJsonUrlUniqueValidator.class
)
public @interface SttStateRawJsonUrlUnique {

    String message() default "{exists.sttState.rawJsonUrl}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SttStateRawJsonUrlUniqueValidator implements ConstraintValidator<SttStateRawJsonUrlUnique, String> {

        private final SttStateService sttStateService;
        private final HttpServletRequest request;

        public SttStateRawJsonUrlUniqueValidator(final SttStateService sttStateService,
                final HttpServletRequest request) {
            this.sttStateService = sttStateService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(sttStateService.get(Long.parseLong(currentId)).getRawJsonUrl())) {
                // value hasn't changed
                return true;
            }
            return !sttStateService.rawJsonUrlExists(value);
        }

    }

}

package com.aiw.backend.app.model.stt_segment.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.aiw.backend.app.model.stt_segment.service.SttSegmentService;
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
 * Validate that the segText value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = SttSegmentSegTextUnique.SttSegmentSegTextUniqueValidator.class
)
public @interface SttSegmentSegTextUnique {

    String message() default "{exists.sttSegment.segText}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class SttSegmentSegTextUniqueValidator implements ConstraintValidator<SttSegmentSegTextUnique, String> {

        private final SttSegmentService sttSegmentService;
        private final HttpServletRequest request;

        public SttSegmentSegTextUniqueValidator(final SttSegmentService sttSegmentService,
                final HttpServletRequest request) {
            this.sttSegmentService = sttSegmentService;
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
            if (currentId != null && value.equalsIgnoreCase(sttSegmentService.get(Long.parseLong(currentId)).getSegText())) {
                // value hasn't changed
                return true;
            }
            return !sttSegmentService.segTextExists(value);
        }

    }

}

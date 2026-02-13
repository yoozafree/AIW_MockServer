package com.aiw.backend.app.model.meeting_speaker_map.dto;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.aiw.backend.app.model.meeting_speaker_map.service.MeetingSpeakerMapService;
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
 * Check that id is present and available when a new MeetingSpeakerMap is created.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = MeetingSpeakerMapIdValid.MeetingSpeakerMapIdValidValidator.class
)
public @interface MeetingSpeakerMapIdValid {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class MeetingSpeakerMapIdValidValidator
        implements ConstraintValidator<MeetingSpeakerMapIdValid, Long> {

        private final MeetingSpeakerMapService meetingSpeakerMapService;
        private final HttpServletRequest request;

        public MeetingSpeakerMapIdValidValidator(
                final MeetingSpeakerMapService meetingSpeakerMapService,
                final HttpServletRequest request) {
            this.meetingSpeakerMapService = meetingSpeakerMapService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Long value, final ConstraintValidatorContext cvContext) {
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null) {
                // only relevant for new objects
                return true;
            }
            String error = null;
            if (value == null) {
                // missing input
                error = "NotNull";
            } else if (meetingSpeakerMapService.idExists(value)) {
                error = "exists.meetingSpeakerMap.id";
            }
            if (error != null) {
                cvContext.disableDefaultConstraintViolation();
                cvContext.buildConstraintViolationWithTemplate("{" + error + "}")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }

    }

}

package com.aiw.backend.app.model.meeting_summary.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.aiw.backend.app.model.meeting_summary.service.MeetingSummaryService;
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
 * Validate that the meeting value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = MeetingSummaryMeetingUnique.MeetingSummaryMeetingUniqueValidator.class
)
public @interface MeetingSummaryMeetingUnique {

    String message() default "{Exists.meetingSummary.Meeting}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class MeetingSummaryMeetingUniqueValidator implements ConstraintValidator<MeetingSummaryMeetingUnique, Long> {

        private final MeetingSummaryService meetingSummaryService;
        private final HttpServletRequest request;

        public MeetingSummaryMeetingUniqueValidator(
                final MeetingSummaryService meetingSummaryService,
                final HttpServletRequest request) {
            this.meetingSummaryService = meetingSummaryService;
            this.request = request;
        }

        @Override
        public boolean isValid(final Long value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equals(meetingSummaryService.get(Long.parseLong(currentId)).getMeeting())) {
                // value hasn't changed
                return true;
            }
            return !meetingSummaryService.meetingExists(value);
        }

    }

}

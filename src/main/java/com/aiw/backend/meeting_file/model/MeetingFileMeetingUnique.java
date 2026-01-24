package com.aiw.backend.meeting_file.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.aiw.backend.meeting_file.service.MeetingFileService;
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
        validatedBy = MeetingFileMeetingUnique.MeetingFileMeetingUniqueValidator.class
)
public @interface MeetingFileMeetingUnique {

    String message() default "{Exists.meetingFile.Meeting}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class MeetingFileMeetingUniqueValidator implements ConstraintValidator<MeetingFileMeetingUnique, Long> {

        private final MeetingFileService meetingFileService;
        private final HttpServletRequest request;

        public MeetingFileMeetingUniqueValidator(final MeetingFileService meetingFileService,
                final HttpServletRequest request) {
            this.meetingFileService = meetingFileService;
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
            if (currentId != null && value.equals(meetingFileService.get(Long.parseLong(currentId)).getMeeting())) {
                // value hasn't changed
                return true;
            }
            return !meetingFileService.meetingExists(value);
        }

    }

}

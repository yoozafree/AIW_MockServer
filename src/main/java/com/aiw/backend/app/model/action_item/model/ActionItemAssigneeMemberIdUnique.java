package com.aiw.backend.app.model.action_item.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.aiw.backend.app.model.action_item.service.ActionItemService;
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
 * Validate that the assigneeMemberId value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = ActionItemAssigneeMemberIdUnique.ActionItemAssigneeMemberIdUniqueValidator.class
)
public @interface ActionItemAssigneeMemberIdUnique {

    String message() default "{exists.actionItem.assigneeMemberId}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class ActionItemAssigneeMemberIdUniqueValidator implements ConstraintValidator<ActionItemAssigneeMemberIdUnique, String> {

        private final ActionItemService actionItemService;
        private final HttpServletRequest request;

        public ActionItemAssigneeMemberIdUniqueValidator(final ActionItemService actionItemService,
                final HttpServletRequest request) {
            this.actionItemService = actionItemService;
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
            if (currentId != null && value.equalsIgnoreCase(actionItemService.get(Long.parseLong(currentId)).getAssigneeMemberId())) {
                // value hasn't changed
                return true;
            }
            return !actionItemService.assigneeMemberIdExists(value);
        }

    }

}

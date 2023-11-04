package com.uiys.jpa.support;

import com.uiys.jpa.valid.ValidateGroup;
import com.uiys.jpa.valid.ValidateResult;
import com.uiys.jpa.valid.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import org.springframework.util.CollectionUtils;

/**
 * @author uiys
 */
public abstract class BaseEntityOperation implements EntityOperation {

	static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory()
	  .getValidator();

	public BaseEntityOperation() {
	}

	public <T> void doValidate(T t, Class<? extends ValidateGroup> group) {
		Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(t, group, Default.class);
		if (!CollectionUtils.isEmpty(constraintViolations)) {
			List<ValidateResult> validateResults = new ArrayList<>();
			for (ConstraintViolation<T> violation : constraintViolations) {
				String filedName = violation == null ? "null" : violation.getPropertyPath() == null ? "null" :
				  violation.getPropertyPath()
					.toString();
				Object filedValue = violation == null ? "null" : violation.getInvalidValue() == null ? "null" :
				  violation.getInvalidValue();
				String msg = violation == null ? "null" : violation.getMessageTemplate() == null ? "null" :
				  violation.getMessageTemplate();
				validateResults.add(new ValidateResult(filedName, filedValue, msg));
			}
			throw new ValidationException(validateResults);
		}
	}
}

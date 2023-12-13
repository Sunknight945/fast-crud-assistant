package com.uiys.jpa.globalExceptionHandler;


import com.google.common.collect.Iterables;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.result.GeneralResult;
import com.uiys.jpa.valid.BusinessException;
import com.uiys.jpa.valid.ValidationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author uiys
 * 全局
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


	@ExceptionHandler(value = {BusinessException.class})
	public GeneralResult<Object> handler(BusinessException exception) throws Exception {

		return GeneralResult.setErrorResult(exception.getMsg()
		  .getName(), exception.getData());

	}

	@ExceptionHandler(ValidationException.class)
	public GeneralResult<Object> handleValidateException(ValidationException validationException) {
		if (Iterables.size(validationException.getResult()) > 0) {

			return GeneralResult.setErrorResult(validationException.toString(), validationException.getResult());
		}
		return GeneralResult.setErrorResult("校验问题", "校验");
	}

	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public GeneralResult<Object> handler(MethodArgumentNotValidException e) throws Exception {
		BindingResult bindingResult = e.getBindingResult();
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		StringBuilder str = new StringBuilder();
		for (FieldError fieldError : fieldErrors) {
			str.append("字段:")
			  .append(fieldError.getField())
			  .append(" ")
			  .append("原因:")
			  .append(fieldError.getDefaultMessage())
			  .append(" ")
			  .append("填充值:")
			  .append(fieldError.getRejectedValue())
			  .append(".");
		}
		return GeneralResult.setErrorResult(str.toString(), ErrorCode.ERROR_CODE);
	}

	@ExceptionHandler(value = {Exception.class})
	public GeneralResult<Object> handler(Exception e) throws Exception {
		if (e.getClass()
		  .equals(UndeclaredThrowableException.class)) {
			UndeclaredThrowableException e1 = (UndeclaredThrowableException) e;
			Throwable undeclaredThrowable = e1.getUndeclaredThrowable();
			if (undeclaredThrowable.getClass()
			  .equals(InvocationTargetException.class)) {
				InvocationTargetException invocationTargetException = (InvocationTargetException) undeclaredThrowable;
				System.out.println("invocationTargetException = " + invocationTargetException);
				Throwable targetException = invocationTargetException.getTargetException();
				if (invocationTargetException.getTargetException()
				  .getClass()
				  .equals(BusinessException.class)) {
					BusinessException targetException1 = (BusinessException) targetException;
					return GeneralResult.setErrorResult(targetException1.getMessage(), targetException1.getData());
				}
			}
			return GeneralResult.setErrorResult(undeclaredThrowable.getMessage(), undeclaredThrowable.getMessage());
		} else if (e.getClass()
		  .isAssignableFrom(IllegalArgumentException.class)) {
			return GeneralResult.setErrorResult(e.getMessage(), e.getCause()
			  .getCause());
		}
		throw e;
	}


}



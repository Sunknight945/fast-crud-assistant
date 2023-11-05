package com.uiys.jpa.globalExceptionHandler;


import com.google.common.collect.Iterables;
import com.uiys.jpa.result.GeneralResult;
import com.uiys.jpa.valid.BusinessException;
import com.uiys.jpa.valid.ValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author uiys
 * 全局
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = {Exception.class})
	public GeneralResult<Object> handler(Exception e) {
		if (e.getClass()
		  .equals(BusinessException.class)) {
			BusinessException e1 = (BusinessException) e;
			return GeneralResult.setErrorResult(e1.getMsg()
			  .getName(), e1.getData());
		} else if (e.getClass()
		  .equals(ValidationException.class)) {
			return GeneralResult.setErrorResult(e.getMessage(), e.getCause()
			  .getCause());
		} else if (e.getClass()
		  .equals(IllegalArgumentException.class)) {
			return GeneralResult.setErrorResult(e.getMessage(), e.getCause()
			  .getCause());
		}
		return GeneralResult.setErrorResult(e.getMessage(), e);
	}

	@ExceptionHandler(ValidationException.class)
	public GeneralResult<Object> handleValidateException(ValidationException validationException) {
		if (Iterables.size(validationException.getResult()) > 0) {

			return GeneralResult.setErrorResult(validationException.toString(), validationException.getResult());
		}
		return GeneralResult.setErrorResult("校验问题", "校验");
	}


}



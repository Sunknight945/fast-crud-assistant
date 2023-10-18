package com.uiys.jpa.valid;

import java.util.List;


public class ValidationException extends RuntimeException {
	private final List<ValidateResult> result;

	public ValidationException(List<ValidateResult> list) {
		this.result = list;
	}

	public List<ValidateResult> getResult() {
		return this.result;
	}
}

package com.uiys.jpa.valid;

import java.util.List;


public class ValidationException extends RuntimeException {

	private List<ValidateResult> result;

	public ValidationException(List<ValidateResult> result) {
		this.result = result;
	}


	public List<ValidateResult> getResult() {
		return result;
	}

	public void setResult(List<ValidateResult> result) {
		this.result = result;
	}


}


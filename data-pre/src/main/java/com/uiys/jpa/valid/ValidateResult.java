package com.uiys.jpa.valid;


public final class ValidateResult {

	private String filed;
	private Object iVal;
	private String eMsg;

	public ValidateResult(String filed, Object iVal, String eMsg) {
		this.filed = filed;
		this.iVal = iVal;
		this.eMsg = eMsg;
	}

	public String getFiled() {
		return filed;
	}

	public void setFiled(String filed) {
		this.filed = filed;
	}

	public Object getiVal() {
		return iVal;
	}

	public void setiVal(Object iVal) {
		this.iVal = iVal;
	}

	public String geteMsg() {
		return eMsg;
	}

	public void seteMsg(String eMsg) {
		this.eMsg = eMsg;
	}
}



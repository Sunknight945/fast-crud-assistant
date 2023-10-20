package com.uiys.jpa.valid;


public final class ValidateResult {

	private String filed;
	private Object val;
	private String msg;

	public ValidateResult(String filed, Object val, String msg) {
		this.filed = filed;
		this.val = val;
		this.msg = msg;
	}

	public String getFiled() {
		return filed;
	}

	public void setFiled(String filed) {
		this.filed = filed;
	}

	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}



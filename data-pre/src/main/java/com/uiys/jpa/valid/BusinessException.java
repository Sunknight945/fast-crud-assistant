package com.uiys.jpa.valid;

import com.uiys.jpa.constant.BaseEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private final BaseEnum msg;
	private Object data;

	public BusinessException(BaseEnum msg) {
		super(msg.getName());
		this.msg = msg;
	}

	public BusinessException(BaseEnum msg, Object data) {
		super(msg.getName());
		this.msg = msg;
		this.data = data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "BusinessException{" +
		  "msg=" + msg +
		  ", data=" + data +
		  '}';
	}
}

package com.uiys.jpa.constant;


public enum ErrorCode implements BaseEnum {

	NOTFOUND(1, "找不到"),
	CAST_ERROR(2, "转换错误"),
	ERROR_CODE(3, "未知原因"),
	CREATE_ERROR(4, "创建失败"),
	;

	private final Integer code;
	private final String name;

	ErrorCode(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Integer getCode() {
		return this.code;
	}
}



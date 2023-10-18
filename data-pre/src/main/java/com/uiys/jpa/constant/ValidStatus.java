package com.uiys.jpa.constant;

import java.util.Optional;


public enum ValidStatus implements BaseEnum {
	INVALID(-1, "失效的"),
	EFFECTIVE(1, "有效的"),
	;

	private final Integer code;
	private final String name;


	ValidStatus(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	public static Optional<ValidStatus> of(Integer code) {
		return BaseEnum.parseEnumByCode(ValidStatus.class, code);
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

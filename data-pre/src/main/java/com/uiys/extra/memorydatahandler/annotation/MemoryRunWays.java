package com.uiys.extra.memorydatahandler.annotation;

import com.uiys.jpa.constant.BaseEnum;
import java.util.Optional;

public enum MemoryRunWays implements BaseEnum {

	Serial(1, "串行(默认的)"),
	Parallel(2, "并行"),

	;

	MemoryRunWays(Integer code, String name) {
		this.code = code;
		this.name = name;
	}

	private Integer code;
	private String name;

	@Override
	public Integer getCode() {
		return this.code;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public static Optional<MemoryRunWays> of(Integer code) {
		return BaseEnum.parseEnumByCode(MemoryRunWays.class, code);
	}

}
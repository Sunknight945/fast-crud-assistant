package com.uiys.extra.joininmemory.annotation;

import com.uiys.jpa.constant.BaseEnum;
import java.util.Optional;

/**
 * 串行/并行处理
 */
public enum JoinInMemoryExecutorType implements BaseEnum {

	SERIAL(1, "串行"),
	PARALLEL(2, "并行"),

	;

	JoinInMemoryExecutorType(Integer code, String name) {
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

	public static Optional<JoinInMemoryExecutorType> of(Integer code) {
		return BaseEnum.parseEnumByCode(JoinInMemoryExecutorType.class, code);
	}

}
package com.uiys.jpa.constant;

import java.util.Optional;


public interface BaseEnum {

	static <E extends BaseEnum> Optional<E> parseEnumByCode(Class<E> clazz, Integer code) {
		E[] enumConstants = clazz.getEnumConstants();
		for (E enumConstant : enumConstants) {
			if (enumConstant.getCode()
			  .equals(code)) {
				return Optional.of(enumConstant);
			}
		}
		return Optional.empty();
	}

	String getName();

	Integer getCode();

}

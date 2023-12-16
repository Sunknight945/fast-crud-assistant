package com.uiys.jpa.constant;

import java.util.Arrays;
import java.util.Optional;


public interface BaseEnum {

	static <E extends BaseEnum> Optional<E> parseEnumByCode(Class<E> clazz, Integer code) {
		return Arrays.stream(clazz.getEnumConstants())
		  .filter(item -> item.getCode()
			.equals(code))
		  .findFirst();
	}

	String getName();

	Integer getCode();

}

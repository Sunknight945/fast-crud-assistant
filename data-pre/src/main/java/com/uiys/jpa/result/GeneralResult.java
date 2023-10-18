package com.uiys.jpa.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode
@ToString
public class GeneralResult<E> {

	private Integer code;
	private String msg;
	private E result;

	private GeneralResult() {

	}

	public static <E> GeneralResult<E> setNormalResult(E e) {
		return setNormalResult(e, "ok");
	}

	public static <E> GeneralResult<E> setNormalResult(E e, String msg) {
		GeneralResult<E> generalResult = new GeneralResult<>();
		generalResult.setCode(200);
		generalResult.setMsg(msg);
		generalResult.setResult(e);
		return generalResult;
	}

	public static <E> GeneralResult<E> setErrorResult(String msg, E e) {
		GeneralResult<E> generalResult = new GeneralResult<>();
		generalResult.setCode(500);
		generalResult.setMsg(msg);
		generalResult.setResult(e);
		return generalResult;
	}

	public GeneralResult<E> setErrorResult(E e) {
		return setErrorResult("error", e);
	}

}



package com.uiys.extra.joininmemory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author uiys
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinInMemory {

	/**
	 * 从sourceData中提取key
	 */
	String keyFromSourceDate();

	/**
	 * 从joinData中提取key
	 */
	String keyFromJoinData();

	/**
	 * 批量数据提取
	 */
	String loader();

	/**
	 * 批量数据转换
	 */
	String joinDataConvert();

	/**
	 * 运行级别: 同一级别的 join 可并行执行
	 */
	int runLevel() default 10;

}

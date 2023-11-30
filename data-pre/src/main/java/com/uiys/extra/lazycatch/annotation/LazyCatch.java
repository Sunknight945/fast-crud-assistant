package com.uiys.extra.lazycatch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author uiys
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LazyCatch {

	/**
	 * 根据SpeL表达式, 哪个类什么方法抓, 要抓取数据的key值如何获取?
	 *
	 * @return 如 #{\@couponRepository.getById(sku.id)}
	 */
	String loader();

}

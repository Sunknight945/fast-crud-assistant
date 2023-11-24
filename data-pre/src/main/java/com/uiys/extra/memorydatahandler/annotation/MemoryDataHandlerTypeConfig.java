package com.uiys.extra.memorydatahandler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author uiys
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemoryDataHandlerTypeConfig {

	MemoryRunWays runWay() default MemoryRunWays.Serial;

}

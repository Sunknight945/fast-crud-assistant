package com.uiys.extra.joininmemory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author uiys
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JoinInMemoryConfig {
	JoinInMemoryExecutorType executorType() default JoinInMemoryExecutorType.SERIAL;

	String executorName() default "defaultExecutor";

}



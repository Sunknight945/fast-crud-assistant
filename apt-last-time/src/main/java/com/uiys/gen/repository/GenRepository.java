package com.uiys.gen.repository;

/**
 * @author uiys
 * @date 2023/10/17
 */
public @interface GenRepository {
	String pkg();

	String sourcePath() default "src/main/java";

	boolean overrideSource() default false;
}

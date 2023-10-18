package com.uiys.gen.mapper;

/**
 * @author uiys
 * @date 2023/10/17
 */
public @interface GenMapper {
	String pkg();

	String sourcePath() default "src/main/java";

	boolean overrideSource() default false;
}

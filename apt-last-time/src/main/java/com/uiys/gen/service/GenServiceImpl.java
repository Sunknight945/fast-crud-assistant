package com.uiys.gen.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenServiceImpl {

  String pkg();

  String sourcePath() default "src/main/java";

  boolean overrideSource() default false;
}

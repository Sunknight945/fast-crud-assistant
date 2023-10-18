package com.uiys.gen.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenFeign {

  String pkg();

  String serverName() default "nameServer";

  String sourcePath() default "src/main/java";

  boolean overrideSource() default false;
}

package com.uiys.spi;

import java.lang.annotation.Annotation;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;


public interface CodeGenProcessor {

	Class<? extends Annotation> getAnnotation();

	String getPackageName(TypeElement typeElement);

	void generate(TypeElement typeElement, RoundEnvironment environment);

}



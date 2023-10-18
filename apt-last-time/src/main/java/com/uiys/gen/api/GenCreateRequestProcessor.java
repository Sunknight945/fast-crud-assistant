package com.uiys.gen.api;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.creator.IgnoreCreator;
import com.uiys.jpa.controller.Request;
import com.uiys.spi.CodeGenProcessor;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author uiys
 */
@AutoService(CodeGenProcessor.class)
public class GenCreateRequestProcessor extends AbstractCodeGenProcessor {

	public static final String CreateRequest = "CreateRequest";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		Set<VariableElement> variableElements = findFields(typeElement,
		  ve -> Objects.isNull(ve.getAnnotation(IgnoreCreator.class)));
		String currentHandClassName = getCurrentHandClassName(typeElement) + CreateRequest;
		TypeSpec.Builder builder = TypeSpec.classBuilder(currentHandClassName)
		  .addSuperinterface(Request.class)
		  .addModifiers(Modifier.PUBLIC);
		addFieldSetGetMethod(variableElements, builder);
		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenCreateRequest.class)
		  .sourcePath(), builder);
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenCreateRequest.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenCreateRequest.class)
		  .pkg();
	}
}



package com.uiys.gen.api;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.gen.query.GenQueryIgnore;
import com.uiys.jpa.controller.Request;
import com.uiys.spi.CodeGenProcessor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;


/**
 * @author uiys
 * @date 2023/10/17
 */
@AutoService(CodeGenProcessor.class)
public class GenQueryRequestProcessor extends AbstractCodeGenProcessor {

	public static final String QueryRequest = "QueryRequest";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		DefaultNameContext nameContext = getNameContext(typeElement);
		Set<VariableElement> variableElements = findFields(typeElement,
		  ve -> Objects.isNull(ve.getAnnotation(GenQueryIgnore.class)));
		TypeSpec.Builder builder = TypeSpec.classBuilder(nameContext.getQueryRequestClassName())
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Schema.class)
		  .addSuperinterface(Request.class);

		addFieldSetGetMethodWithConvert(variableElements, builder);

		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenQueryRequest.class)
		  .sourcePath(), builder);

	}


	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenQueryRequest.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenQueryRequest.class)
		  .pkg();
	}
}



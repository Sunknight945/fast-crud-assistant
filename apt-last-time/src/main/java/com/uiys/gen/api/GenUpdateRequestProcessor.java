package com.uiys.gen.api;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.gen.updater.IgnoreUpdater;
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

@AutoService(value = CodeGenProcessor.class)
public class GenUpdateRequestProcessor extends AbstractCodeGenProcessor {

	public static String UpdateRequest = "UpdateRequest";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		DefaultNameContext nameContext = getNameContext(typeElement);
		Set<VariableElement> fields = findFields(typeElement,
		  p -> Objects.isNull(p.getAnnotation(IgnoreUpdater.class)));
		TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(nameContext.getUpdateClassName())
		  .addModifiers(Modifier.PUBLIC)
		  .addSuperinterface(Request.class)
		  .addAnnotation(Schema.class);

		addFieldSetGetMethodWithConvert(fields, typeSpecBuilder);
		TypeName idTypeName = getTableIdTypeName(typeElement);
		addIdSetterAndGetter(typeSpecBuilder, idTypeName);
		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenUpdateRequest.class)
		  .sourcePath(), typeSpecBuilder);
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenUpdateRequest.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenUpdateRequest.class)
		  .pkg();
	}



}

package com.uiys.gen.creator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.spi.CodeGenProcessor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.Data;


@AutoService(CodeGenProcessor.class)
public class GenCreatorProcessor extends AbstractCodeGenProcessor {

	public static final String next = "Creator";


	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		String creatorName = Base + typeElement.getSimpleName()
		  .toString() + next;
		String sourceName =  typeElement.getSimpleName()
		  .toString() + next;

		TypeSpec.Builder typeBuild = TypeSpec.classBuilder(creatorName)
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Schema.class)
		  .addAnnotation(Data.class);
		Set<VariableElement> variableElements = findFields(typeElement,
		  ve -> Objects.isNull(ve.getAnnotation(IgnoreCreator.class)) && !dtoIgnore(ve));
		addFieldSetGetMethod(variableElements, typeBuild);
		String packageName = getPackageName(typeElement);
		genJavaFile(packageName, typeBuild);
		genJavaFile(packageName,getSourceType(sourceName, packageName, creatorName));


	}

	private boolean dtoIgnore(VariableElement ve) {
		return CLASSES.contains(TypeName.get(ve.getClass())) || ve.getModifiers()
		  .contains(Modifier.STATIC);
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenCreator.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenCreator.class)
		  .pkg();
	}

	public static final List<TypeName> CLASSES = new ArrayList<>();

	static {
		CLASSES.add(TypeName.get(Date.class));
		CLASSES.add(TypeName.get(LocalDateTime.class));
	}
}



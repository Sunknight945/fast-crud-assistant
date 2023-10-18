package com.uiys.gen.query;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.spi.CodeGenProcessor;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.Data;


/**
 * @author uiys
 * @date 2023/10/17
 */
@AutoService(CodeGenProcessor.class)
public class GenQueryProcessor extends AbstractCodeGenProcessor {

	public static final String Query = "Query";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		String currentHandClassName = getCurrentHandClassName(typeElement);
		String BaseCcQuery = Base + currentHandClassName + Query;
		String CcQuery = currentHandClassName + Query;
		Set<VariableElement> fields = findFields(typeElement,
		  ve -> Objects.isNull(ve.getAnnotation(GenQueryIgnore.class)));
		TypeSpec.Builder builder = TypeSpec.classBuilder(BaseCcQuery)
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Data.class);
		addFieldSetGetMethod(fields, builder);

		String packageName = getPackageName(typeElement);
		genJavaFile(packageName, builder);
		genJavaFile(packageName, getSourceType(CcQuery, packageName, BaseCcQuery));


	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenQuery.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenQuery.class)
		  .pkg();
	}
}



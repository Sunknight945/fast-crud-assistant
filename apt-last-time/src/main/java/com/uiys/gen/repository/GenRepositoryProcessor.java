package com.uiys.gen.repository;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.jpa.support.BaseJpaRepository;
import com.uiys.spi.CodeGenProcessor;
import java.lang.annotation.Annotation;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @author uiys
 * @date 2023/10/17
 */
@AutoService(CodeGenProcessor.class)
public class GenRepositoryProcessor extends AbstractCodeGenProcessor {


	public static final String Repository = "Repository";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {

		String cRepositoryName = typeElement.getSimpleName()
		  .toString() + Repository;

		TypeName itself = ClassName.get(typeElement);

		TypeName idTypeName = getTableIdTypeName(typeElement);

		TypeSpec.Builder builder = TypeSpec.interfaceBuilder(cRepositoryName)
		  .addModifiers(Modifier.PUBLIC)
		  .addSuperinterface(ParameterizedTypeName.get(ClassName.get(BaseJpaRepository.class), itself, idTypeName));

		String packageName = getPackageName(typeElement);

		genJavaSourceFile(packageName, typeElement.getAnnotation(GenRepository.class)
		  .sourcePath(), builder);
	}


	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenRepository.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenRepository.class)
		  .pkg();
	}
}



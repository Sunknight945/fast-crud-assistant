package com.uiys.gen.vo;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.jpa.support.AbstractBaseJpaVo;
import com.uiys.spi.CodeGenProcessor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.Data;


@AutoService(CodeGenProcessor.class)
public class GenVoProcessor extends AbstractCodeGenProcessor {

	public static final String Vo = "VO";

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenVo.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenVo.class)
		  .pkg();
	}


	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		TypeName typeName = TypeName.get(typeElement.asType());
		String simpleName = (Base + typeElement.getSimpleName()
		  .toString() + Vo);
		String sourceClassName = typeElement.getSimpleName()
		  .toString() + Vo;
		Set<VariableElement> variableElements = findFields(typeElement,
		  ve -> Objects.isNull(ve.getAnnotation(IgnoreVo.class)));
		TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(simpleName)
		  .superclass(AbstractBaseJpaVo.class)
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Data.class)
		  .addAnnotation(Schema.class);
		addFieldSetGetMethod(variableElements, typeBuilder);


		typeBuilder.addMethod(MethodSpec.constructorBuilder()
		  .addModifiers(Modifier.PROTECTED)
		  .build());

		MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
		  .addParameter(typeName, "source")
		  .addModifiers(Modifier.PUBLIC);
		constructorBuilder.addStatement("super(source)");
		for (VariableElement element : variableElements) {
			constructorBuilder.addStatement("this.set$L(source.get$L())", getMethodDefaultName(element) , getMethodDefaultName(element));
		}
		typeBuilder.addMethod(constructorBuilder.build());
		String packageName = getPackageName(typeElement);
		genJavaFile(packageName, typeBuilder);
		genJavaFile(packageName, getSourceTypeWithConstruct(typeElement, sourceClassName, packageName, simpleName));

	}


}

































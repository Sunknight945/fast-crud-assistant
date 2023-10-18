package com.uiys.gen.updater;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.spi.CodeGenProcessor;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;


@AutoService(CodeGenProcessor.class)
public class GenUpdaterProcessor extends AbstractCodeGenProcessor {

	public static final String Update = "Updater";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		String cClassName = typeElement.getSimpleName()
		  .toString();
		String baseCcCreatorName = Base + cClassName + Update;
		String ccCreatorName = cClassName + Update;
		Set<VariableElement> variableElements = findFields(typeElement,
		  ve -> Objects.isNull(ve.getAnnotation(IgnoreUpdater.class)));
		TypeSpec.Builder builder = TypeSpec.classBuilder(baseCcCreatorName)
		  .addModifiers(Modifier.PUBLIC);
		addIdSetterAndGetter(builder, getTableIdTypeName(typeElement));
		addLoadThingMethod(variableElements, typeElement, builder);
		addFieldSetGetMethod(variableElements, builder);
		String packageName = getPackageName(typeElement);
		genJavaFile(packageName, builder);
		genJavaFile(packageName, getSourceType(ccCreatorName, packageName, baseCcCreatorName));

	}

	@Override
	protected String getMethodDefaultName(VariableElement ve) {
		return super.getMethodDefaultName(ve);
	}

	private void addLoadThingMethod(Set<VariableElement> variableElements, TypeElement typeElement,
	                               TypeSpec.Builder builder) {
		String pName = typeElement.getSimpleName()
		  .toString()
		  .substring(0, 1)
		  .toLowerCase() + typeElement.getSimpleName()
		  .toString()
		  .substring(1);

		MethodSpec.Builder loadThingMethod = MethodSpec.methodBuilder("load" + typeElement.getSimpleName()
			.toString())
		  .returns(void.class)
		  .addModifiers(Modifier.PUBLIC)
		  .addParameter(ClassName.get(typeElement), pName);

		for (VariableElement ve : variableElements) {
			String get = "get" + getMethodDefaultName(ve);
			String set = "set" + getMethodDefaultName(ve);
			CodeBlock codeBlock = CodeBlock.of("$T.ofNullable($L())\n" + "\t.ifPresent(f -> $L.$L(f)); \n\n",
			  ClassName.get(Optional.class), get, pName, set);
			loadThingMethod.addCode(codeBlock);
		}
		builder.addMethod(loadThingMethod.build());

	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenUpdater.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenUpdater.class)
		  .pkg();
	}
}



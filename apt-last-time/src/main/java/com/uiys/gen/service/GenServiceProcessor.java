package com.uiys.gen.service;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.jpa.request.PageRequestWrapper;
import com.uiys.spi.CodeGenProcessor;
import com.uiys.util.StringUtils;
import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.springframework.data.domain.Page;

/**
 * @author uiys
 * @date 2023/10/17
 */
@AutoService(CodeGenProcessor.class)
public class GenServiceProcessor extends AbstractCodeGenProcessor {
	public static final String Service = "Service";

	public static final String I = "I";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		String serviceInterfaceName = I + typeElement.getSimpleName()
		  .toString() + Service;
		TypeSpec.Builder builder = TypeSpec.interfaceBuilder(serviceInterfaceName)
		  .addModifiers(Modifier.PUBLIC);
		DefaultNameContext context = getNameContext(typeElement);

		addUpdateXxMethod(context, builder, typeElement);
		addCreateXxMethod(context, builder, typeElement);
		addValidXxMethod(context, builder, typeElement);
		addInValidXxMethod(context, builder, typeElement);
		addFindByIdMethod(context, builder, typeElement);
		addFindByPageXxMethod(context, builder, typeElement);

		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenService.class)
		  .sourcePath(), builder);
	}

	private void addFindByPageXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {

		MethodSpec findByPage = MethodSpec.methodBuilder("findByPage")
		  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
		  .addParameter(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
			ClassName.get(context.getQueryPackageName(), context.getQueryClassName())), "wrapper")
		  .returns(ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(typeElement)))
		  .build();
		builder.addMethod(findByPage);
	}

	private void addFindByIdMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		MethodSpec findById = MethodSpec.methodBuilder("findById")
		  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
		  .addParameter(getTableIdTypeName(typeElement), "id")
		  .returns(ClassName.get(typeElement))
		  .build();
		builder.addMethod(findById);
	}

	private void addInValidXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		MethodSpec inValid = MethodSpec.methodBuilder("inValid" + typeElement.getSimpleName()
			.toString())
		  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
		  .addParameter(getTableIdTypeName(typeElement), "id")
		  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)))
		  .build();
		builder.addMethod(inValid);
	}

	private void addValidXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		MethodSpec validXxMethod = MethodSpec.methodBuilder("valid" + typeElement.getSimpleName()
			.toString())
		  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
		  .addParameter(getTableIdTypeName(typeElement), "id")
		  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)))
		  .build();
		builder.addMethod(validXxMethod);
	}

	private void addCreateXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		boolean contained = StringUtils.containsNull(context.getCreatePackageName(), context.getCreatorPackageName());
		if (!contained) {
			MethodSpec build = MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)))
			  .addParameter(ClassName.get(context.getCreatorPackageName(), context.getCreatorClassName()), "creator")
			  .build();
			builder.addMethod(build);

		}
	}

	/**
	 * 更新 接口
	 */
	private void addUpdateXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		boolean contained = StringUtils.containsNull(context.getUpdaterPackageName(), context.getUpdatePackageName());
		if (!contained) {
			MethodSpec build = MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)))
			  .addParameter(ClassName.get(context.getUpdaterPackageName(), context.getUpdaterClassName()), "updater")
			  .build();
			builder.addMethod(build);

		}
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenService.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenService.class)
		  .pkg();
	}
}



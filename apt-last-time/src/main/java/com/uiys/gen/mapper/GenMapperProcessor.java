package com.uiys.gen.mapper;

import cn.hutool.core.util.StrUtil;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.jpa.convert.DateLong2Instant;
import com.uiys.jpa.convert.ValidStatusConverter;
import com.uiys.spi.CodeGenProcessor;
import com.uiys.util.StringUtils;
import java.lang.annotation.Annotation;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author uiys
 * @date 2023/10/17
 */
@AutoService(CodeGenProcessor.class)
public class GenMapperProcessor extends AbstractCodeGenProcessor {
	public static final String tMapper = "Mapper";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {


		TypeSpec.Builder builder = genInstance(typeElement);
		DefaultNameContext context = getNameContext(typeElement);
		methodR2U(context, typeElement, builder);
		methodR2C(context, typeElement, builder);
		methodU2E(context, typeElement, builder);
		methodR2Q(context, typeElement, builder);
		genJavaFile(getPackageName(typeElement), builder);
	}

	/**
	 * reqeust to Query (domain)
	 */
	private void methodR2Q(DefaultNameContext context, TypeElement typeElement, TypeSpec.Builder builder) {
		boolean containsNull = StringUtils.containsNull(context.getQueryPackageName(),
		  context.getQueryRequestPackageName());
		if (!containsNull) {
			MethodSpec build = MethodSpec.methodBuilder("r2Q")
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .returns(ClassName.get(context.getQueryPackageName(), context.getQueryClassName()))
			  .addParameter(ClassName.get(context.getQueryRequestPackageName(), context.getQueryRequestClassName()), "query")
			  .build();
			builder.addMethod(build);
		}

	}

	/**
	 * update to entity
	 */
	private void methodU2E(DefaultNameContext context, TypeElement typeElement, TypeSpec.Builder builder) {
		boolean containsNull = StringUtils.containsNull(context.getUpdaterPackageName(),
		  context.getUpdateClassName());
		if (!containsNull) {
			MethodSpec build = MethodSpec.methodBuilder("u2E")
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .returns(ClassName.get(typeElement))
			  .addParameter(ClassName.get(context.getCreatorPackageName(), context.getCreatorClassName()), "reqeust")
			  .build();
			builder.addMethod(build);
		}
	}

	/**
	 * (create)reqeust to create Dto
	 */
	private void methodR2C(DefaultNameContext context, TypeElement typeElement, TypeSpec.Builder builder) {
		if (StrUtil.isNotBlank(context.getCreatorPackageName()) && StrUtil.isNotBlank(context.getCreatePackageName())) {
			MethodSpec build = MethodSpec.methodBuilder("r2C")
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .returns(ClassName.get(context.getCreatorPackageName(), context.getCreatorClassName()))
			  .addParameter(ClassName.get(context.getCreatePackageName(), context.getCreateClassName()), "reqeust")
			  .build();
			builder.addMethod(build);
		}
	}

	/**
	 * (update)reqeust to update Dto
	 */
	private void methodR2U(DefaultNameContext context, TypeElement typeElement, TypeSpec.Builder builder) {
		boolean containsNull = StringUtils.containsNull(context.getUpdaterPackageName(),
		  context.getUpdatePackageName());
		if (!containsNull) {
			MethodSpec build = MethodSpec.methodBuilder("r2U")
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .returns(ClassName.get(context.getUpdaterPackageName(), context.getUpdaterClassName()))
			  .addParameter(ClassName.get(context.getUpdatePackageName(), context.getUpdateClassName()), "reqeust")
			  .build();
			builder.addMethod(build);
		}

	}

	private TypeSpec.Builder genInstance(TypeElement typeElement) {
		DefaultNameContext nameContext = getNameContext(typeElement);
		String nameMapper = typeElement.getSimpleName()
		  .toString() + tMapper;
		AnnotationSpec annotationSpec = AnnotationSpec.builder(Mapper.class)
		  .addMember("uses", "$T.class", ClassName.get(DateLong2Instant.class))
		  .addMember("uses", "$T.class", ClassName.get(ValidStatusConverter.class))
		  .build();

		TypeSpec.Builder builder = TypeSpec.interfaceBuilder(nameMapper)
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(annotationSpec);
		ClassName className1 = ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName());
		FieldSpec build = FieldSpec.builder(className1, "INSTANCE")
		  .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		  .initializer(CodeBlock.of("$T.getMapper($T.class) \n", ClassName.get(Mappers.class), className1))
		  .build();
		builder.addField(build);
		return builder;
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenMapper.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenMapper.class)
		  .pkg();
	}
}



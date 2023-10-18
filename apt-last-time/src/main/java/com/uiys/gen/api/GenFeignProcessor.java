package com.uiys.gen.api;

import cn.hutool.db.PageResult;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.jpa.request.PageRequestWrapper;
import com.uiys.jpa.result.GeneralResult;
import com.uiys.spi.CodeGenProcessor;
import com.uiys.util.StringUtils;
import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AutoService(value = CodeGenProcessor.class)
public class GenFeignProcessor extends AbstractCodeGenProcessor {

	public static String FeignService = "FeignService";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment roundEnvironment) {
		DefaultNameContext nameContext = getNameContext(typeElement);
		String classFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, typeElement.getSimpleName()
		  .toString());
		GenFeign feign = typeElement.getAnnotation(GenFeign.class);
		TypeSpec.Builder builder = TypeSpec.interfaceBuilder(nameContext.getFeignClassName())
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(AnnotationSpec.builder(FeignClient.class)
			.addMember("value", "$S", feign.serverName())
			.addMember("contextId", "$S", classFieldName + "Client")
			.addMember("path", "$S", classFieldName + "/v1")
			.build());
		Optional<MethodSpec> createMethod = createMethod(typeElement, nameContext);
		createMethod.ifPresent(builder::addMethod);
		Optional<MethodSpec> updateMethod = updateMethod(typeElement, nameContext);
		updateMethod.ifPresent(builder::addMethod);
		Optional<MethodSpec> validMethod = validMethod(typeElement);
		validMethod.ifPresent(builder::addMethod);
		Optional<MethodSpec> invalidMethod = invalidMethod(typeElement);
		invalidMethod.ifPresent(builder::addMethod);
		Optional<MethodSpec> findById = findById(nameContext, typeElement);
		findById.ifPresent(builder::addMethod);
		Optional<MethodSpec> findByPage = findByPage(nameContext);
		findByPage.ifPresent(builder::addMethod);
		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenFeign.class)
		  .sourcePath(), builder);
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenFeign.class;
	}


	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenFeign.class)
		  .pkg();
	}

	private Optional<MethodSpec> createMethod(TypeElement typeElement, DefaultNameContext nameContext) {
		boolean containsNull = StringUtils.containsNull(nameContext.getCreatePackageName());
		if (!containsNull) {
			return Optional.of(MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
			  .addParameter(ParameterSpec.builder(ClassName.get(nameContext.getCreatePackageName(),
				  nameContext.getCreateClassName()), "request")
				.addAnnotation(RequestBody.class)
				.build())
			  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
				.addMember("value", "$S", "create" + typeElement.getSimpleName())
				.build())
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .addJavadoc("createRequest")
			  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class), getTableIdTypeName(typeElement)))
			  .build());
		}
		return Optional.empty();
	}

	private Optional<MethodSpec> updateMethod(TypeElement typeElement, DefaultNameContext nameContext) {
		boolean containsNull = StringUtils.containsNull(nameContext.getUpdatePackageName());
		if (!containsNull) {
			return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
			  .addParameter(ParameterSpec.builder(ClassName.get(nameContext.getUpdatePackageName(),
				  nameContext.getUpdateClassName()), "request")
				.addAnnotation(RequestBody.class)
				.build())
			  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
				.addMember("value", "$S", "update" + typeElement.getSimpleName())
				.build())
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class), ClassName.get(String.class)))
			  .addJavadoc("update request")
			  .build());
		}
		return Optional.empty();
	}

	private Optional<MethodSpec> validMethod(TypeElement typeElement) {
		return Optional.of(MethodSpec.methodBuilder("valid" + typeElement.getSimpleName())
		  .addParameter(ParameterSpec.builder(getTableIdTypeName(typeElement), "id")
			.addAnnotation(AnnotationSpec.builder(PathVariable.class)
			  .addMember("value", "$S", "id")
			  .build())
			.build())
		  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
			.addMember("value", "$S", "valid/{id}")
			.build())
		  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
		  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class), ClassName.get(String.class)))
		  .addJavadoc("valid")
		  .build());
	}

	private Optional<MethodSpec> invalidMethod(TypeElement typeElement) {
		return Optional.of(MethodSpec.methodBuilder("invalid" + typeElement.getSimpleName())
		  .addParameter(ParameterSpec.builder(getTableIdTypeName(typeElement), "id")
			.addAnnotation(AnnotationSpec.builder(PathVariable.class)
			  .addMember("value", "$S", "id")
			  .build())
			.build())
		  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
			.addMember("value", "$S", "invalid/{id}")
			.build())
		  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
		  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class), ClassName.get(String.class)))
		  .addJavadoc("invalid")
		  .build());
	}

	private Optional<MethodSpec> findById(DefaultNameContext nameContext, TypeElement typeElement) {
		boolean containsNull = StringUtils.containsNull(nameContext.getResponsePackageName());
		if (!containsNull) {
			return Optional.of(MethodSpec.methodBuilder("findById")
			  .addParameter(ParameterSpec.builder(getTableIdTypeName(typeElement), "id")
				.addAnnotation(AnnotationSpec.builder(PathVariable.class)
				  .addMember("value", "$S", "id")
				  .build())
				.build())
			  .addAnnotation(AnnotationSpec.builder(GetMapping.class)
				.addMember("value", "$S", "findById/{id}")
				.build())
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .addJavadoc("findById")
			  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
			    ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName())))
			  .build());
		}
		return Optional.empty();
	}

	private Optional<MethodSpec> findByPage(DefaultNameContext nameContext) {
		boolean containsNull = StringUtils.containsNull(nameContext.getQueryRequestPackageName(),
		  nameContext.getResponsePackageName());
		if (!containsNull) {
			return Optional.of(MethodSpec.methodBuilder("findByPage")
			  .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
				  ClassName.get(nameContext.getQueryRequestPackageName(), nameContext.getQueryRequestClassName())),
				  "request")
				.addAnnotation(RequestBody.class)
				.build())
			  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
				.addMember("value", "$S", "findByPage")
				.build())
			  .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			  .addJavadoc("findByPage request")
			  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
			    ParameterizedTypeName.get(ClassName.get(PageResult.class),
			      ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()))))
			  .build());
		}
		return Optional.empty();
	}
}

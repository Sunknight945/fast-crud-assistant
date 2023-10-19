package com.uiys.gen.controller;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.request.PageRequestWrapper;
import com.uiys.jpa.result.GeneralPageResult;
import com.uiys.jpa.result.GeneralResult;
import com.uiys.jpa.valid.BusinessException;
import com.uiys.spi.CodeGenProcessor;
import com.uiys.util.StringUtils;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author uiys
 */
@AutoService(value = CodeGenProcessor.class)
public class GenControllerProcessor extends AbstractCodeGenProcessor {

	public static final String Controller = "Controller";


	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment roundEnvironment) {
		DefaultNameContext nameContext = getNameContext(typeElement);
		TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(nameContext.getControllerClassName())
		  .addAnnotation(RestController.class)
		  .addAnnotation(Slf4j.class)
		  .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
			.addMember("value", "$S", StringUtils.camel(typeElement.getSimpleName()
			  .toString()) + "/v1")
			.build())
		  .addAnnotation(RequiredArgsConstructor.class)
		  .addModifiers(Modifier.PUBLIC);
		String serviceFieldName = StringUtils.camel(typeElement.getSimpleName()
		  .toString()) + "Service";
		if (StringUtils.containsNull(nameContext.getServicePackageName())) {
			return;
		}
		FieldSpec serviceField = FieldSpec.builder(ClassName.get(nameContext.getServicePackageName(),
			nameContext.getServiceClassName()), serviceFieldName)
		  .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
		  .build();
		typeSpecBuilder.addField(serviceField);

		Optional<MethodSpec> createMethod = createMethod(serviceFieldName, typeElement, nameContext);
		createMethod.ifPresent(typeSpecBuilder::addMethod);

		Optional<MethodSpec> updateMethod = updateMethod(serviceFieldName, typeElement, nameContext);
		updateMethod.ifPresent(typeSpecBuilder::addMethod);

		Optional<MethodSpec> validMethod = validMethod(serviceFieldName, typeElement, nameContext);
		validMethod.ifPresent(typeSpecBuilder::addMethod);

		Optional<MethodSpec> invalidMethod = inValidMethod(serviceFieldName, typeElement, nameContext);
		invalidMethod.ifPresent(typeSpecBuilder::addMethod);

		Optional<MethodSpec> findById = findById(serviceFieldName, nameContext, typeElement);
		findById.ifPresent(typeSpecBuilder::addMethod);

		Optional<MethodSpec> findByPage = findByPage(serviceFieldName, nameContext, typeElement);
		findByPage.ifPresent(typeSpecBuilder::addMethod);

		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenController.class)
		  .sourcePath(), typeSpecBuilder);
	}


	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenController.class;
	}


	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenController.class)
		  .pkg();
	}


	/**
	 * 创建方法
	 *
	 * @param serviceFieldName
	 * @param typeElement
	 * @param nameContext
	 * @return
	 */
	private Optional<MethodSpec> createMethod(String serviceFieldName, TypeElement typeElement,
	                                          DefaultNameContext nameContext) {
		boolean containsNull = StringUtils.containsNull(nameContext.getCreatePackageName(),
		  nameContext.getCreatorPackageName(), nameContext.getMapperPackageName());
		if (!containsNull) {

			ParameterSpec parameterSpec = ParameterSpec.builder(ClassName.get(nameContext.getCreatePackageName(),
				nameContext.getCreateClassName()), "createRequest")
			  .addAnnotation(RequestBody.class)
			  .build();

			MethodSpec.Builder createXX = MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
			  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
				.addMember("value", "$S", "create" + typeElement.getSimpleName())
				.build())
			  .addModifiers(Modifier.PUBLIC)
			  .addParameter(parameterSpec)
			  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
				ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())));

			CodeBlock codeBlock1 = CodeBlock.of("$T creator = $T.INSTANCE.r2C(createRequest); \n",
			  ClassName.get(nameContext.getCreatorPackageName(), nameContext.getCreatorClassName()),
			  ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()));
			createXX.addCode(codeBlock1);
			CodeBlock codeBlock2 = CodeBlock.of("$T<$T> createResult = $L.$L(creator); \n",
			  ClassName.get(Optional.class), ClassName.get(typeElement), serviceFieldName,
			  "create" + typeElement.getSimpleName());
			createXX.addCode(codeBlock2);
			CodeBlock codeBlock3 = CodeBlock.of("return GeneralResult.setNormalResult(new $T($L.orElseThrow(() -> " +
				"new" + " $T" + "($T.ERROR_CODE, createRequest)))); \n", ClassName.get(nameContext.getVoPackageName(),
				nameContext.getVoClassName()), "createResult", ClassName.get(BusinessException.class),
			  ClassName.get(ErrorCode.class));
			createXX.addCode(codeBlock3);
			return Optional.of(createXX.build());
		}
		return Optional.empty();
	}

	/**
	 * 更新方法
	 *
	 * @param serviceFieldName
	 * @param typeElement
	 * @param nameContext
	 * @return
	 */
	private Optional<MethodSpec> updateMethod(String serviceFieldName, TypeElement typeElement,
	                                          DefaultNameContext nameContext) {
		boolean containsNull = StringUtils.containsNull(nameContext.getUpdatePackageName(),
		  nameContext.getUpdaterPackageName(), nameContext.getMapperPackageName());
		if (!containsNull) {
			String tableName = typeElement.getSimpleName()
			  .toString();
			MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("update" + tableName)
			  .addModifiers(Modifier.PUBLIC)
			  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
				ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())))
			  .addParameter(ParameterSpec.builder(ClassName.get(nameContext.getUpdatePackageName(),
				  nameContext.getUpdateClassName()), "updateRequest")
				.addAnnotation(RequestBody.class)
				.build())
			  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
				.addMember("value", "$S", "update" + typeElement.getSimpleName()
				  .toString())
				.build());


			CodeBlock codeBlock = CodeBlock.of("$T updater = $T.INSTANCE.r2U(updateRequest); \n",
			  ClassName.get(nameContext.getUpdaterPackageName(), nameContext.getUpdaterClassName()),
			  ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()));

			CodeBlock codeBlock1 = CodeBlock.of("$T<$T> updateResult = $L.update$L(updater); \n",
			  ClassName.get(Optional.class), ClassName.get(typeElement), serviceFieldName,
			  typeElement.getSimpleName());

			CodeBlock codeBlock2 =
			  CodeBlock.of("return $T.setNormalResult(new $T(updateResult.orElseThrow(() -> new" + " " + "$T($T" +
				  ".ERROR_CODE, " + "updateRequest)))); \n", ClassName.get(GeneralResult.class),
				ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()),
				ClassName.get(BusinessException.class), ClassName.get(ErrorCode.class));

			methodBuild.addCode(codeBlock);
			methodBuild.addCode(codeBlock1);
			methodBuild.addCode(codeBlock2);

			return Optional.of(methodBuild.build());
		}
		return Optional.empty();
	}

	/**
	 * 启用
	 *
	 * @param serviceFieldName
	 * @param typeElement
	 * @param nameContext
	 * @return
	 */
	private Optional<MethodSpec> validMethod(String serviceFieldName, TypeElement typeElement,
	                                         DefaultNameContext nameContext) {
		MethodSpec.Builder builder = MethodSpec.methodBuilder("valid")
		  .addModifiers(Modifier.PUBLIC)
		  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
			ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())))
		  .addParameter(ParameterSpec.builder(getTableIdTypeName(typeElement), "id")
			.addAnnotation(PathVariable.class)
			.build());

		builder.addAnnotation(AnnotationSpec.builder(PostMapping.class)
		  .addMember("value", "$S", "valid/{id}")
		  .build());

		CodeBlock codeBlock = CodeBlock.of("$T<$T> valid = $L.validG1(id); \n ", ClassName.get(Optional.class),
		  ClassName.get(typeElement), serviceFieldName);
		builder.addCode(codeBlock);


		CodeBlock codeBlock1 = CodeBlock.of("return $T.setNormalResult(new $T(valid.orElseThrow(() -> new $T($T" +
		  ".ERROR_CODE, id))));\n ", ClassName.get(GeneralResult.class), ClassName.get(nameContext.getVoPackageName(),
		  nameContext.getVoClassName()), ClassName.get(BusinessException.class), ClassName.get(ErrorCode.class));
		builder.addCode(codeBlock1);

		return Optional.of(builder.build());


	}

	/**
	 * 修复不返回方法的问题
	 *
	 * @param serviceFieldName
	 * @param typeElement
	 * @param nameContext
	 * @return
	 */
	private Optional<MethodSpec> inValidMethod(String serviceFieldName, TypeElement typeElement,
	                                           DefaultNameContext nameContext) {
		MethodSpec.Builder builder = MethodSpec.methodBuilder("inValid")
		  .addModifiers(Modifier.PUBLIC)
		  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
			ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())))
		  .addParameter(ParameterSpec.builder(getTableIdTypeName(typeElement), "id")
			.addAnnotation(PathVariable.class)
			.build());

		builder.addAnnotation(AnnotationSpec.builder(PostMapping.class)
		  .addMember("value", "$S", "inValid/{id}")
		  .build());

		CodeBlock codeBlock = CodeBlock.of("$T<$T> inValid = $L.inValid$L(id); \n ", ClassName.get(Optional.class),
		  ClassName.get(typeElement), serviceFieldName, typeElement.getSimpleName()
			.toString());
		builder.addCode(codeBlock);


		CodeBlock codeBlock1 = CodeBlock.of("return $T.setNormalResult(new $T(inValid.orElseThrow(() -> new $T($T" +
		  ".ERROR_CODE, id))));\n ", ClassName.get(GeneralResult.class), ClassName.get(nameContext.getVoPackageName(),
		  nameContext.getVoClassName()), ClassName.get(BusinessException.class), ClassName.get(ErrorCode.class));
		builder.addCode(codeBlock1);

		return Optional.of(builder.build());
	}

	private Optional<MethodSpec> findById(String serviceFieldName, DefaultNameContext nameContext,
	                                      TypeElement typeElement) {
		boolean containsNull = StringUtils.containsNull(nameContext.getVoPackageName(),
		  nameContext.getMapperPackageName());
		if (!containsNull) {
			return Optional.of(MethodSpec.methodBuilder("findById")
			  .addParameter(ParameterSpec.builder(getTableIdTypeName(typeElement), "id")
				.addAnnotation(PathVariable.class)
				.build())
			  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
				.addMember("value", "$S", "findById/{id}")
				.build())
			  .addModifiers(Modifier.PUBLIC)
			  .addCode(CodeBlock.of("$T byId = $L.findById(id);\n"
				, ClassName.get(typeElement)
				, serviceFieldName))

			  .addCode(CodeBlock.of("return $T.setNormalResult(new $T(byId));"
				, GeneralResult.class, ClassName.get(nameContext.getVoPackageName()
				  , nameContext.getVoClassName())))
			  .returns(
				ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
				ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()))
			  )
			  .build());
		}
		return Optional.empty();
	}

	/**
	 * 分页
	 *
	 * @param serviceFieldName
	 * @param nameContext
	 * @param typeElement
	 * @return
	 */
	private Optional<MethodSpec> findByPage(String serviceFieldName, DefaultNameContext nameContext, TypeElement typeElement) {
		boolean containsNull = StringUtils.containsNull(nameContext.getQueryRequestPackageName(),
		  nameContext.getQueryPackageName(), nameContext.getMapperPackageName(), nameContext.getVoPackageName()/*,
		  nameContext.getResponsePackageName()*/);
		if (!containsNull) {

			ParameterSpec.Builder pageRequestParame =
			  ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
				  ClassName.get(nameContext.getQueryRequestPackageName(), nameContext.getQueryRequestClassName())), "pageReqeust")
				.addAnnotation(ClassName.get(RequestBody.class));

			MethodSpec.Builder pageQuery = MethodSpec.methodBuilder("findByPage")
			  .addModifiers(Modifier.PUBLIC)
			  .addAnnotation(AnnotationSpec.builder(PostMapping.class)
				.addMember("value", "$S", "findByPage")
				.build())
			  .returns(ParameterizedTypeName.get(ClassName.get(GeneralResult.class),
				ParameterizedTypeName.get(ClassName.get(GeneralPageResult.class), ClassName.get(nameContext.getVoPackageName(),nameContext.getVoClassName()))))
			  .addParameter(pageRequestParame.build());


			CodeBlock codeBlock = CodeBlock.of("$T<$T> wrapper = $T.of(pageReqeust,\n" +
				"\t  $T.INSTANCE.$L(pageReqeust.getQuery())); \n"
			  , ClassName.get(PageRequestWrapper.class)
			  , ClassName.get(nameContext.getQueryPackageName(), nameContext.getQueryClassName())
			  , ClassName.get(PageRequestWrapper.class)
			  , ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName())
			  , "r2Q");
			pageQuery.addCode(codeBlock);

			CodeBlock codeBlock1 = CodeBlock.of("$T<$T> byPage = $L.findByPage(wrapper); \n"
			  , ClassName.get(Page.class)
			  , ClassName.get(typeElement)
			  , serviceFieldName);
			pageQuery.addCode(codeBlock1);

			CodeBlock codeBlock2 = CodeBlock.of("$T<$T> records = byPage.get()\n" +
				"\t  .map($T::new)\n" +
				"\t  .collect($T.toList()); \n"
			  , ClassName.get(List.class)
			  , ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())
			  , ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())
			  , ClassName.get(Collectors.class)
			);
			pageQuery.addCode(codeBlock2);

			CodeBlock codeBlock3 = CodeBlock.of("\t return $T.setNormalResult($T.of(byPage, records));"
			  , ClassName.get(GeneralResult.class)
			  , ClassName.get(GeneralPageResult.class)
			);
			pageQuery.addCode(codeBlock3);

			return Optional.ofNullable(pageQuery.build());
		}
		return Optional.empty();
	}
}

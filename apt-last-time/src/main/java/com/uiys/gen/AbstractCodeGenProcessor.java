package com.uiys.gen;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.api.GenCreateRequest;
import com.uiys.gen.api.GenCreateRequestProcessor;
import com.uiys.gen.api.GenFeign;
import com.uiys.gen.api.GenFeignProcessor;
import com.uiys.gen.api.GenQueryRequest;
import com.uiys.gen.api.GenQueryRequestProcessor;
import com.uiys.gen.api.GenResponse;
import com.uiys.gen.api.GenResponseProcessor;
import com.uiys.gen.api.GenUpdateRequest;
import com.uiys.gen.api.GenUpdateRequestProcessor;
import com.uiys.gen.api.TypeConverter;
import com.uiys.gen.controller.GenController;
import com.uiys.gen.controller.GenControllerProcessor;
import com.uiys.gen.creator.GenCreator;
import com.uiys.gen.creator.GenCreatorProcessor;
import com.uiys.gen.mapper.GenMapper;
import com.uiys.gen.mapper.GenMapperProcessor;
import com.uiys.gen.query.GenQuery;
import com.uiys.gen.query.GenQueryProcessor;
import com.uiys.gen.repository.GenRepository;
import com.uiys.gen.repository.GenRepositoryProcessor;
import com.uiys.gen.service.GenService;
import com.uiys.gen.service.GenServiceImpl;
import com.uiys.gen.service.GenServiceImplProcessor;
import com.uiys.gen.service.GenServiceProcessor;
import com.uiys.gen.updater.GenUpdater;
import com.uiys.gen.updater.GenUpdaterProcessor;
import com.uiys.gen.vo.GenVo;
import com.uiys.gen.vo.GenVoProcessor;
import com.uiys.holder.ProcessingHolder;
import com.uiys.otherType.annoType.FieldDesc;
import com.uiys.spi.CodeGenProcessor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.persistence.Id;
import javax.tools.Diagnostic;
import lombok.Data;


public abstract class AbstractCodeGenProcessor implements CodeGenProcessor {

	protected static final String Base = "Base";

	public abstract void generateCode(TypeElement typeElement, RoundEnvironment environment);

	@Override
	public void generate(TypeElement typeElement, RoundEnvironment environment) {
		generateCode(typeElement, environment);
	}


	public Set<VariableElement> findFields(TypeElement typeElement, Predicate<VariableElement> predicate) {
		Set<VariableElement> set = new HashSet<>();
		List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
		List<VariableElement> variableElements = ElementFilter.fieldsIn(enclosedElements);
		for (VariableElement variableElement : variableElements) {
			if (predicate.test(variableElement)) {
				set.add(variableElement);
			}
		}
		return set;
	}

	protected void addFieldSetGetMethod(Set<VariableElement> variableElements, TypeSpec.Builder typeBuilder) {
		for (VariableElement ve : variableElements) {
			TypeName fieldType = TypeName.get(ve.asType());
			String fieldName = ve.getSimpleName()
			  .toString();
			FieldSpec.Builder filedBuild = FieldSpec.builder(fieldType, fieldName, Modifier.PRIVATE)
			  .addAnnotation(AnnotationSpec.builder(Schema.class)
				.addMember("title", "$S", getFieldDesc(ve))
				.build());

			String methodDefaultName = getMethodDefaultName(ve);

			MethodSpec.Builder getMethod = MethodSpec.methodBuilder("get" + methodDefaultName)
			  .returns(fieldType)
			  .addModifiers(Modifier.PUBLIC)
			  .addStatement("return " + fieldName);

			MethodSpec.Builder setMethod = MethodSpec.methodBuilder("set" + methodDefaultName)
			  .returns(void.class)
			  .addModifiers(Modifier.PUBLIC)
			  .addParameter(fieldType, fieldName)
			  .addStatement("this.$L = $L", fieldName, fieldName);

			typeBuilder.addField(filedBuild.build());
			typeBuilder.addMethod(getMethod.build());
			typeBuilder.addMethod(setMethod.build());
		}
	}

	protected void addFieldSetGetMethodWithConvert(Set<VariableElement> variableElements, TypeSpec.Builder builder) {
		for (VariableElement ve : variableElements) {
			String fieldName = ve.getSimpleName()
			  .toString();
			TypeName typeName;
			if (Objects.nonNull(ve.getAnnotation(TypeConverter.class))) {
				//这里处理下泛型的情况，比如List<String> 这种，TypeConverter FullName 用逗号分隔"java.lang.List
				String fullName = ve.getAnnotation(TypeConverter.class)
				  .toTypeFullName();
				Iterable<String> classes = Splitter.on(",")
				  .split(fullName);
				int size = Iterables.size(classes);
				if (size > 1) {
					typeName = ParameterizedTypeName.get(ClassName.bestGuess(Iterables.get(classes, 0)),
					  ClassName.bestGuess(Iterables.get(classes, 1)));
				} else {
					typeName = ClassName.bestGuess(ve.getAnnotation(TypeConverter.class)
					  .toTypeFullName());
				}
			} else {
				typeName = ClassName.get(ve.asType());
			}
			FieldSpec filed = FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE)
			  .addAnnotation(AnnotationSpec.builder(Schema.class)
				.addMember("title", "$S", getFieldDesc(ve))
				.build())
			  .build();
			builder.addField(filed);
			String getMethod = "get" + getMethodDefaultName(ve);
			MethodSpec getVe = MethodSpec.methodBuilder(getMethod)
			  .addModifiers(Modifier.PUBLIC)
			  .returns(typeName)
			  .addStatement("return this.$L", fieldName)
			  .build();
			builder.addMethod(getVe);
			String setMethod = "set" + getMethodDefaultName(ve);
			MethodSpec setVe = MethodSpec.methodBuilder(setMethod)
			  .addModifiers(Modifier.PUBLIC)
			  .returns(void.class)
			  .addParameter(typeName, fieldName)
			  .addStatement("this.$L = $L", fieldName, fieldName)
			  .build();
			builder.addMethod(setVe);
		}
	}

	private String getFieldDesc(VariableElement ve) {
		return Optional.ofNullable(ve.getAnnotation(FieldDesc.class))
		  .map(FieldDesc::name)
		  .orElse(ve.getSimpleName()
			.toString());
	}


	protected String getMethodDefaultName(VariableElement ve) {
		return ve.getSimpleName()
		  .toString()
		  .substring(0, 1)
		  .toUpperCase() + ve.getSimpleName()
		  .toString()
		  .substring(1);
	}

	public TypeSpec.Builder getSourceTypeWithConstruct(TypeElement e, String sourceName, String packageName,
	                                                   String superClassName) {
		MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder()
		  .addParameter(TypeName.get(e.asType()), "source")
		  .addModifiers(Modifier.PUBLIC);
		constructorSpecBuilder.addStatement("super(source)");
		TypeSpec.Builder sourceBuilder = TypeSpec.classBuilder(sourceName)
		  .superclass(ClassName.get(packageName, superClassName))
		  .addModifiers(Modifier.PUBLIC)
		  .addMethod(MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.build())
		  .addMethod(constructorSpecBuilder.build())
		  .addAnnotation(Schema.class)
		  .addAnnotation(Data.class);
		return sourceBuilder;
	}

	public TypeSpec.Builder getSourceType(String sourceName, String packageName, String superClassName) {
		TypeSpec.Builder sourceBuilder = TypeSpec.classBuilder(sourceName)
		  .superclass(ClassName.get(packageName, superClassName))
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Schema.class)
		  .addAnnotation(Data.class);
		return sourceBuilder;
	}

	protected void genJavaFile(String packageName, TypeSpec.Builder typeBuilder) {
		JavaFile javaFile = JavaFile.builder(packageName, typeBuilder.build())
		  .addFileComment("AutoGenerate")
		  .build();
		try {
			javaFile.writeTo(ProcessingHolder.getProcessingEnv()
			  .getFiler());
		} catch (IOException e) {
			ProcessingHolder.getProcessingEnv()
			  .getMessager()
			  .printMessage(Diagnostic.Kind.ERROR, "要死了ca: " + e.getMessage());
		}
	}


	public void genJavaSourceFile(String packageName, String pathStr, TypeSpec.Builder typeSpecBuilder) {
		TypeSpec typeSpec = typeSpecBuilder.build();
		JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
		  .addFileComment("AutoGenerate")
		  .build();
//    System.out.println(javaFile);
		String packagePath = packageName.replace(".", File.separator) + File.separator + typeSpec.name + ".java";
		try {
			Path path = Paths.get(pathStr);
			File file = new File(path.toFile()
			  .getAbsolutePath());
			if (!file.exists()) {
				return;
			}
			String sourceFileName = path.toFile()
			  .getAbsolutePath() + File.separator + packagePath;
			File sourceFile = new File(sourceFileName);
			if (!sourceFile.exists()) {
				javaFile.writeTo(file);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	protected String getCurrentHandClassName(TypeElement typeElement) {
		return typeElement.getSimpleName()
		  .toString();
	}


	protected TypeName getTableIdTypeName(TypeElement typeElement) {
		TypeName idTypeName = ClassName.get(String.class);
		TypeMirror superClassTypeMirror = typeElement.getSuperclass();
		TypeElement superClassTypeElement = (TypeElement) ((DeclaredType) superClassTypeMirror).asElement();
		Set<VariableElement> fields = findFields(superClassTypeElement,
		  ve -> Objects.nonNull(ve.getAnnotation(Id.class)));
		if (CollUtil.isNotEmpty(fields) && fields.size() == 1) {
			List<VariableElement> variableElements = new ArrayList<>(fields);
			VariableElement variableElement = variableElements.get(0);
			idTypeName = TypeName.get(variableElement.asType());
		}
		return idTypeName;
	}

	protected String getEntity(TypeElement typeElement){
		return ClassName.get(typeElement).simpleName();
	}



	protected void addIdSetterAndGetter(TypeSpec.Builder builder, TypeName idType) {
		FieldSpec.Builder id = FieldSpec.builder(idType, "id", Modifier.PRIVATE)
		  .addAnnotation(AnnotationSpec.builder(Schema.class)
			.addMember("title", "$S", "主键")
			.build());
		MethodSpec.Builder getMethod = MethodSpec.methodBuilder("getId")
		  .returns(idType)
		  .addModifiers(Modifier.PUBLIC)
		  .addStatement("return $L", "id");
		MethodSpec.Builder setMethod = MethodSpec.methodBuilder("setId")
		  .returns(void.class)
		  .addModifiers(Modifier.PUBLIC)
		  .addParameter(idType, "id")
		  .addStatement("this.$L = $L", "id", "id");
		builder.addField(id.build());
		builder.addMethod(getMethod.build());
		builder.addMethod(setMethod.build());
	}

	/**
	 * 获取名称默认上下文
	 *
	 * @param typeElement
	 * @return
	 */
	public DefaultNameContext getNameContext(TypeElement typeElement) {
		DefaultNameContext context = new DefaultNameContext();
		String serviceName = GenServiceProcessor.I + typeElement.getSimpleName() + GenServiceProcessor.Service;
		String implName = typeElement.getSimpleName() + GenServiceImplProcessor.ServiceImpl;
		String repositoryName = typeElement.getSimpleName() + GenRepositoryProcessor.Repository;
		String mapperName = typeElement.getSimpleName() + GenMapperProcessor.tMapper;
		String voName = typeElement.getSimpleName() + GenVoProcessor.Vo;
		String queryName = typeElement.getSimpleName() + GenQueryProcessor.Query;
		String creatorName = typeElement.getSimpleName() + GenCreatorProcessor.next;
		String updaterName = typeElement.getSimpleName() + GenUpdaterProcessor.Update;
		String createRequestName = typeElement.getSimpleName() + GenCreateRequestProcessor.CreateRequest;
		String updateRequestName = typeElement.getSimpleName() + GenUpdateRequestProcessor.UpdateRequest;
		String queryRequestName = typeElement.getSimpleName() + GenQueryRequestProcessor.QueryRequest;
		String responseName = typeElement.getSimpleName() + GenResponseProcessor.Response;
		String feignName = typeElement.getSimpleName() + GenFeignProcessor.FeignService;
		String controllerName = typeElement.getSimpleName() + GenControllerProcessor.Controller;
		context.setServiceClassName(serviceName);
		context.setRepositoryClassName(repositoryName);
		context.setMapperClassName(mapperName);
		context.setVoClassName(voName);
		context.setQueryClassName(queryName);
		context.setCreatorClassName(creatorName);
		context.setUpdaterClassName(updaterName);
		context.setImplClassName(implName);
		context.setCreateClassName(createRequestName);
		context.setUpdateClassName(updateRequestName);
		context.setQueryRequestClassName(queryRequestName);
		context.setResponseClassName(responseName);
		context.setFeignClassName(feignName);
		context.setControllerClassName(controllerName);
		Optional.ofNullable(typeElement.getAnnotation(GenCreator.class))
		  .ifPresent(anno -> {
			  context.setCreatorPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenUpdater.class))
		  .ifPresent(anno -> {
			  context.setUpdaterPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenQuery.class))
		  .ifPresent(anno -> {
			  context.setQueryPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenVo.class))
		  .ifPresent(anno -> {
			  context.setVoPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenRepository.class))
		  .ifPresent(anno -> {
			  context.setRepositoryPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenMapper.class))
		  .ifPresent(anno -> {
			  context.setMapperPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenService.class))
		  .ifPresent(anno -> {
			  context.setServicePackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenServiceImpl.class))
		  .ifPresent(anno -> {
			  context.setImplPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenCreateRequest.class))
		  .ifPresent(anno -> {
			  context.setCreatePackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenUpdateRequest.class))
		  .ifPresent(anno -> {
			  context.setUpdatePackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenQueryRequest.class))
		  .ifPresent(anno -> {
			  context.setQueryRequestPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenResponse.class))
		  .ifPresent(anno -> {
			  context.setResponsePackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenFeign.class))
		  .ifPresent(anno -> {
			  context.setFeignPackageName(anno.pkg());
		  });
		Optional.ofNullable(typeElement.getAnnotation(GenController.class))
		  .ifPresent(anno -> {
			  context.setControllerPackageName(anno.pkg());
		  });
		return context;
	}


}



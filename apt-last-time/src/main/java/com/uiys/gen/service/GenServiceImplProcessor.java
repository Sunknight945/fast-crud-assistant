package com.uiys.gen.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.google.auto.service.AutoService;
import com.querydsl.core.BooleanBuilder;
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
import com.uiys.jpa.request.PageUtil;
import com.uiys.jpa.support.EntityOperations;
import com.uiys.jpa.util.AssembleUtils;
import com.uiys.jpa.valid.BusinessException;
import com.uiys.spi.CodeGenProcessor;
import com.uiys.util.StringUtils;
import com.uiys.util.TorE;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@AutoService(CodeGenProcessor.class)
public class GenServiceImplProcessor extends AbstractCodeGenProcessor {
	public static final String ServiceImpl = "ServiceImpl";
	private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		DefaultNameContext context = getNameContext(typeElement);

		boolean contained = StringUtils.containsNull(context.getServiceClassName());
		if (contained) {
			return;
		}


		String xXServiceImple = typeElement.getSimpleName()
		  .toString() + ServiceImpl;


		TypeSpec.Builder builder = TypeSpec.classBuilder(xXServiceImple)
		  .addAnnotation(Slf4j.class)
		  .addAnnotation(Service.class)
		  .addAnnotation(Transactional.class)
		  .addAnnotation(RequiredArgsConstructor.class)
		  .addModifiers(Modifier.PUBLIC)
		  .addSuperinterface(ClassName.get(context.getServicePackageName(), context.getServiceClassName()));

		addRepository(context, builder, typeElement);

		addUpdateXxMethod(context, builder, typeElement);
		addCreateXxMethod(context, builder, typeElement);
		addValidXxMethod(context, builder, typeElement);
		addInValidXxMethod(context, builder, typeElement);
		addFindByIdMethod(context, builder, typeElement);
		addFindByPageXxMethod(context, builder, typeElement);
		addFindAllMethod(context, builder, typeElement);
		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenServiceImpl.class)
		  .sourcePath(), builder);
	}

	private void addFindAllMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		List<ParameterSpec> parameterSpecs = new ArrayList<>();
		ParameterSpec wrapper =
		  ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
			  ClassName.get(context.getQueryPackageName(), context.getQueryClassName())), "wrapper")
			.build();
		parameterSpecs.add(wrapper);
		ParameterSpec booleanBuilder = ParameterSpec.builder(ClassName.get(BooleanBuilder.class), "booleanBuilder")
		  .build();
		parameterSpecs.add(booleanBuilder);
		ParameterSpec all = ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class),
			ClassName.get(context.getVoPackageName(), context.getVoClassName())), "all")
		  .build();
		parameterSpecs.add(all);

		ClassName voClass = ClassName.get(context.getVoPackageName(), context.getVoClassName());
		MethodSpec.Builder findAllMethod = MethodSpec.methodBuilder("findAll")
		  .addModifiers(Modifier.PUBLIC, Modifier.PUBLIC)
		  .addParameters(parameterSpecs)
		  .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(context.getVoPackageName(),
			context.getVoClassName())));
		findAllMethod.addCode(CodeBlock.of("$T<$T> page = $L.findAll(booleanBuilder, $T.get(wrapper));\n" +
			"if ($T.isEmpty(page.getContent())) {\n" +
			"\treturn all;\n" +
			"}\n" +
			"all.addAll($T.content(page, $T::new));\n" +
			"wrapper.setPageNum(wrapper.getPageNum() + 1);\n" +
			"return findAll(wrapper, booleanBuilder, all);",
		  ClassName.get(Page.class),
		  typeElement,
		  repositoryName(),
		  ClassName.get(PageUtil.class),
		  ClassName.get(CollUtil.class),
		  ClassName.get(AssembleUtils.class),
		  voClass));

		builder.addMethod(findAllMethod.build());
	}

	private void addFindByPageXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		MethodSpec.Builder findByPage = MethodSpec.methodBuilder("findByPage")
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Override.class)
		  .addParameter(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
			ClassName.get(context.getQueryPackageName(), context.getQueryClassName())), "wrapper")
		  .returns(ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(context.getVoPackageName(),
			context.getVoClassName())));
		String repositoryName = repositoryName();
		CodeBlock codeBlock1 = CodeBlock.of("$T booleanBuilder = new $T(); \n", ClassName.get(BooleanBuilder.class),
		  ClassName.get(BooleanBuilder.class));
		findByPage.addCode(codeBlock1);

		CodeBlock codeBlock2 = CodeBlock.of("$T<$T> page = $L.findAll(booleanBuilder, $T.get(wrapper)); \n",
		  ClassName.get(Page.class), ClassName.get(typeElement), repositoryName, ClassName.get(PageUtil.class));
		findByPage.addCode(codeBlock2);

		CodeBlock codeBlock3 = CodeBlock.of("return $T.change(page, $T::new); \n", ClassName.get(PageUtil.class),
		  ClassName.get(context.getVoPackageName(), context.getVoClassName()));
		findByPage.addCode(codeBlock3);


		builder.addMethod(findByPage.build());
	}


	private void addFindByIdMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {

		ClassName vo = ClassName.get(context.getVoPackageName(), context.getVoClassName());

		String voL = vo.simpleName()
		  .substring(0, 1)
		  .toLowerCase() + vo.simpleName()
		  .substring(1);


		MethodSpec.Builder findById = MethodSpec.methodBuilder("findById")
		  .addModifiers(Modifier.PUBLIC)
		  .addParameter(getTableIdTypeName(typeElement), "id")
		  .addParameter(ClassName.get(Boolean.class), "throwExt")
		  .addAnnotation(Override.class)
		  .returns(vo);
		String repositoryName = repositoryName();
		CodeBlock codeBlock = CodeBlock.of(
			"$T $L = $L.findById(id).map($T::new).orElse(null);\n" +
			"$T.isTrue(!(throwExt && $L==null),()-> new $T($T.NOTFOUND, $T.name($T.class, id)));\n" +
			"return $L;",
		  vo,
		  voL,
		  repositoryName,
		  vo,
		  ClassName.get(Assert.class),
		  voL,
		  ClassName.get(BusinessException.class),
		  ClassName.get(ErrorCode.class),
		  ClassName.get(TorE.class),
		  ClassName.get(typeElement),
		  voL);


		findById.addCode(codeBlock);


		builder.addMethod(findById.build());
	}

	private void addInValidXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		MethodSpec.Builder valid = MethodSpec.methodBuilder("inValid" + typeElement.getSimpleName()
			.toString())
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Override.class)
		  .addParameter(getTableIdTypeName(typeElement), "id")
		  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)));
		String repositoryName = repositoryName();
		CodeBlock codeBlock =
		  CodeBlock.of("return EntityOperations.doUpdate($L)\n" + "\t\t  .loadById(id)\n" + "\t" + "\t" + " " + " " +
			".update($T::invalid)\n" + "\t\t  .execute();", repositoryName, ClassName.get(typeElement));

		valid.addCode(codeBlock);


		builder.addMethod(valid.build());
	}

	private void addValidXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		MethodSpec.Builder valid = MethodSpec.methodBuilder("valid" + typeElement.getSimpleName()
			.toString())
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Override.class)
		  .addParameter(getTableIdTypeName(typeElement), "id")
		  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)));
		String repositoryName = repositoryName();
		CodeBlock codeBlock =
		  CodeBlock.of("return EntityOperations.doUpdate($L)\n" + "\t\t  .loadById(id)\n" + "\t" + "\t" + " " + " " +
			".update($T::valid)\n" + "\t\t  .execute();", repositoryName, ClassName.get(typeElement));

		valid.addCode(codeBlock);


		builder.addMethod(valid.build());


	}

	private void addCreateXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		boolean contained = StringUtils.containsNull(context.getCreatePackageName(), context.getCreatorPackageName());
		if (contained) {
			return;
		}
		MethodSpec.Builder create = MethodSpec.methodBuilder("create" + typeElement.getSimpleName()
			.toString())
		  .addAnnotation(Override.class)
		  .addModifiers(Modifier.PUBLIC)
		  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)))
		  .addParameter(ClassName.get(context.getCreatorPackageName(), context.getCreatorClassName()), "creator");
		String repositoryName = repositoryName();
		CodeBlock codeBlock = CodeBlock.of("return $T.doCreate($L)\n" + "\t\t  .create(() -> " + "$T" + ".INSTANCE" +
			".u2Entity" + "(creator))\n" + "\t\t  .update($T::init)\n" + "\t\t  .execute();",
		  ClassName.get(EntityOperations.class), repositoryName, ClassName.get(context.getMapperPackageName(),
			context.getMapperClassName()), ClassName.get(typeElement));

		create.addCode(codeBlock);
		builder.addMethod(create.build());

	}

	private void addUpdateXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		boolean contained = StringUtils.containsNull(context.getUpdaterPackageName(), context.getUpdatePackageName());
		if (contained) {
			return;
		}
		MethodSpec.Builder updater = MethodSpec.methodBuilder("update" + typeElement.getSimpleName()
			.toString())
		  .addAnnotation(Override.class)
		  .addModifiers(Modifier.PUBLIC)
		  .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)))
		  .addParameter(ClassName.get(context.getUpdaterPackageName(), context.getUpdaterClassName()), "updater");
		String repositoryName = repositoryName();
		String loadClass = "load" + typeElement.getSimpleName()
		  .toString();
		CodeBlock codeBlock =
		  CodeBlock.of("return $T.doUpdate($L)\n" + "\t\t  .loadById(updater.getId" + "()" + ")\n" + "\t" + "\t  " +
			  ".update(updater::$L)\n" + "\t\t  .execute();", ClassName.get(EntityOperations.class), repositoryName,
			loadClass);
		updater.addCode(codeBlock);
		builder.addMethod(updater.build());
	}

	private void addRepository(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		boolean contained = StringUtils.containsNull(context.getRepositoryPackageName());
		if (!contained) {
			ClassName className = ClassName.get(context.getRepositoryPackageName(), context.getRepositoryClassName());
			setRepositoryName(className);
			FieldSpec.Builder repository = FieldSpec.builder(className, repositoryName(), Modifier.PRIVATE,
			  Modifier.FINAL);
			builder.addField(repository.build())
			  .build();
		}
	}

	private void setRepositoryName(ClassName className) {
		String s = className.simpleName()
		  .substring(0, 1)
		  .toLowerCase() + className.simpleName()
		  .substring(1);
		threadLocal.set(s);
	}

	private String repositoryName() {

		return threadLocal.get();
	}


	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenServiceImpl.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenServiceImpl.class)
		  .pkg();
	}
}



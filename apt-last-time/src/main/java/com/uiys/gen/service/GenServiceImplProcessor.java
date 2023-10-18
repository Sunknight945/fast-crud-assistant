package com.uiys.gen.service;

import com.google.auto.service.AutoService;
import com.querydsl.core.BooleanBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.request.PageRequestWrapper;
import com.uiys.jpa.support.EntityOperations;
import com.uiys.jpa.valid.BusinessException;
import com.uiys.spi.CodeGenProcessor;
import com.uiys.util.StringUtils;
import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenServiceImpl.class)
		  .sourcePath(), builder);
	}

	private void addFindByPageXxMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {
		MethodSpec.Builder findByPage = MethodSpec.methodBuilder("findByPage")
		  .addModifiers(Modifier.PUBLIC)
		  .addAnnotation(Override.class)
		  .addParameter(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
			ClassName.get(context.getQueryPackageName(), context.getQueryClassName())), "wrapper")
		  .returns(ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(typeElement)));
		String repositoryName = repositoryName();
		CodeBlock codeBlock1 = CodeBlock.of("$T booleanBuilder = new $T();", ClassName.get(BooleanBuilder.class),
		  ClassName.get(BooleanBuilder.class));

		CodeBlock codeBlock2 = CodeBlock.of("Page<$T> createdAt = $L.findAll(booleanBuilder, $T.of(wrapper" +
			".getPageNum() - " + "1," + "\n" + "\t\t  wrapper.getPageSize(), $T.by(Sort.Direction.DESC, \"createdAt\"," +
			" " +
			"\"id\")));\n" + "\t" + "\treturn new $T<>(createdAt.getContent(), createdAt.getPageable(), createdAt" +
			".getTotalElements()" + ");", ClassName.get(typeElement), repositoryName, ClassName.get(PageRequest.class),
		  ClassName.get(Sort.class), ClassName.get(PageImpl.class));

		findByPage.addCode(codeBlock1);
		findByPage.addCode(codeBlock2);
		builder.addMethod(findByPage.build());
	}

	private void addFindByIdMethod(DefaultNameContext context, TypeSpec.Builder builder, TypeElement typeElement) {

		MethodSpec.Builder findById = MethodSpec.methodBuilder("findById")
		  .addModifiers(Modifier.PUBLIC)
		  .addParameter(getTableIdTypeName(typeElement), "id")
		  .addAnnotation(Override.class)
		  .returns(ClassName.get(typeElement));

		String repositoryName = repositoryName();
		CodeBlock codeBlock = CodeBlock.of("return $L.findById(id)\n" + "\t\t  .orElseThrow(() -> new " + "$T($T" +
			".NOTFOUND, \"id is: \" + id));", repositoryName, ClassName.get(BusinessException.class),
		  ClassName.get(ErrorCode.class));

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
			".update($T::inValid)\n" + "\t\t  .execute();", repositoryName, ClassName.get(typeElement));

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
			".u2E" +
			"(creator))\n" + "\t\t  .update($T::init)\n" + "\t\t  .execute();", ClassName.get(EntityOperations.class),
		  repositoryName, ClassName.get(context.getMapperPackageName(), context.getMapperClassName()),
		  ClassName.get(typeElement));

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
			  ".update(updater::$L)\n" + "\t\t  .execute();", ClassName.get(EntityOperations.class),
			repositoryName, loadClass);
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



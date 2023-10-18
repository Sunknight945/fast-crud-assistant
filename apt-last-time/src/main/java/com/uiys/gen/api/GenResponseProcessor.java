package com.uiys.gen.api;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeSpec;
import com.uiys.gen.AbstractCodeGenProcessor;
import com.uiys.gen.DefaultNameContext;
import com.uiys.gen.vo.IgnoreVo;
import com.uiys.jpa.response.AbstractJpaResponse;
import com.uiys.spi.CodeGenProcessor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

@AutoService(value = CodeGenProcessor.class)
public class GenResponseProcessor extends AbstractCodeGenProcessor {

	public static String Response = "Response";

	@Override
	public void generateCode(TypeElement typeElement, RoundEnvironment environment) {
		DefaultNameContext nameContext = getNameContext(typeElement);
		Set<VariableElement> fields = findFields(typeElement, p -> Objects.isNull(p.getAnnotation(IgnoreVo.class)));
		TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(nameContext.getResponseClassName())
		  .addModifiers(Modifier.PUBLIC)
		  .superclass(AbstractJpaResponse.class)
		  .addAnnotation(Schema.class);
		addFieldSetGetMethodWithConvert(fields, typeSpecBuilder);
		genJavaSourceFile(getPackageName(typeElement), typeElement.getAnnotation(GenResponse.class)
		  .sourcePath(), typeSpecBuilder);
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return GenResponse.class;
	}

	@Override
	public String getPackageName(TypeElement typeElement) {
		return typeElement.getAnnotation(GenResponse.class)
		  .pkg();
	}


}

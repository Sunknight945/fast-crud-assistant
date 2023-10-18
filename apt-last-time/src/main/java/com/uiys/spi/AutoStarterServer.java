package com.uiys.spi;

import com.google.auto.service.AutoService;
import com.uiys.holder.ProcessingHolder;
import com.uiys.register.CodeGenAndAnnoRegister;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

/**
 * @author uiys
 * @date
 */
@AutoService(Processor.class)
public class AutoStarterServer extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement an : annotations) {
			Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(an);
			Set<TypeElement> types = ElementFilter.typesIn(elementsAnnotatedWith);
			for (TypeElement typeElement : types) {
				CodeGenProcessor codeGenProcessor = CodeGenAndAnnoRegister.getCodeGenProcessor(an.getQualifiedName()
				  .toString());
				try {
					codeGenProcessor.generate(typeElement, roundEnv);
				} catch (Exception e) {
					ProcessingHolder.getProcessingEnv()
					  .getMessager()
					  .printMessage(Diagnostic.Kind.ERROR, "生成代码异常, 原因: " + e.getMessage());
				}
			}
		}
		return false;
	}


	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return CodeGenAndAnnoRegister.getSupportAnnot();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		CodeGenAndAnnoRegister.registerProcessAndAnno();
		ProcessingHolder.setProcessingEnv(processingEnv);
	}
}



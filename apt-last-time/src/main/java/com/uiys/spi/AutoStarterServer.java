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

	/**
	 *
	 * @param annotations the annotation types requested to be processed (需要处理的那些注解在init的时候就放进去了)
	 * @param roundEnv  environment for information about the current and prior round (注解处理器的环境支持)
	 * @return
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// 将需要处理的注解循环
		for (TypeElement an : annotations) {
			// 得到需要处理的注解的所有元素(因为这个元素的是所有的 表示程序元素，如包、类或方法。每个元素表示静态的语言级构造 (而不是例如虚拟机的运行时构造))
			Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(an);
			// 过滤里面的类元素 (找到需要处理的类)
			Set<TypeElement> types = ElementFilter.typesIn(elementsAnnotatedWith);
			// 循环处理某一个类
			for (TypeElement typeElement : types) {
				// 根据注解的名称获取到 处理这个注解的处理器.
				CodeGenProcessor codeGenProcessor = CodeGenAndAnnoRegister.getCodeGenProcessor(an.getQualifiedName()
				  .toString());
				try {
					// 处理器利用上下文的环境 处理这个类元素
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



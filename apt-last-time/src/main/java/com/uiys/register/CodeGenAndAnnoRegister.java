package com.uiys.register;

import com.uiys.spi.CodeGenProcessor;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;


public class CodeGenAndAnnoRegister {

	private static Map<String, ? extends CodeGenProcessor> map = new HashMap<>();


	public static void registerProcessAndAnno() {
		Map<String, CodeGenProcessor> currentMap = new HashMap<>();
		ServiceLoader<CodeGenProcessor> load = ServiceLoader.load(CodeGenProcessor.class,
		  CodeGenProcessor.class.getClassLoader());
		for (CodeGenProcessor next : load) {
			currentMap.put(next.getAnnotation()
			  .getName(), next);
		}
		map = currentMap;
	}
	public static Set<String> getSupportAnnot() {
		return map.keySet();
	}

	public static CodeGenProcessor getCodeGenProcessor(String name) {
		return map.get(name);
	}
}



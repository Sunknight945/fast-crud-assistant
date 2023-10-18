package com.uiys.holder;

import javax.annotation.processing.ProcessingEnvironment;


public class ProcessingHolder {

	private static final ThreadLocal<ProcessingEnvironment> LOCAL = new ThreadLocal<>();


	public static void setProcessingEnv(ProcessingEnvironment processingEnv) {
		LOCAL.set(processingEnv);
	}

	public static ProcessingEnvironment getProcessingEnv() {
		return LOCAL.get();
	}


}



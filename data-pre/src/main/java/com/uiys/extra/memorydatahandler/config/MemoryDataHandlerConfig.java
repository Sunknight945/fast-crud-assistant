package com.uiys.extra.memorydatahandler.config;

import com.uiys.extra.memorydatahandler.MemoryDataFieldBuilder;
import com.uiys.extra.memorydatahandler.support.MemoryDataExecutorDefault;
import com.uiys.extra.memorydatahandler.support.MemoryDataFieldBuilderDefault;
import com.uiys.extra.memorydatahandler.support.MemoryDataTypeBuilderDefault;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;

/**
 * @author uiys
 * 内存数据处理器的配置.
 * 诸如 BeanResolver 的配置 在使用 joinDataLoader 前需要
 * 需要对 Spel 的上下文 中放入 BeanResolver 以便能从 Spring中取出 某个
 * xxxRepository 的实例让 它加载出来 对应的 joinData.否则光由 Sepl 表达式也找不到对应的BeanFactory中的实例.
 * 等 ...
 */
@Configuration
public class MemoryDataHandlerConfig {


	@Bean
	@ConditionalOnMissingBean
	public MemoryDataFieldBuilderDefault memoryDataFieldBuilderDefault(ApplicationContext applicationContext) {
		BeanFactoryResolver beanFactoryResolver = new BeanFactoryResolver(applicationContext);
		return new MemoryDataFieldBuilderDefault(beanFactoryResolver);
	}

	@Bean
	@ConditionalOnMissingBean
	public MemoryDataTypeBuilderDefault memoryDataTypeBuilderDefault(MemoryDataFieldBuilder memoryDataFieldBuilder,
	                                                                 ExecutorService executorService) {
		return new MemoryDataTypeBuilderDefault(memoryDataFieldBuilder, executorService);
	}


	@Bean
	@ConditionalOnMissingBean
	public MemoryDataExecutorDefault memoryDataExecutorDefault(MemoryDataTypeBuilderDefault memoryDataTypeBuilderDefault) {
		return new MemoryDataExecutorDefault(memoryDataTypeBuilderDefault);
	}


	@Bean
	public ExecutorService executorService() {
		BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder().namingPattern("memoryDataHandler-Thread" +
			"-%d")
		  .daemon(true)
		  .build();
		return new ThreadPoolExecutor(0, Runtime.getRuntime()
		  .availableProcessors() * 2, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), basicThreadFactory,
		  new ThreadPoolExecutor.CallerRunsPolicy());

	}
}



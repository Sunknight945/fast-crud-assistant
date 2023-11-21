package com.uiys.extra.joininmemory;

import com.uiys.extra.joininmemory.support.DefaultJoinItemsExecutorFactory;
import com.uiys.extra.joininmemory.support.DefaultJoinService;
import com.uiys.extra.joininmemory.support.JoinInMemoryBasedJoinItemExecutorFactory;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
/**
 * @author uiys
 * @date 2023/11/22
 */
@Configuration
@Slf4j
public class JoinInMemoryAutoConfiguration {
	@Bean
	@ConditionalOnMissingBean
	public JoinItemsExecutorFactory joinItemsExecutorFactory(Collection<? extends JoinItemExecutorFactory> joinItemExecutorFactories,
	                                                         Map<String, ExecutorService> executorServiceMap) {
		return new DefaultJoinItemsExecutorFactory(joinItemExecutorFactories, executorServiceMap);
	}

	@Bean
	@ConditionalOnMissingBean
	public JoinService joinService(JoinItemsExecutorFactory joinItemsExecutorFactory) {
		return new DefaultJoinService(joinItemsExecutorFactory);
	}

	@Bean
	public JoinInMemoryBasedJoinItemExecutorFactory joinInMemoryBasedJoinItemExecutorFactory(ApplicationContext applicationContext) {
		return new JoinInMemoryBasedJoinItemExecutorFactory(new BeanFactoryResolver(applicationContext));
	}

	@Bean
	public ExecutorService defaultExecutor() {
		BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder()
		  .namingPattern("JoinInMemory-Thread-%d")
		  .daemon(true)
		  .build();
		int maxSize = Runtime.getRuntime()
		  .availableProcessors() * 3;
		return new ThreadPoolExecutor(0, maxSize,
		  60L, TimeUnit.SECONDS,
		  new SynchronousQueue<>(),
		  basicThreadFactory,
		  new ThreadPoolExecutor.CallerRunsPolicy());
	}
}



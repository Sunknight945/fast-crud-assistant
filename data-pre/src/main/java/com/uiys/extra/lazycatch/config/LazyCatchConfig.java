package com.uiys.extra.lazycatch.config;

import com.uiys.extra.lazycatch.LazyCatchFactory;
import com.uiys.extra.lazycatch.support.LazyCatchFactoryDefault;
import com.uiys.extra.lazycatch.support.LazyFieldLoaderFactory;
import com.uiys.extra.lazycatch.support.LazyTypeInterceptorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author uiys
 */
@Configuration
public class LazyCatchConfig {

	@Bean
	public LazyCatchFactory lazyCatchFactory(LazyTypeInterceptorFactory lazyTypeInterceptorFactory, ApplicationContext applicationContext) {
		return new LazyCatchFactoryDefault(lazyTypeInterceptorFactory, applicationContext);
	}

	@Bean
	public LazyTypeInterceptorFactory lazyTypeInterceptorFactory(ApplicationContext applicationContext) {
		return new LazyTypeInterceptorFactory(new LazyFieldLoaderFactory(applicationContext));
	}


}



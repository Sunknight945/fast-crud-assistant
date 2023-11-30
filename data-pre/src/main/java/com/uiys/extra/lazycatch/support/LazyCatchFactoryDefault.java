package com.uiys.extra.lazycatch.support;

import com.uiys.extra.lazycatch.LazyCatchFactory;
import java.lang.reflect.Modifier;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author uiys
 */
public class LazyCatchFactoryDefault implements LazyCatchFactory {
	private final LazyTypeInterceptorFactory lazyTypeInterceptorFactory;
	private final ApplicationContext applicationContext;

	public LazyCatchFactoryDefault(LazyTypeInterceptorFactory lazyTypeInterceptorFactory,
	                               ApplicationContext applicationContext) {
		this.lazyTypeInterceptorFactory = lazyTypeInterceptorFactory;
		this.applicationContext = applicationContext;
	}

	@Override
	public <T> T lazyCatch(T traget) {
		if (traget == null) {
			return null;
		} else if (traget.getClass().isPrimitive() || Modifier.isFinal(traget.getClass().getModifiers())) {
			return traget;
		}
		applicationContext.getAutowireCapableBeanFactory().autowireBean(traget);
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setTarget(traget);
		proxyFactory.addAdvice(lazyTypeInterceptorFactory.createLazyType(traget.getClass(), traget));
		return (T) proxyFactory.getProxy();
	}
}



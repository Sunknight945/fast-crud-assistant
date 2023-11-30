package com.uiys.extra.lazycatch.support;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author uiys
 */
public class LazyTypeInterceptorFactory {

	private final Map<Class, Map<String, LazyFieldLoader>> lazyTypeInterceptorMap = new ConcurrentHashMap<>();
	private final LazyFieldLoaderFactory lazyFieldLoaderFactory;

	public LazyTypeInterceptorFactory(LazyFieldLoaderFactory lazyFieldLoaderFactory) {
		this.lazyFieldLoaderFactory = lazyFieldLoaderFactory;
	}


	public LazyTypeInterceptor createLazyType(Class<?> tCls, Object target) {
		Map<String, LazyFieldLoader> lazyFieldLoaderMap = this.lazyTypeInterceptorMap.computeIfAbsent(tCls,
		  targetCls -> createForClass(targetCls));
		return new LazyTypeInterceptor(lazyFieldLoaderMap, target);
	}

	private Map<String, LazyFieldLoader> createForClass(Class createForClas) {
		List<LazyFieldLoader> lazyFieldLoaders = lazyFieldLoaderFactory.createFieldLoader(createForClas);
		return lazyFieldLoaders.stream()
		  .collect(Collectors.toMap(lazyFieldLoader -> lazyFieldLoader.getField()
			.getName(), lazyFieldLoader -> lazyFieldLoader, (keyOld, keyNew) -> keyNew));
	}


}



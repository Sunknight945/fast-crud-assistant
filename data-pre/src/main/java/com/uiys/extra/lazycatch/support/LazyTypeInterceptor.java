package com.uiys.extra.lazycatch.support;

import static ch.qos.logback.core.joran.util.beans.BeanUtil.getPropertyName;
import static ch.qos.logback.core.joran.util.beans.BeanUtil.isGetter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.cglib.proxy.InvocationHandler;

/**
 * @author uiys
 */
@Data
public class LazyTypeInterceptor implements InvocationHandler, MethodInterceptor {
	Map<String, LazyFieldLoader> lazyFieldLoaderMap;
	private Object target;

	public LazyTypeInterceptor(Map<String, LazyFieldLoader> lazyFieldLoaderMap, Object target) {
		this.lazyFieldLoaderMap = lazyFieldLoaderMap;
		this.target = target;
	}

	@Nullable
	@Override
	public Object invoke(@Nonnull MethodInvocation methodInvocation) throws Throwable {
		if (methodInvocation instanceof ProxyMethodInvocation) {
			ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) methodInvocation;
			return invoke(proxyMethodInvocation.getProxy(), proxyMethodInvocation.getMethod(),
			  proxyMethodInvocation.getArguments());
		}
		return invoke(methodInvocation.getThis(), methodInvocation.getMethod(), methodInvocation.getArguments());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] argues) throws Throwable {
		if (isGetter(method)) {
			String propertyName = getPropertyName(method);
			LazyFieldLoader propertyLazyLoader = this.lazyFieldLoaderMap.get(propertyName);

			if (propertyLazyLoader != null) {
				Object data = method.invoke(target, argues);
				if (data != null) {
					return data;
				}
				// 捞取数据
				data = propertyLazyLoader.loadData(proxy);

				if (data != null) {
					if (data.getClass().isAssignableFrom(Optional.class)) {
						if (((Optional<?>) data).isPresent()){
							data = ((Optional<?>) data).get();
						}else {
							data = null;
						}
					}
					// 写入字段值
					FieldUtils.writeField(target, propertyName, data, true);
				}
				return data;
			} else {
				method.invoke(target, argues);
			}
		}

		return method.invoke(target, argues);
	}
}



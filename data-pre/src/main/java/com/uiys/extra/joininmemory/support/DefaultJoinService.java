package com.uiys.extra.joininmemory.support;

import com.uiys.extra.joininmemory.JoinItemsExecutor;
import com.uiys.extra.joininmemory.JoinItemsExecutorFactory;
import com.uiys.extra.joininmemory.JoinService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author uiys
 * @date 2023/11/21
 */
public class DefaultJoinService implements JoinService {

	private final JoinItemsExecutorFactory joinItemsExecutorFactory;

	private final Map<Class, JoinItemsExecutor> cache = new ConcurrentHashMap<>();

	public DefaultJoinService(JoinItemsExecutorFactory joinItemsExecutorFactory) {
		this.joinItemsExecutorFactory = joinItemsExecutorFactory;
	}

	@Override
	public <T> void joinInMemory(Class<T> tCls, List<T> t) {
		this.cache.computeIfAbsent(tCls, this::createJoinItemsExecutor)
		  .execute(t);
	}


	@Override
	public <T> void register(Class<T> tCls) {
		this.cache.computeIfAbsent(tCls, this::createJoinItemsExecutor);
	}


	private JoinItemsExecutor createJoinItemsExecutor(Class aClass) {
		JoinItemsExecutor aFor = this.joinItemsExecutorFactory.createFor(aClass);
		return aFor;
	}
}



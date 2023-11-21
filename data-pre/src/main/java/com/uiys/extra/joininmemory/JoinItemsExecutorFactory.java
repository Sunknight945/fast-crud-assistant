package com.uiys.extra.joininmemory;

/**
 * @author uiys
 * 从 class 中解析 并创建 joinItemsExecutor
 */
public interface JoinItemsExecutorFactory {

	/**
	 * 为 类 创建 join 执行器
	 *
	 * @param cls 类
	 * @param <D> 泛型
	 * @return 执行器
	 */
	<D> JoinItemsExecutor<D> createFor(Class<D> cls);
}

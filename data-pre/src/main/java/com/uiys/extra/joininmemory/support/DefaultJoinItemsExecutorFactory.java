package com.uiys.extra.joininmemory.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.uiys.extra.joininmemory.JoinItemExecutor;
import com.uiys.extra.joininmemory.JoinItemExecutorFactory;
import com.uiys.extra.joininmemory.JoinItemsExecutor;
import com.uiys.extra.joininmemory.JoinItemsExecutorFactory;
import com.uiys.extra.joininmemory.annotation.JoinInMemoryConfig;
import com.uiys.extra.joininmemory.annotation.JoinInMemoryExecutorType;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.valid.BusinessException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * @author uiys
 * @date 2023/11/21
 */
@Slf4j
public class DefaultJoinItemsExecutorFactory implements JoinItemsExecutorFactory {

	private final List<JoinItemExecutorFactory> joinItemExecutorFactories;
	private final Map<String, ExecutorService> executorServiceMap;

	public DefaultJoinItemsExecutorFactory(Collection<? extends JoinItemExecutorFactory> joinItemExecutorFactories,
	                                       Map<String, ExecutorService> executorServiceMap) {
		this.joinItemExecutorFactories = Lists.newArrayList(joinItemExecutorFactories);
		// 按执行顺序排序
		AnnotationAwareOrderComparator.sort(this.joinItemExecutorFactories);
		this.executorServiceMap = executorServiceMap;
	}


	/**
	 * 为 类 创建 join 执行器
	 *
	 * @param cls 类
	 * @return 执行器
	 */
	@Override
	public <D> JoinItemsExecutor<D> createFor(Class<D> cls) {
		// 依次遍历 joinItemExecutorFactory, 收集 joinItemExecutor .
		List<JoinItemExecutor<D>> joinItemExecutors = this.joinItemExecutorFactories.stream()
		  .flatMap(joinItemExecutorFactory -> joinItemExecutorFactory.createForType(cls)
			.stream())
		  .collect(Collectors.toList());

		// 从 class 上读取配置信息
		JoinInMemoryConfig joinInMemoryConfig = cls.getAnnotation(JoinInMemoryConfig.class);

		// 封装为 joinItemsExecutor
		return buildJoinItemsExecutor(cls, joinInMemoryConfig, joinItemExecutors);
	}

	private <D> JoinItemsExecutor<D> buildJoinItemsExecutor(Class<D> cls, JoinInMemoryConfig joinInMemoryConfig,
	                                                        List<JoinItemExecutor<D>> joinItemExecutors) {
		// 使用 串行执行器
		if (joinInMemoryConfig == null || joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.SERIAL) {
			log.info("joinInMemory for {} use serial executor", cls);
			return new SerialJoinItemsExecutor<>(cls, joinItemExecutors);
		}
		// 使用 并行处理器
		if (joinInMemoryConfig.executorType() == JoinInMemoryExecutorType.PARALLEL) {
			log.info("joinInMemory for {} use parallel executor", cls);
			ExecutorService executor = executorServiceMap.get(joinInMemoryConfig.executorName());
			Preconditions.checkArgument(executor != null);
			return null;
		}
		throw new BusinessException(ErrorCode.NOTFOUND, "处理器不合规");
	}
}



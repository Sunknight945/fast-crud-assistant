package com.uiys.extra.joininmemory.support;

import com.google.common.base.Preconditions;
import com.uiys.extra.joininmemory.JoinItemExecutor;
import com.uiys.extra.joininmemory.JoinItemsExecutor;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;

/**
 * @author uiys
 * @date 2023/11/21
 */
public abstract class AbstractJoinItemsExecutor<DATA> implements JoinItemsExecutor<DATA> {

	@Getter
	private final Class<DATA> dataCls;

	@Getter
	private final List<JoinItemExecutor<DATA>> joinItemExecutors;

	public AbstractJoinItemsExecutor(Class<DATA> dataCls, List<JoinItemExecutor<DATA>> joinItemExecutors) {
		Preconditions.checkArgument(dataCls != null);
		Preconditions.checkArgument(joinItemExecutors != null);
		this.dataCls = dataCls;
		this.joinItemExecutors = joinItemExecutors;
		this.joinItemExecutors.sort(Comparator.comparing(JoinItemExecutor::runOnLevel));
	}
}



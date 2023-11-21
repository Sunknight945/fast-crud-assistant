package com.uiys.extra.joininmemory;

import java.util.List;

/**
 * @author uiys
 * @date 2023/11/21
 */
public interface JoinItemExecutorFactory {
	<DATA> List<JoinItemExecutor<DATA>> createForType(Class<DATA> cls);

}

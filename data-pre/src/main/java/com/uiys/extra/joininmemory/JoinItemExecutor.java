package com.uiys.extra.joininmemory;

import java.util.List;

/**
 * @author uiys
 */
public interface JoinItemExecutor<DATA> {

	void execute(List<DATA> data);

	default Integer runOnLevel() {
		return 10;
	}


}

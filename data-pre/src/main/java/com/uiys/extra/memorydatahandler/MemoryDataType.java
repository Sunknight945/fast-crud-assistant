package com.uiys.extra.memorydatahandler;

import java.util.List;

/**
 * @author uiys
 */
public interface MemoryDataType {
	<DATA> void execute(List<DATA> dataList);
}

package com.uiys.extra.memorydatahandler;

import java.util.List;

/**
 * @author uiys
 */
public interface MemoryDataField<DATA> {

	void execute(List<DATA> dataList);
}

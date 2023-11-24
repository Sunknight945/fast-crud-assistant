package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataField;
import java.util.List;

/**
 * @author uiys
 */
public class SerialMemoryDataType extends MemoryDataTypeAbstract {

	protected SerialMemoryDataType(List<MemoryDataField> memoryDataFieldList) {
		super(memoryDataFieldList);
	}

	@Override
	public <DATA> void execute(List<DATA> dataList) {
		getMemoryDataFieldList().forEach(memoryDataField -> memoryDataField.execute(dataList));
	}
}



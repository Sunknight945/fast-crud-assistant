package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataField;
import com.uiys.extra.memorydatahandler.MemoryDataType;
import java.util.List;
import lombok.Getter;

/**
 * @author uiys
 */
@Getter
public abstract class MemoryDataTypeAbstract implements MemoryDataType {

	/**
	 * XxxDetail里面多个field的类型为非基本类型时需要处理的多字段执行器.
	 */
	private final List<MemoryDataField> memoryDataFieldList;


	protected MemoryDataTypeAbstract(List<MemoryDataField> memoryDataFieldList) {
		this.memoryDataFieldList = memoryDataFieldList;
	}
}



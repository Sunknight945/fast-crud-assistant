package com.uiys.extra.memorydatahandler;

import java.lang.annotation.Annotation;

/**
 * @author uiys
 * 构建 MemoryDataType
 */
public interface MemoryDataTypeBuilder<Ann extends Annotation> {
	<DATA> MemoryDataType build(Class<DATA> cls);
}

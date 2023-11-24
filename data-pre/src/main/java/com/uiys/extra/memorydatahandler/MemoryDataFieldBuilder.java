package com.uiys.extra.memorydatahandler;

import com.uiys.extra.memorydatahandler.annotation.MemoryDataHandler;
import java.lang.reflect.Field;

/**
 * @author uiys
 */
public interface MemoryDataFieldBuilder<DATA> {


	MemoryDataField<DATA> build(Class<DATA> cls, Field field, MemoryDataHandler mergedAnnotation);
}



package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataField;
import com.uiys.extra.memorydatahandler.MemoryDataFieldBuilder;
import com.uiys.extra.memorydatahandler.MemoryDataType;
import com.uiys.extra.memorydatahandler.MemoryDataTypeBuilder;
import com.uiys.extra.memorydatahandler.annotation.MemoryDataHandler;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author uiys
 */
public class MemoryDataTypeBuilderDefault implements MemoryDataTypeBuilder<MemoryDataHandler> {

	private final Class<MemoryDataHandler> annCls;
	private final MemoryDataFieldBuilder memoryDataFieldBuilder;


	public <DATA> MemoryDataTypeBuilderDefault(MemoryDataFieldBuilder<DATA> memoryDataFieldBuilder) {
		this.annCls = MemoryDataHandler.class;
		this.memoryDataFieldBuilder = memoryDataFieldBuilder;
	}

	@Override
	public <DATA> MemoryDataType build(Class<DATA> cls) {
		List<Field> allFieldsList = FieldUtils.getAllFieldsList(cls);
		List<MemoryDataField> collect = allFieldsList.stream()
		  .filter(field -> Objects.nonNull(field.getAnnotation(annCls)))
		  .map(field -> memoryDataFieldBuilder.build(cls, field, AnnotatedElementUtils.findMergedAnnotation(field,
			this.annCls)))
		  .collect(Collectors.toList());
		return new SerialMemoryDataType(collect);
	}
}



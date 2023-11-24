package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataField;
import com.uiys.extra.memorydatahandler.MemoryDataFieldBuilder;
import com.uiys.extra.memorydatahandler.MemoryDataType;
import com.uiys.extra.memorydatahandler.MemoryDataTypeBuilder;
import com.uiys.extra.memorydatahandler.annotation.MemoryDataHandler;
import com.uiys.extra.memorydatahandler.annotation.MemoryDataHandlerTypeConfig;
import com.uiys.extra.memorydatahandler.annotation.MemoryRunWays;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.valid.BusinessException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author uiys
 */
public class MemoryDataTypeBuilderDefault implements MemoryDataTypeBuilder<MemoryDataHandler> {

	private final Class<MemoryDataHandler> annCls;
	private final MemoryDataFieldBuilder memoryDataFieldBuilder;
	private final ExecutorService executorService;


	public <DATA> MemoryDataTypeBuilderDefault(MemoryDataFieldBuilder<DATA> memoryDataFieldBuilder,
	                                           ExecutorService executorService) {
		this.annCls = MemoryDataHandler.class;
		this.memoryDataFieldBuilder = memoryDataFieldBuilder;
		this.executorService = executorService;
	}

	@Override
	public <DATA> MemoryDataType build(Class<DATA> cls) {
		List<Field> allFieldsList = FieldUtils.getAllFieldsList(cls);
		List<MemoryDataField> memoryDataFields = allFieldsList.stream()
		  .filter(field -> Objects.nonNull(field.getAnnotation(annCls)))
		  .map(field -> memoryDataFieldBuilder.build(cls, field, AnnotatedElementUtils.findMergedAnnotation(field,
			this.annCls)))
		  .collect(Collectors.toList());

		MemoryDataHandlerTypeConfig annotation = cls.getAnnotation(MemoryDataHandlerTypeConfig.class);
		if (annotation == null || annotation.runWay()
		  .equals(MemoryRunWays.Serial)) {
			return new SerialMemoryDataType(memoryDataFields);
		} else if (annotation.runWay()
		  .equals(MemoryRunWays.Parallel)) {
			return new ParallelMemoryDataType(memoryDataFields, executorService);
		} else {
			throw new BusinessException(ErrorCode.NOTFOUND, "找不到指定的处理方式");
		}


	}
}



package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataExecutor;
import com.uiys.extra.memorydatahandler.MemoryDataType;
import com.uiys.extra.memorydatahandler.MemoryDataTypeBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;

/**
 * @author uiys
 */
public class MemoryDataExecutorDefault implements MemoryDataExecutor {

	private final Map<Class, MemoryDataType> memoryDataTypeCache = new ConcurrentHashMap<>();

	private final MemoryDataTypeBuilder memoryDataTypeBuilder;

	public MemoryDataExecutorDefault(MemoryDataTypeBuilder memoryDataTypeBuilder) {
		this.memoryDataTypeBuilder = memoryDataTypeBuilder;
	}

	/**
	 * 需要处理的单挑数据
	 *
	 * @param data 需要处理的数据
	 */
	@Override
	public <DATA> void load(DATA data) {
		Assert.notNull(data, () -> "data不能为null!");
		load(Collections.singletonList(data));
	}

	/**
	 * 需要处理的多条数据
	 *
	 * @param dataList 需要处理的多条数据
	 */
	@Override
	public <DATA> void load(List<DATA> dataList) {
		Assert.notEmpty(dataList, () -> "dataList不能为空!");
		load((Class<DATA>) dataList.get(0)
		  .getClass(), dataList);
	}


	private <DATA> void load(Class<DATA> cls, List<DATA> dataList) {
		this.memoryDataTypeCache.computeIfAbsent(cls, this::createMemoryDataType)
		  .execute(dataList);
	}


	private <DATA> MemoryDataType createMemoryDataType(Class<DATA> cls) {
		return memoryDataTypeBuilder.build(cls);
	}


}













































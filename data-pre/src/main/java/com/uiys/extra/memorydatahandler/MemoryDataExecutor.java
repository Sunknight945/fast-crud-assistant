package com.uiys.extra.memorydatahandler;

import java.util.List;

/**
 * @author uiys
 * 内存数据执行器
 * 由它将 需要处理的数据 装载进来
 * 先打造 由 @MemoryDataHandler 注解标识的字段的执行器们().
 * 然后去执行数据的加载,分组,转换类型和拼接等.
 */
public interface MemoryDataExecutor {


	/**
	 * 需要处理的单挑数据
	 *
	 * @param data   需要处理的数据
	 * @param <DATA> 数据的泛型符号
	 */
	<DATA> void load(DATA data);

	/**
	 * 需要处理的多条数据
	 *
	 * @param dataList 需要处理的多条数据
	 * @param <DATA>   数据需要的泛型符号
	 */
	<DATA> void load(List<DATA> dataList);


}

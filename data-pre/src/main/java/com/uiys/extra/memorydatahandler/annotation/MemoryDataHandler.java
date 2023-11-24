package com.uiys.extra.memorydatahandler.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author uiys
 * 处理的数据结构是
 * {
 * "预先查询结构1": {
 * "自身信息1": "Welcome to JSON Viewer Pro",
 * "自身信息2": "Welcome to JSON Viewer Pro",
 * "其他数据_1_key": "数据1的key值",
 * "其他数据_2_key": "数据2的key值",
 * "其他数据_3_key": "数据3的key值"
 * },
 * "其他数据_1": {},
 * "其他数据_2": {},
 * "其他数据_3": {}
 * }
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemoryDataHandler {

	/**
	 * 此条数据来源的key.
	 *
	 * @return 如 "#{xxx.ssId}"
	 */
	String sourceKey();

	/**
	 * 利用 sourceKey 查询出来加载数据的 expressionSpel 表达式.
	 *
	 * @return 如"#{//@xxxRepository.findByIds(#root)}"
	 */
	String joinDataLoader();

	/**
	 * joinData自身的数据key的字段. 比如 这个数据是由 id 查询出来的, 那么这个字段就是 id
	 * 这个id字段的值就是此joinData的分组字段,
	 * 分组字段存在的意义是 将此条 data 拼接到正确的 source data 部分.
	 * 比如 获取 由 sourceKey spel 表达式获取到 sourceData 的 sourceKey, 由 dataKey 的 spel 表达式 获取到 joinData
	 * 的 dataKey. 如果匹配上了, 那么就 把 当前由 dataKey 分组的 joinData 数据 组装到 当前Stream流处理的 sourceData 上.
	 *
	 * @return 如 "#{id}"
	 */
	String dataKey();

	/**
	 * 查询出来的join 如果需要进行 entity to vo 等的转化器.
	 *
	 * @return 如 "#{T(com.uiys.xxMapper).INSTANCE.u2Vo(#root)}"
	 */
	String joinDataConverter();

	/**
	 * 并行处理的时候可能存在的执行优先级别
	 *
	 * @return 执行排序 低到高
	 */
	int executeLevel() default 5;

}

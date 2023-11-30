package com.uiys.extra.lazycatch;

/**
 * @author uiys
 * 单条数据延时抓取
 * (注意: 禁止在批量数据下使用, 因为模型中的数据获取依赖SpeL表达式,数据如果有上下依赖,
 * 会出现下级数据依赖的上级数据为null时,下级的SpeL解析直接报错!)
 */
public interface LazyCatchFactory {
	<T> T lazyCatch(T t);
}

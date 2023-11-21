package com.uiys.extra.joininmemory;

import cn.hutool.core.collection.CollectionUtil;
import java.util.Collections;
import java.util.List;

/**
 * @author uiys
 */
public interface JoinService {

	default <T> void joinInMemory(T t) {
		if (t == null) {
			return;
		}
		joinInMemory((Class<T>) t.getClass(), Collections.singletonList(t));
	}

	default <T> void joinInMemory(List<T> t) {
		if (CollectionUtil.isEmpty(t)) {
			return;
		}
		if (t.size() == 1) {
			joinInMemory(t.get(0));
		} else {
			joinInMemory((Class<T>) t.get(0)
			  .getClass(), t);
		}

	}


	<T> void joinInMemory(Class<T> tCls, List<T> t);

	<T> void register(Class<T> tCls);
}

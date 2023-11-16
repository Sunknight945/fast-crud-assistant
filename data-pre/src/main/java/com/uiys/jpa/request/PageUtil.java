package com.uiys.jpa.request;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @author uiys
 */
public final class PageUtil {
	public static <E> PageRequest get(PageRequestWrapper<E> wrapper, Sort.Direction sort, List<String> sortItems) {
		String[] properties = {"createdAt", "id"};
		boolean hasNull = ArrayUtil.hasNull(wrapper.getPageNum(), wrapper.getPageSize());
		Sort noNullSort = Sort.by(sort == null ? Sort.Direction.DESC : sort, CollUtil.isEmpty(sortItems) ? properties
		  : sortItems.toArray(new String[0]));
		return PageRequest.of(hasNull ? 0 : wrapper.getPageNum() - 1, hasNull ? 1000 : wrapper.getPageSize(),
		  noNullSort);
	}

	public static <E> PageRequest get(PageRequestWrapper<E> wrapper) {
		return get(wrapper, null, Lists.newArrayList());
	}

	public static <E> com.baomidou.mybatisplus.extension.plugins.pagination.Page<E> getPage(PageRequestWrapper<E> wrapper) {
		boolean hasNull = ArrayUtil.hasNull(wrapper.getPageNum(), wrapper.getPageSize());
		return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(hasNull ? 1 : wrapper.getPageNum(),
		  hasNull ? 100 : wrapper.getPageSize());
	}

	public static <E, R> Page<R> change(Page<E> page, Function<E, R> function) {
		List<R> collect = page.getContent()
		  .stream()
		  .map(function)
		  .collect(Collectors.toList());
		return new PageImpl<>(collect, page.getPageable(), page.getTotalElements());
	}


}



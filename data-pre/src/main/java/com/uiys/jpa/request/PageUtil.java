package com.uiys.jpa.request;

import cn.hutool.core.util.ArrayUtil;
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
	public static <E> PageRequest get(PageRequestWrapper<E> wrapper, Sort.Direction sort, String... sortItem) {
		String[] properties = {"createdAt", "id"};
		boolean hasNull = ArrayUtil.hasNull(wrapper.getPageNum(), wrapper.getPageSize());
		Sort noNullSort = Sort.by(sort == null ? Sort.Direction.DESC : sort, hasNull ? properties : sortItem);
		return PageRequest.of(hasNull ? 0 : wrapper.getPageNum() - 1, hasNull ? 1000 : wrapper.getPageSize(),
		  noNullSort);
	}

	public static <E> PageRequest get(PageRequestWrapper<E> wrapper) {
		return get(wrapper, null, (String) null);
	}

	public static <E, R> Page<R> change(Page<E> page, Function<E, R> function) {
		List<R> collect = page.getContent()
		  .stream()
		  .map(function)
		  .collect(Collectors.toList());
		return new PageImpl<>(collect, page.getPageable(), page.getTotalElements());
	}


}



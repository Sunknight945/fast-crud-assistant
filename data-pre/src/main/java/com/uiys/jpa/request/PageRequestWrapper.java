package com.uiys.jpa.request;

import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString
@EqualsAndHashCode
public class PageRequestWrapper<E> {
	private E query;
	private Integer pageSize;
	private Integer pageNum;
	private Map<String, String> sort;

	public PageRequestWrapper() {
	}

	private PageRequestWrapper(E query, Integer pageSize, Integer pageNum, Map<String, String> sort) {
		this.query = query;
		this.pageSize = pageSize;
		this.pageNum = pageNum;
		this.sort = sort;
	}


	public static <E, T> PageRequestWrapper<E> of(PageRequestWrapper<T> wrapper, E query) {
		return new PageRequestWrapper<E>(query, wrapper.pageSize, wrapper.pageNum, wrapper.sort);
	}
}



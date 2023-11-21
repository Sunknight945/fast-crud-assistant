package com.uiys.jpa.request;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString
@EqualsAndHashCode
public class PageRequestWrapper<E> implements Serializable {
	private E query;
	private Integer pageSize;
	private Integer pageNum;
	private Map<String, String> sort;

	public PageRequestWrapper() {
	}

	public static <E> PageRequestWrapper<E> defaultWrapper() {
		return new PageRequestWrapper<>(1, 1000);
	}


	private PageRequestWrapper(Integer pageNum, Integer pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
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



package com.uiys.jpa.result;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Page;


@Data
@ToString
@EqualsAndHashCode
public class GeneralPageResult<E> {
	private Integer pageSize;

	private Integer pageNum;

	private Integer totalPage;

	private Long totalSize;

	private List<E> records;

	public GeneralPageResult(Integer pageSize, Integer pageNum, Long totalSize, List<E> records) {
		this.pageSize = pageSize;
		this.pageNum = pageNum;
		this.totalPage = Integer.valueOf(String.valueOf((totalSize % pageSize > 0) ? ((totalSize / pageSize) + 1) :
		  (totalSize / pageSize)));
		this.totalSize = totalSize;
		this.records = records;
	}

	private static <E> GeneralPageResult<E> of(Integer pageSize, Integer pageNum, Long totalSize, List<E> records) {
		return new GeneralPageResult<>(pageSize, pageNum, totalSize, records);
	}


	public static <E, T> GeneralPageResult<E> of(Page<T> page, List<E> records) {
		return new GeneralPageResult<>(page.getPageable()
		  .getPageSize(), page.getPageable()
		  .getPageNumber() + 1, page.getTotalElements(), records);
	}

	public static <E, T> GeneralPageResult<E> of(Page<E> page) {
		return of(page, page.getContent());
	}


}



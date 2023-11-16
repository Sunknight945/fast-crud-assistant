package com.uiys.extra.filterpipeline.filterselector;

import com.uiys.extra.filterpipeline.filter.BusFilter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author uiys
 */
@Slf4j
public class LocalBaseFilterSelector implements FilterSelector {


	private static final List<String> FILTER_NAMES = new ArrayList<>();

	public void addFilterName(String filterName) {
		log.info("添加了{} filter", filterName);
		FILTER_NAMES.add(filterName);
	}

	@Override
	public List<String> getFilterNames() {
		return FILTER_NAMES;
	}

	@Override
	public <E> Boolean matchFilter(BusFilter<E> filter) {
		return FILTER_NAMES.contains(filter.getClass()
		  .getSimpleName());
	}
}



package com.uiys.extra.filterpipeline.filterselector;

import com.uiys.extra.filterpipeline.filter.BusFilter;
import java.util.List;

/**
 * @author uiys
 * @date 2023/11/16
 */
public interface FilterSelector {


	List<String> getFilterNames();

	<E> Boolean matchFilter(BusFilter<E> filter);


}

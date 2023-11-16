package com.uiys.extra.filterpipeline;

import com.uiys.extra.filterpipeline.chain.DefaultBusFilterChain;
import com.uiys.extra.filterpipeline.context.BusContext;
import com.uiys.extra.filterpipeline.filter.BusFilter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author uiys
 * @date 2023/11/16
 */
@Slf4j
public class FilterPipeLine<E extends BusContext> {

	@Getter
	private DefaultBusFilterChain<E> chain;

	public void addFilter(String filterDesc, BusFilter<E> filter) {
		log.info("添加 {} filter", filterDesc);
		chain = new DefaultBusFilterChain<>(chain, filter);
	}

}



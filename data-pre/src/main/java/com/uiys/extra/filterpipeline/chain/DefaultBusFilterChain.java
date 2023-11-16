package com.uiys.extra.filterpipeline.chain;

import com.uiys.extra.filterpipeline.context.BusContext;
import com.uiys.extra.filterpipeline.filter.BusFilter;
import java.util.Objects;

/**
 * @author uiys
 * @date 2023/11/16
 */
public class DefaultBusFilterChain<E extends BusContext> implements BusFilterChain<E> {

	private final DefaultBusFilterChain<E> next;
	private final BusFilter<E> filter;

	public DefaultBusFilterChain(DefaultBusFilterChain<E> chain, BusFilter<E> filter) {
		this.next = chain;
		this.filter = filter;
	}


	@Override
	public void hand(E e) {
		filter.doFilter(e, this);
	}

	@Override
	public void fireNext(E e) {
		if (Objects.nonNull(next)) {
			next.hand(e);
		}
	}
}



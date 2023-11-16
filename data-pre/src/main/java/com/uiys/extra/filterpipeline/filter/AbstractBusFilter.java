package com.uiys.extra.filterpipeline.filter;

import com.uiys.extra.filterpipeline.chain.BusFilterChain;
import com.uiys.extra.filterpipeline.context.AbstractBusContext;

/**
 * @author uiys
 * @date 2023/11/16
 */
public abstract class AbstractBusFilter<E extends AbstractBusContext> implements BusFilter<E> {
	@Override
	public void doFilter(E e, BusFilterChain<E> chain) {
		if (e.getFilterSelector()
		  .matchFilter(this)) {
			handler(e);
		}
		if (e.continued()) {
			chain.fireNext(e);
		}
	}

	protected abstract void handler(E e);


}



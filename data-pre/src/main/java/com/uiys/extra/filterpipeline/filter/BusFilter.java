package com.uiys.extra.filterpipeline.filter;

import com.uiys.extra.filterpipeline.chain.BusFilterChain;

/**
 * @author uiys
 * @date 2023/11/16
 */
public interface BusFilter<E> {


	void doFilter(E e, BusFilterChain<E> chain);

}

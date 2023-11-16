package com.uiys.extra.filterpipeline.chain;

/**
 * @author uiys
 * @date 2023/11/16
 */
public interface BusFilterChain<E> {


	void hand(E e);

	void fireNext(E e);

}

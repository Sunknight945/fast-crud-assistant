package com.uiys.jpa.support;

import java.util.function.Supplier;

/**
 * @author uiys
 * @date 2023/10/21
 */
public interface Creator<T, Id> {

	UpdateHandler<T> create(Supplier<T> updater);

}

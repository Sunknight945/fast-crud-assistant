package com.uiys.jpa.support;

import java.util.function.Supplier;


public interface Loader<T, ID> {
	UpdateHandler<T> loadById(ID var1);

	UpdateHandler<T> load(Supplier<T> var1);
}



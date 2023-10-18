package com.uiys.jpa.support;

import java.util.Optional;
import java.util.function.Consumer;


public interface Executor<T> {
	Optional<T> execute();

	Consumer<T> successHook(T t);

	Consumer<Throwable> errorHook(Throwable t);
}

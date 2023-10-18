package com.uiys.jpa.support;

import org.springframework.data.repository.CrudRepository;


public abstract class EntityOperations {

	public EntityOperations() {
	}

	public static <T, Id> EntityUpdater<T, Id> doUpdate(CrudRepository<T, Id> repository) {
		return new EntityUpdater<>(repository);
	}


	public static <T, Id> EntityCreator<T, Id> doCreate(CrudRepository<T, Id> repository) {
		return new EntityCreator<>(repository);
	}
}
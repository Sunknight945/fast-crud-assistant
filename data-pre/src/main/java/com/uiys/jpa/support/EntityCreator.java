package com.uiys.jpa.support;

import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.valid.BusinessException;
import com.uiys.jpa.valid.UpdateGroup;
import io.vavr.control.Try;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;


@Slf4j
public class EntityCreator<T, Id> extends BaseEntityOperation implements Creator<T, Id>, UpdateHandler<T>, Executor<T> {

	private final CrudRepository<T, Id> crudRepository;

	private T t;


	public EntityCreator(CrudRepository<T, Id> repository) {
		this.crudRepository = repository;
	}


	@Override
	public UpdateHandler<T> create(Supplier<T> updater) {
		t = updater.get();
		return this;
	}

	@Override
	public Executor<T> update(Consumer<T> consumer) {
		consumer.accept(t);
		return this;
	}


	@Override
	public Optional<T> execute() {
		this.doValidate(t, UpdateGroup.class);
		T t1 = Try.of(() -> this.crudRepository.save(t))
		  .onSuccess(this.successHook(t))
		  .onFailure(this.errorHook(new BusinessException(ErrorCode.ERROR_CODE)))
		  .get();
		return Optional.of(t1);
	}


	@Override
	public Consumer<T> successHook(T t) {
		return t1 -> log.info("创建{}成功:", t1);
	}

	@Override
	public Consumer<Throwable> errorHook(Throwable t) {
		return t1 -> {
			log.warn("创建失败", t);
		};

	}


}



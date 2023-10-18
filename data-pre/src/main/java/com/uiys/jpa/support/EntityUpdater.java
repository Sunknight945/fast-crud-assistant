package com.uiys.jpa.support;


import com.google.common.base.Preconditions;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.valid.BusinessException;
import com.uiys.jpa.valid.UpdateGroup;
import io.vavr.control.Try;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;


@Slf4j
public class EntityUpdater<T, ID> extends BaseEntityOperation implements Loader<T, ID>, UpdateHandler<T>, Executor<T> {

	private final CrudRepository<T, ID> repository;
	private final Consumer<T> successHook = (t) -> {
		log.info("执行更新表{}的{}数据成功!", t.getClass()
		  .getSimpleName(), t);
	};
	private T t;
	private final Consumer<? super Throwable> errorHook = (e) -> {
		log.warn("执行更新{}数据失败,原因:{}", t.getClass()
		  .getSimpleName(), e.getLocalizedMessage());
	};

	public EntityUpdater(CrudRepository<T, ID> repository) {
		this.repository = repository;
	}

	@Override
	public UpdateHandler<T> loadById(ID id) {
		Preconditions.checkArgument(Objects.nonNull(id), "id is null");
		Optional<T> oldSource = repository.findById(id);
		t = oldSource.orElseThrow(() -> new BusinessException(ErrorCode.NOTFOUND, "id is: " + id));
		return this;
	}

	@Override
	public UpdateHandler<T> load(Supplier<T> supplier) {
		t = supplier.get();
		return this;
	}

	@Override
	public Optional<T> execute() {
		this.doValidate(t, UpdateGroup.class);
		T save = Try.of(() -> this.repository.save(this.t))
		  .onSuccess(successHook)
		  .onFailure(errorHook)
		  .get();
		return Optional.ofNullable(save);
	}

	@Override
	public Consumer<T> successHook(T t) {
		return t1 -> log.info("执行更新表{}的{}数据成功!", t.getClass()
		  .getSimpleName(), t);
	}

	@Override
	public Consumer<Throwable> errorHook(Throwable t) {
		log.warn("执行更新{}数据失败,原因:{}", t.getClass()
		  .getSimpleName(), t.getLocalizedMessage());
		return tt1 -> t.printStackTrace();
	}


	@Override
	public Executor<T> update(Consumer<T> consumer) {
		Preconditions.checkArgument(Objects.nonNull(this.t), "entity is null");
		consumer.accept(this.t);
		return this;
	}
}

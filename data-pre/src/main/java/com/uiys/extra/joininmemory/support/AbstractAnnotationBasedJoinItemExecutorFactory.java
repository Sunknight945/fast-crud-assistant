package com.uiys.extra.joininmemory.support;

import com.uiys.extra.joininmemory.JoinItemExecutor;
import com.uiys.extra.joininmemory.JoinItemExecutorFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;


/**
 * @author uiys
 * @date 2023/11/21
 */
public abstract class AbstractAnnotationBasedJoinItemExecutorFactory<A extends Annotation> implements JoinItemExecutorFactory {

	private final Class<A> annotationCls;

	public AbstractAnnotationBasedJoinItemExecutorFactory(Class<A> annotationCls) {
		this.annotationCls = annotationCls;
	}

	@Override
	public <DATA> List<JoinItemExecutor<DATA>> createForType(Class<DATA> cls) {
		List<Field> fieldsListWithAnnotation = FieldUtils.getAllFieldsList(cls);
		return fieldsListWithAnnotation.stream()
		  .filter(Objects::nonNull)
		  .map(field -> createForField(cls, field, AnnotatedElementUtils.findMergedAnnotation(field, annotationCls)))
		  .filter(Objects::nonNull)
		  .collect(Collectors.toList());
	}


	private <DATA> JoinItemExecutor<DATA> createForField(Class<DATA> cls, Field field, A annotation) {
		if (annotation == null) {
			return null;
		}
		JoinItemExecutorAdapter build = JoinItemExecutorAdapter.builder()
		  .name(createName(cls, field, annotation))
		  .runLevel(createRunLevel(cls, field, annotation))
		  .getKeyFromSourceData(collectKeysFromSourceData(cls, field, annotation))
		  .loadJoinDatasFromJoinKeys(pullOutDataLoader(cls, field, annotation))
		  .getKeyFromJoinData(collectKeysFromJoinData(cls, field, annotation))
		  .convertJoinToResult(createConvertJoin(cls, field, annotation))
		  .onFound(createOnFound(cls, field, annotation))
		  .onNotFound(createOnNotFound(cls, field, annotation))
		  .build();

		return build;
	}

	private <DATA> BiConsumer<Object, Object> createOnNotFound(Class<DATA> cls, Field field,
	                                                           A annotation) {
		return null;
	}

	protected abstract <DATA> BiConsumer<Object, List<Object>> createOnFound(Class<DATA> cls, Field field,
	                                                                         A annotation);

	protected abstract <DATA> Function<Object, Object> createConvertJoin(Class<DATA> cls, Field field,
	                                                                     A annotation);

	protected abstract <DATA> Function<Object, Object> collectKeysFromJoinData(Class<DATA> cls, Field field,
	                                                                           A annotation);


	protected abstract <DATA> Function<List<Object>, List<Object>> pullOutDataLoader(Class<DATA> cls, Field field,
	                                                                                 A annotation);

	protected abstract <DATA> Function<Object, Object> collectKeysFromSourceData(Class<DATA> cls, Field field,
	                                                                             A annotation);

	protected abstract <DATA> Integer createRunLevel(Class<DATA> cls, Field field, A annotation);

	private <DATA> String createName(Class<DATA> cls, Field field, A annotation) {

		return "class[" +
		  cls.getSimpleName() +
		  "]" +
		  "#field[" +
		  field.getName() +
		  "]" +
		  "-" +
		  annotation.getClass()
			.getSimpleName();

	}
}



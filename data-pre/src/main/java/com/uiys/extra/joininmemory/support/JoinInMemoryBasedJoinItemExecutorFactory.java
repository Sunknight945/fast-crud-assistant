package com.uiys.extra.joininmemory.support;

import com.uiys.extra.joininmemory.annotation.JoinInMemory;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author uiys
 * @date 2023/11/21
 */
@Slf4j
public class JoinInMemoryBasedJoinItemExecutorFactory extends AbstractAnnotationBasedJoinItemExecutorFactory<JoinInMemory> {

	private final ExpressionParser expressionParser = new SpelExpressionParser();
	private final TemplateParserContext templateParserContext = new TemplateParserContext();
	private final BeanResolver beanResolver;


	public JoinInMemoryBasedJoinItemExecutorFactory(BeanResolver beanResolver) {
		super(JoinInMemory.class);
		this.beanResolver = beanResolver;
	}


	@Override
	protected <DATA> BiConsumer<Object, List<Object>> createOnFound(Class<DATA> cls, Field field,
	                                                                JoinInMemory annotation) {
		boolean isCollection = Collection.class.isAssignableFrom(field.getType());
		return new DataSetter<>(field.getName(), isCollection);
	}

	@Override
	protected <DATA> Function<Object, Object> createConvertJoin(Class<DATA> cls, Field field,
	                                                            JoinInMemory annotation) {
		return new DataGetter<>(annotation.joinDataConvert());
	}

	@Override
	protected <DATA> Function<Object, Object> collectKeysFromJoinData(Class<DATA> cls, Field field,
	                                                                  JoinInMemory annotation) {
		return new DataGetter<>(annotation.keyFromJoinData());
	}

	@Override
	protected <DATA> Function<List<Object>, List<Object>> pullOutDataLoader(Class<DATA> cls, Field field,
	                                                                        JoinInMemory annotation) {
		return new DataGetter<>(annotation.loader());
	}

	@Override
	protected <DATA> Function<Object, Object> collectKeysFromSourceData(Class<DATA> cls, Field field,
	                                                                    JoinInMemory annotation) {
		return new DataGetter<>(annotation.keyFromSourceDate());
	}

	@Override
	protected <DATA> Integer createRunLevel(Class<DATA> cls, Field field, JoinInMemory annotation) {
		return annotation.runLevel();
	}


	class DataGetter<T, R> implements Function<T, R> {
		private final String expStr;
		private final Expression expression;
		private final EvaluationContext evaluationContext;


		DataGetter(String expStr) {
			this.expStr = expStr;
			this.expression = expressionParser.parseExpression(expStr, templateParserContext);
			StandardEvaluationContext evaluationContext1 = new StandardEvaluationContext();
			evaluationContext1.setBeanResolver(beanResolver);
			this.evaluationContext = evaluationContext1;
		}


		@Override
		public Object apply(Object data) {
			if (data == null) {
				return null;
			}
			Object value = expression.getValue(evaluationContext, data);
			return value;
		}

	}


	class DataSetter<T, R> implements BiConsumer<Object, List<Object>> {
		private final String fieldName;
		private final boolean isCollection;
		private final Expression expression;

		public DataSetter(String fieldName, boolean isCollection) {
			this.fieldName = fieldName;
			this.isCollection = isCollection;
			this.expression = expressionParser.parseExpression(fieldName);
		}

		@Override
		public void accept(Object data, List<Object> result) {
			if (isCollection) {
				this.expression.setValue(data, result);
			} else {
				int size = result.size();
				if (size == 1) {
					this.expression.setValue(data, result.get(0));
				} else {
					log.warn("write join result  error, field is {}, data is {}",

					  fieldName,
					  result);
				}
			}
		}
	}

}



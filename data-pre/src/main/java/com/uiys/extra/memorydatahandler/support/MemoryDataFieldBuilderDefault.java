package com.uiys.extra.memorydatahandler.support;

import cn.hutool.core.util.StrUtil;
import com.uiys.extra.memorydatahandler.MemoryDataField;
import com.uiys.extra.memorydatahandler.MemoryDataFieldBuilder;
import com.uiys.extra.memorydatahandler.annotation.MemoryDataHandler;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author uiys
 */
@Slf4j
public class MemoryDataFieldBuilderDefault<DATA> implements MemoryDataFieldBuilder<DATA> {

	private final TemplateParserContext templateParserContext;
	private final SpelExpressionParser expressionParser;
	private final StandardEvaluationContext standardEvaluationContext;


	public MemoryDataFieldBuilderDefault(BeanFactoryResolver beanFactoryResolver) {
		standardEvaluationContext = new StandardEvaluationContext();
		standardEvaluationContext.setBeanResolver(beanFactoryResolver);
		this.templateParserContext = new TemplateParserContext();
		this.expressionParser = new SpelExpressionParser();
	}


	@Override
	public MemoryDataField<DATA> build(Class<DATA> cls, Field field, MemoryDataHandler ann) {
		MemoryDataFieldDefault build = MemoryDataFieldDefault.builder()
		  .collectSourceKeyF(createSourceKey(cls, field, ann))
		  .loadJoinDataF(createLoader(cls, field, ann))
		  .convertJoinDataF(createConverter(cls, field, ann))
		  .collectJoinDataKeyF(createJoinDataKey(cls, field, ann))
		  .assembledJoinResultF(createAssemble(cls, field))
		  .notFoundJoinDataF(createNotFound(cls, field))
		  .build();
		return build;
	}


	private Function<Object, Object> createSourceKey(Class<DATA> cls, Field field, MemoryDataHandler ann) {
		log.info("构建 {} 类, 收集{}的 sourceKey 的Function函数!", cls.getSimpleName(), field.getName());
		return new GetterData<>(ann.sourceKey());
	}

	private Function<List<Object>, List<Object>> createLoader(Class<DATA> cls, Field field, MemoryDataHandler ann) {
		log.info("构建 {} 类, 收集{}的 loader Function 函数!", cls.getSimpleName(), field.getName());
		return new GetterData(ann.joinDataLoader());
	}

	private Function<Object, Object> createJoinDataKey(Class<DATA> cls, Field field, MemoryDataHandler ann) {
		log.info("构建 {} 类, 收集{}的 joinDataKey Function函数!", cls.getSimpleName(), field.getName());
		return new GetterData<>(ann.dataKey());
	}

	private Function<Object, Object> createConverter(Class<DATA> cls, Field field, MemoryDataHandler ann) {
		log.info("构建 {} 类, 字段{}的 converter Function 函数!", cls.getSimpleName(), field.getName());
		return StrUtil.isBlank(ann.joinDataConverter()) ? Function.identity() :
		  new GetterData<>(ann.joinDataConverter());
	}

	private BiConsumer<Object, List<Object>> createAssemble(Class<DATA> cls, Field field) {
		log.info("构建 {} 类, 字段{}的 assemble BiConsumer 函数!", cls.getSimpleName(), field.getName());
		return new SetterData(field);
	}

	private BiConsumer<Object, Object> createNotFound(Class<DATA> cls, Field field) {
		log.info("构建 {} 类, 字段{}的 notFound BiConsumer 函数!", cls.getSimpleName(), field.getName());
		return (o, o2) -> log.info("字段未采集到数据{}", field.getName());
	}

	class GetterData<T, R> implements Function<Object, Object> {

		private final Expression expression;

		public GetterData(String speL) {
			this.expression = expressionParser.parseExpression(speL, templateParserContext);
		}

		@Override
		public Object apply(Object data) {
			return expression.getValue(standardEvaluationContext, data);
		}
	}


	class SetterData implements BiConsumer<Object, List<Object>> {
		private final Field field;
		private final Expression expression;

		public SetterData(Field field) {
			this.field = field;
			this.expression = expressionParser.parseExpression(field.getName());
		}

		@Override
		public void accept(Object data, List<Object> joinDataResults) {
			if (Collection.class.isAssignableFrom(field.getType())) {
				expression.setValue(data, joinDataResults);
			} else {
				if (joinDataResults.size() == 1) {
					expression.setValue(data, joinDataResults.get(0));
				} else {
					String[] warns = {field.toString(), field.getName(), joinDataResults.toString()};
					log.warn("{},setterData 数据异常, filed {} 非集合,但是采集的数据是 {}", warns);
				}
			}
		}
	}


}



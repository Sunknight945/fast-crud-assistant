package com.uiys.extra.lazycatch.support;

import java.lang.reflect.Field;
import lombok.Getter;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author uiys
 */
public class LazyFieldLoader {

	@Getter
	private final Field field;
	private final StandardEvaluationContext evaluationContext;
	private final Expression expression;
	public LazyFieldLoader(Field field, BeanFactoryResolver beanResolver, String expressionStr) {
		this.field = field;
		ExpressionParser parser = new SpelExpressionParser();
		TemplateParserContext templateParserContext = new TemplateParserContext();
		this.expression = parser.parseExpression(expressionStr, templateParserContext);
		this.evaluationContext = new StandardEvaluationContext();
		this.evaluationContext.setBeanResolver(beanResolver);
	}


	public Object loadData(Object sourceData) {
		return this.expression.getValue(evaluationContext, sourceData);
	}
}



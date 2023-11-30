package com.uiys.extra.lazycatch.support;

import cn.hutool.core.util.StrUtil;
import com.uiys.extra.lazycatch.annotation.LazyCatch;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author uiys
 */
public class LazyFieldLoaderFactory {

	private final ApplicationContext applicationContext;

	public LazyFieldLoaderFactory(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public List<LazyFieldLoader> createFieldLoader(Class cls) {
		List<Field> allFieldsList = FieldUtils.getAllFieldsList(cls);
		return allFieldsList.stream()
		  .map(this::createField)
		  .filter(Objects::nonNull)
		  .collect(Collectors.toList());
	}


	private LazyFieldLoader createField(Field field) {
		LazyCatch lazyCatch = AnnotatedElementUtils.findMergedAnnotation(field, LazyCatch.class);
		if (Objects.isNull(lazyCatch) || StrUtil.isBlank(lazyCatch.loader())) {
			return null;
		}
		return new LazyFieldLoader(field, new BeanFactoryResolver(applicationContext), lazyCatch.loader());
	}

}

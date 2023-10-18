package com.uiys.jpa.fgs;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IncrementGenerator;
import org.springframework.data.annotation.Version;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class VersionGenerator extends IncrementGenerator {

	@Override
	public synchronized Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		Class<?> clazz = object.getClass()
		  .getSuperclass();
		Field[] fields = clazz.getFields();
		try {
			for (Field field : fields) {
				Version annotation = field.getAnnotation(Version.class);
				if (Objects.isNull(annotation)) {
					continue;
				}
				String getMethod = generateGetMethod(field.getName());
				Method declaredMethod = clazz.getDeclaredMethod(getMethod);
				Integer invoke = (Integer) declaredMethod.invoke(object);
				if (Objects.isNull(invoke)) {
					invoke = 0;
				} else {
					invoke = invoke + 1;
				}
				return invoke;
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return super.generate(session, object);
	}

	private String generateGetMethod(String filedName) {
		return "get" + filedName.substring(0, 1)
		  .toUpperCase() + filedName.substring(1);
	}
}



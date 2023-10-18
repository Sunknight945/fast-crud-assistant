package com.uiys.jpa.fgs;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import javax.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CustomerUUIDGenerator extends UUIDGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) {
		Class<?> clazz = object.getClass()
		  .getSuperclass();
		Field[] fields = clazz.getDeclaredFields();
		log.debug("id generate class:{}", clazz.getName());
		try {
			for (Field field : fields) {
				Id idAnnotation = field.getAnnotation(Id.class);
				if (Objects.isNull(idAnnotation)) {
					continue;
				}
				String filedName = field.getName();
				log.debug("filed name :{}", filedName);
				String getGeneratorName = generateGetMethod(filedName);
				log.debug("get method name :{}", getGeneratorName);
				Method declaredMethod = clazz.getDeclaredMethod(getGeneratorName);
				String idValue = (String) declaredMethod.invoke(object);
				if (StrUtil.isNotBlank(idValue)) {
					log.debug("use set value:{}", idValue);
					return idValue;
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return getUuid();
	}

	private String generateGetMethod(String filedName) {
		return "get" + filedName.substring(0, 1)
		  .toUpperCase() + filedName.substring(1);
	}

	private String getUuid() {
		return UUID.randomUUID(true)
		  .toString(true);
	}
}



package com.uiys.util;

import cn.hutool.core.util.StrUtil;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.Table;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author uiys
 */
public class TorE {


	private static final String FLAG = "+id+";

	@Getter
	public static final ConcurrentHashMap<Class<?>, String> MAP = new ConcurrentHashMap<>();

	public static String name(Class<?> clz, String id) {
		return getTableOrEntityName(clz, id);
	}

	public static String getTableOrEntityName(Class<?> clz, String id) {
		return MAP.computeIfAbsent(clz, str -> putAndGetName(clz)).replace(FLAG, id);
	}


	public static String putAndGetName(Class<?> clz) {
		String name;
		Table table = AnnotationUtils.findAnnotation(clz, Table.class);
		if (Objects.nonNull(table) && StrUtil.isNotBlank(table.name())) {
			name = "not found: select * from " + table.name() + " where id = '" + FLAG + "';";
		} else {
			name = "not found: data " + clz.getSimpleName() + ".id = '" + FLAG + "';";
		}
		return name;
	}


}



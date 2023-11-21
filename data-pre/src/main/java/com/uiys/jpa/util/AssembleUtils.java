package com.uiys.jpa.util;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

/**
 * @author uiys
 */
public class AssembleUtils {

	public static <S, C, Link> void assembleList(List<S> sources, List<C> contents, Function<C, Link> c, Function<S,
	  Link> s, BiConsumer<S, List<C>> biConsumer) {
		if (CollUtil.isNotEmpty(sources) && CollUtil.isNotEmpty(contents)) {
			Map<Link, List<C>> map = contents.stream()
			  .collect(Collectors.groupingBy(c));
			sources.forEach(item -> biConsumer.accept(item, map.getOrDefault(s.apply(item), Lists.newArrayList())));
		}
	}

	public static <S, C, Link> void assembleSingle(List<S> sources, List<C> contents, Function<C, Link> c, Function<S,
	  Link> s, BiConsumer<S, C> biConsumer) {
		if (CollUtil.isNotEmpty(sources) && CollUtil.isNotEmpty(contents)) {
			Map<Link, C> map = contents.stream()
			  .collect(Collectors.toMap(c, Function.identity(), (oldC, newC) -> newC));
			sources.forEach(item -> biConsumer.accept(item, map.getOrDefault(s.apply(item), null)));
		}
	}

	public static <E, R> List<R> content(Page<E> page, Function<E, R> function) {
		return page.getContent()
		  .stream()
		  .map(function)
		  .collect(Collectors.toList());
	}

}


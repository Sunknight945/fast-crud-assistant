package com.uiys.extra.filterpipeline.config;

import cn.hutool.core.lang.Assert;
import com.uiys.extra.filterpipeline.context.BusContext;
import com.uiys.extra.filterpipeline.filterselector.FilterSelector;
import com.uiys.extra.filterpipeline.filterselector.LocalBaseFilterSelector;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.valid.BusinessException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author uiys
 * @date 2023/11/16
 */

@Getter
@Configuration
@ConfigurationProperties(prefix = "filterpipeline.bizcode")
public class SelectorFactory {

	private final Map<String, List<String>> bizFilterMap = new HashMap<>();

	public FilterSelector filterSelector(BusContext busContext) {
		Assert.isTrue(bizFilterMap.containsKey(busContext.getBizCode()
		  .getName()), () -> new BusinessException(ErrorCode.NOTFOUND, "找不到相关的业务代码!"));
		LocalBaseFilterSelector filterSelector = new LocalBaseFilterSelector();
		List<String> list = bizFilterMap.get(busContext.getBizCode()
		  .getName());
		list.forEach(filterSelector::addFilterName);
		return filterSelector;
	}

}



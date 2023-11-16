package com.uiys.extra.filterpipeline.context;

import com.uiys.extra.filterpipeline.filterselector.FilterSelector;
import com.uiys.jpa.constant.BaseEnum;

/**
 * @author uiys
 * @date 2023/11/16
 */
public interface BusContext {

	BaseEnum getBizCode();

	FilterSelector getFilterSelector();

	Boolean continued();


}

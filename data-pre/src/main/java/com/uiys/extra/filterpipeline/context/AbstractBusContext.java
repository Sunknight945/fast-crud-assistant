package com.uiys.extra.filterpipeline.context;

import com.uiys.extra.filterpipeline.filterselector.FilterSelector;
import com.uiys.jpa.constant.BaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author uiys
 * @date 2023/11/16
 */
@Getter
public abstract class AbstractBusContext implements BusContext {

	@Setter
	BaseEnum bizCode;

	@Setter
	FilterSelector filterSelector;


}



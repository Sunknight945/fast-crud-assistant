package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataField;
import com.uiys.jpa.constant.ErrorCode;
import com.uiys.jpa.valid.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author uiys
 * 并行处理
 */
@Slf4j
public class ParallelMemoryDataType extends MemoryDataTypeAbstract {

	private final ExecutorService executorService;

	protected ParallelMemoryDataType(List<MemoryDataField> memoryDataFieldList, ExecutorService executorService) {
		super(memoryDataFieldList);
		this.executorService = executorService;
	}


	@Override
	public <DATA> void execute(List<DATA> dataList) {
		List<ExecutorTask<DATA>> executorTasks = new ArrayList<>();
		super.getMemoryDataFieldList()
		  .forEach(memoryDataField -> executorTasks.add(new ExecutorTask(memoryDataField, dataList)));
		try {
			executorService.invokeAll(executorTasks);
		} catch (InterruptedException e) {
			log.error("ParallelMemoryDataType执行类{}失败!", dataList.get(0)
			  .getClass()
			  .getSimpleName());
			throw new BusinessException(ErrorCode.ERROR_CODE, "ParallelMemoryDataType执行失败!" + dataList.get(0)
			  .getClass()
			  .getSimpleName());
		}
	}


	static class ExecutorTask<DATA> implements Callable<DATA> {

		private final MemoryDataField<DATA> memoryDataField;
		private final List<DATA> dataList;

		public ExecutorTask(MemoryDataField<DATA> memoryDataField, List<DATA> dataList) {
			this.memoryDataField = memoryDataField;
			this.dataList = dataList;
		}

		@Override
		public DATA call() {
			memoryDataField.execute(dataList);
			return null;
		}
	}


}



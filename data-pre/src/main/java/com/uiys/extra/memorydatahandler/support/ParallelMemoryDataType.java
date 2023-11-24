package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataField;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * @author uiys
 * 并行处理
 */
public class ParallelMemoryDataType extends MemoryDataTypeAbstract {

	private final ExecutorService executorService;

	protected ParallelMemoryDataType(List<MemoryDataField> memoryDataFieldList, ExecutorService executorService) {
		super(memoryDataFieldList);
		this.executorService = executorService;
	}


	@Override
	public <DATA> void execute(List<DATA> dataList) {
		List<ExecutorTask<DATA>> executorTasks = new ArrayList<>();
		for (MemoryDataField<DATA> ignored : super.getMemoryDataFieldList()) {
			executorTasks.add(new ExecutorTask<>(ignored, dataList));
		}
		try {
			executorService.invokeAll(executorTasks);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
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



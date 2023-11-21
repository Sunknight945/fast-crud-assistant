package com.uiys.extra.joininmemory.support;

import com.google.common.base.Stopwatch;
import com.uiys.extra.joininmemory.JoinItemExecutor;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author uiys
 */
@Slf4j
public class SerialJoinItemsExecutor<DATA> extends AbstractJoinItemsExecutor<DATA> {
	public SerialJoinItemsExecutor(Class<DATA> dataCls, List<JoinItemExecutor<DATA>> joinItemExecutors) {
		super(dataCls, joinItemExecutors);
	}


	@Override
	public void execute(List<DATA> datas) {
		getJoinItemExecutors().forEach(dataJoinItemExecutor -> {
			log.debug("run join on level {} use {}", dataJoinItemExecutor.runOnLevel(), dataJoinItemExecutor);
			if (log.isDebugEnabled()) {
				Stopwatch stopwatch = Stopwatch.createStarted();
				dataJoinItemExecutor.execute(datas);
				stopwatch.stop();
				log.debug("run executor cost {} ms, executor is {}", stopwatch.elapsed(TimeUnit.MILLISECONDS),
				  dataJoinItemExecutor);
			} else {
				dataJoinItemExecutor.execute(datas);
			}
		});
	}
}



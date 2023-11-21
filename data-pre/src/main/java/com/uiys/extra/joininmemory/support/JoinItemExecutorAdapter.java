package com.uiys.extra.joininmemory.support;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author uiys
 * @date 2023/11/21
 */
@Slf4j
@Builder
@Getter
public class JoinItemExecutorAdapter<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> extends AbstractJoinItemExecutor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> {


	private final String name;
	private final Integer runLevel;

	private final Function<SOURCE_DATA, JOIN_KEY> getKeyFromSourceData;
	private final Function<List<JOIN_KEY>, List<JOIN_DATA>> loadJoinDatasFromJoinKeys;
	private final Function<JOIN_DATA, JOIN_KEY> getKeyFromJoinData;
	private final Function<JOIN_DATA, JOIN_RESULT> convertJoinToResult;

	private final BiConsumer<SOURCE_DATA, List<JOIN_RESULT>> onFound;
	private final BiConsumer<SOURCE_DATA, JOIN_KEY> onNotFound;


	public JoinItemExecutorAdapter(String name, Integer runLevel, Function<SOURCE_DATA, JOIN_KEY> getKeyFromSourceData
	  , Function<List<JOIN_KEY>, List<JOIN_DATA>> loadJoinDatasFromJoinKeys,
	                               Function<JOIN_DATA, JOIN_KEY> getKeyFromJoinData,
	                               Function<JOIN_DATA, JOIN_RESULT> convertJoinToResult, BiConsumer<SOURCE_DATA,
	  List<JOIN_RESULT>> onFound, BiConsumer<SOURCE_DATA, JOIN_KEY> onNotFound) {
		this.name = name;

		this.getKeyFromSourceData = getKeyFromSourceData;
		this.loadJoinDatasFromJoinKeys = loadJoinDatasFromJoinKeys;

		this.getKeyFromJoinData = getKeyFromJoinData;
		this.convertJoinToResult = convertJoinToResult;
		this.onFound = onFound;
		if (onNotFound != null) {
			this.onNotFound = getDefaultLostBiConsumer().andThen(onFound);
		} else {
			this.onNotFound = getDefaultLostBiConsumer();
		}

		if (runLevel == null) {
			this.runLevel = 0;
		} else {
			this.runLevel = runLevel;
		}

	}

	private BiConsumer getDefaultLostBiConsumer() {
		return (data, joinKey) -> {
			log.warn("failed to find join data by {} for {}", joinKey, data);
		};
	}

	@Override
	protected JOIN_KEY getJoinKeyFromSourceData(SOURCE_DATA data) {
		return getKeyFromSourceData.apply(data);
	}

	@Override
	protected List<JOIN_DATA> getJoinDataByJoinKeys(List<JOIN_KEY> joinKeys) {
		return loadJoinDatasFromJoinKeys.apply(joinKeys);
	}

	@Override
	protected JOIN_KEY getJoinKeyFromJoinData(JOIN_DATA joinData) {
		return getKeyFromJoinData.apply(joinData);
	}

	@Override
	protected JOIN_RESULT convertResult(JOIN_DATA joinData) {
		return convertJoinToResult.apply(joinData);
	}

	@Override
	protected void onFound(SOURCE_DATA data, List<JOIN_RESULT> joinResults) {
		onFound.accept(data, joinResults);
	}

	@Override
	protected void onNotFound(SOURCE_DATA data, JOIN_KEY joinKey) {
		onNotFound.accept(data, joinKey);
	}

	@Override
	public Integer runOnLevel() {
		return this.runLevel;
	}

	@Override
	public String toString() {
		return "JoinExecutorAdapter-for-" + name;
	}
}



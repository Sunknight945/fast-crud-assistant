package com.uiys.extra.joininmemory.support;

import cn.hutool.core.collection.CollectionUtil;
import com.uiys.extra.joininmemory.JoinItemExecutor;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author uiys
 */
@Slf4j
public abstract class AbstractJoinItemExecutor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> implements JoinItemExecutor<SOURCE_DATA> {
	protected abstract JOIN_KEY getJoinKeyFromSourceData(SOURCE_DATA data);

	protected abstract List<JOIN_DATA> getJoinDataByJoinKeys(List<JOIN_KEY> joinKeys);

	protected abstract JOIN_KEY getJoinKeyFromJoinData(JOIN_DATA joinData);


	protected abstract JOIN_RESULT convertResult(JOIN_DATA joinData);


	protected abstract void onFound(SOURCE_DATA data, List<JOIN_RESULT> joinResults);

	protected abstract void onNotFound(SOURCE_DATA data, JOIN_KEY joinKey);


	@Override
	public void execute(List<SOURCE_DATA> sourceData) {
		List<JOIN_KEY> joinKeys = sourceData.stream()
		  .filter(Objects::nonNull)
		  .map(this::getJoinKeyFromSourceData)
		  .filter(Objects::nonNull)
		  .collect(Collectors.toList());

		List<JOIN_DATA> joinDataByJoinKeys = getJoinDataByJoinKeys(joinKeys);

		Map<JOIN_KEY, List<JOIN_DATA>> joinKeyListMap = joinDataByJoinKeys.stream()
		  .filter(Objects::nonNull)
		  .collect(Collectors.groupingBy(this::getJoinKeyFromJoinData));

		for (SOURCE_DATA sourceDatum : sourceData) {
			JOIN_KEY joinKeyFromSourceData = getJoinKeyFromSourceData(sourceDatum);
			if (joinKeyListMap.containsKey(joinKeyFromSourceData) && CollectionUtil.isNotEmpty(joinKeyListMap.get(joinKeyFromSourceData))) {
				onFound(sourceDatum, joinKeyListMap.get(joinKeyFromSourceData)
				  .stream()
				  .filter(Objects::nonNull)
				  .map(this::convertResult)
				  .collect(Collectors.toList()));
			} else {
				onNotFound(sourceDatum, joinKeyFromSourceData);
			}
		}
	}
}



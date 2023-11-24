package com.uiys.extra.memorydatahandler.support;

import com.uiys.extra.memorydatahandler.MemoryDataField;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author uiys
 */
@Builder
@Slf4j
public class MemoryDataFieldDefault<DATA, SOURCE_KEY, JOIN_DATA, JOIN_RESULT> implements MemoryDataField<DATA> {

	private Function<DATA, SOURCE_KEY> collectSourceKeyF;
	private Function<List<SOURCE_KEY>, List<JOIN_DATA>> loadJoinDataF;
	private Function<JOIN_DATA, SOURCE_KEY> collectJoinDataKeyF;
	private Function<JOIN_DATA, JOIN_RESULT> convertJoinDataF;
	private BiConsumer<DATA, List<JOIN_RESULT>> assembledJoinResultF;
	private BiConsumer<DATA, SOURCE_KEY> notFoundJoinDataF;


	@Override
	public void execute(List<DATA> dataList) {
		List<SOURCE_KEY> sourceKeys = dataList.stream()
		  .filter(Objects::nonNull)
		  .map(collectSourceKeyF)
		  .collect(Collectors.toList());
		List<JOIN_DATA> joinDataList = loadJoinDataF.apply(sourceKeys);
		Map<SOURCE_KEY, List<JOIN_DATA>> joinDataListMap = joinDataList.stream()
		  .filter(Objects::nonNull)
		  .collect(Collectors.groupingBy(collectJoinDataKeyF));
		for (DATA data : dataList) {
			SOURCE_KEY sourceKey = collectSourceKeyF.apply(data);
			if (joinDataListMap.containsKey(sourceKey)) {
				List<JOIN_RESULT> joinResults = joinDataListMap.get(sourceKey)
				  .stream()
				  .map(convertJoinDataF)
				  .collect(Collectors.toList());
				assembledJoinResultF.accept(data, joinResults);
			} else {
				notFoundJoinDataF.accept(data, sourceKey);
				String[] strings = {data.toString(), sourceKey.toString()};
				log.warn("未采集到{}, 字段值{}的数据!", strings );
			}
		}

	}


}



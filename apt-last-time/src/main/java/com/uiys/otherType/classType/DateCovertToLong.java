package com.uiys.otherType.classType;//package com.uiys.otherType.classType;
//
//import java.time.Instant;
//import javax.persistence.AttributeConverter;
//
///**
// * @author uiys
// * @date 2023/10/13
// */
//public class DateCovertToLong implements AttributeConverter<Instant, Long> {
//	@Override
//	public Long convertToDatabaseColumn(Instant instant) {
//		return instant.toEpochMilli();
//	}
//
//	@Override
//	public Instant convertToEntityAttribute(Long aLong) {
//		return Instant.ofEpochMilli(aLong);
//	}
//
//}
//
//

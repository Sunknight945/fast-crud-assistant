package com.uiys.jpa.convert;

import java.time.Instant;
import javax.persistence.AttributeConverter;


public class DateLong2Instant implements AttributeConverter<Instant, Long> {

	@Override
	public Long convertToDatabaseColumn(Instant instant) {
		if (instant == null) {
			return null;
		}
		return instant.toEpochMilli();
	}

	@Override
	public Instant convertToEntityAttribute(Long l) {
		if (l == null) {
			return null;
		}
		return Instant.ofEpochMilli(l);
	}
}



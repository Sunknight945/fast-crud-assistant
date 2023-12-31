package com.uiys.jpa.convert;

import java.time.Instant;
import java.util.Date;
import javax.persistence.AttributeConverter;

public class Instant2Date implements AttributeConverter<Instant, Date> {

//	private static final String formate = "YYYY-MM-dd HH:mm:ss";

	@Override
	public Date convertToDatabaseColumn(Instant attribute) {
		return Date.from(attribute);
	}


	@Override
	public Instant convertToEntityAttribute(Date dbData) {
		return dbData.toInstant();
	}
}


package com.uiys.jpa.convert;


import com.uiys.jpa.constant.ValidStatus;
import java.util.Optional;
import javax.persistence.AttributeConverter;


public class ValidStatusConverter implements AttributeConverter<ValidStatus, Integer> {
	@Override
	public Integer convertToDatabaseColumn(ValidStatus attribute) {
		return attribute.getCode();
	}

	@Override
	public ValidStatus convertToEntityAttribute(Integer dbData) {
		Optional<ValidStatus> validStatus = ValidStatus.of(dbData);
		return validStatus.orElse(null);
	}
}



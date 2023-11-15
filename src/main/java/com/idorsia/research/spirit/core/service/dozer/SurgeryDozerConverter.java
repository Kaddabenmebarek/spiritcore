package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Surgery;

@Service
public class SurgeryDozerConverter extends DozerConverter<Surgery, String>{
	
	public SurgeryDozerConverter() {
		super(Surgery.class, String.class);
	}

	@Override
	public String convertTo(Surgery source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public Surgery convertFrom(String source, Surgery destination) {
		if(source != null ) {
			return Surgery.valueOf(source.toUpperCase());
		}
		return null;
	}
}

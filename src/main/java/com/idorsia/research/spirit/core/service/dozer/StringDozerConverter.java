package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

@Service
public class StringDozerConverter extends DozerConverter<String, String>{

	public StringDozerConverter() {
		super(String.class, String.class);
	}

	@Override
	public String convertTo(String source, String destination) {
		return source == null ? "" : source;
	}

	@Override
	public String convertFrom(String source, String destination) {
		return source == null ? "" : source;
	}
}

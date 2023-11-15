package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.OutputType;

@Service
public class OutputTypeDozerConverter extends DozerConverter<OutputType, String>{
	
	public OutputTypeDozerConverter() {
		super(OutputType.class, String.class);
	}

	@Override
	public String convertTo(OutputType source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public OutputType convertFrom(String source, OutputType destination) {
		if(source != null ) {
			return OutputType.valueOf(source);
		}
		return null;
	}
}

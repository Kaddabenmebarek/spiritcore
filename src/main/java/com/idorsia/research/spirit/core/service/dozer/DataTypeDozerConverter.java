package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.DataType;

@Service
public class DataTypeDozerConverter extends DozerConverter<DataType, String>{
	
	public DataTypeDozerConverter() {
		super(DataType.class, String.class);
	}

	@Override
	public String convertTo(DataType source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public DataType convertFrom(String source, DataType destination) {
		if(source != null ) {
			return DataType.valueOf(source);
		}
		return null;
	}
}

package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.BatchType;


@Service
public class BatchTypeDozerConverter extends DozerConverter<BatchType, String>{
	
	public BatchTypeDozerConverter() {
		super(BatchType.class, String.class);
	}

	@Override
	public String convertTo(BatchType source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public BatchType convertFrom(String source, BatchType destination) {
		if(source != null ) {
			return BatchType.valueOf(source);
		}
		return null;
	}
}

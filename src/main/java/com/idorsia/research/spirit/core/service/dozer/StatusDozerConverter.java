package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Status;

@Service
public class StatusDozerConverter extends DozerConverter<Status, String>{
	
	public StatusDozerConverter() {
		super(Status.class, String.class);
	}

	@Override
	public String convertTo(Status source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public Status convertFrom(String source, Status destination) {
		if(source != null ) {
			return Status.valueOf(source);
		}
		return null;
	}
}

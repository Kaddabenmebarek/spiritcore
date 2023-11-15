package com.idorsia.research.spirit.core.service.dozer;

import java.sql.Timestamp;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.dto.view.Execution;

@Service
public class ExecutionDozerConverter extends DozerConverter<Execution, Timestamp>{
	
	public ExecutionDozerConverter() {
		super(Execution.class, Timestamp.class);
	}

	@Override
	public Timestamp convertTo(Execution source, Timestamp destination) {
		if(source != null) {
			return new Timestamp(source.getExecutionDate().getTime());
		}
		return null;
	}

	@Override
	public Execution convertFrom(Timestamp source, Execution destination) {
		if(source != null) {
			return new Execution(source);
		}
		return null;
	}
}

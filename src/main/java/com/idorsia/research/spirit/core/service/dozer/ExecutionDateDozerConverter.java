package com.idorsia.research.spirit.core.service.dozer;


import java.util.Date;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.dto.view.Execution;

@Service
public class ExecutionDateDozerConverter extends DozerConverter<Execution, Date>{
	
	public ExecutionDateDozerConverter() {
		super(Execution.class, Date.class);
	}

	@Override
	public Date convertTo(Execution source, Date destination) {
		if(source != null && source.getExecutionDate()!=null) {
			return new Date(source.getExecutionDate().getTime());
		}
		return null;
	}

	@Override
	public Execution convertFrom(Date source, Execution destination) {
		if(source != null) {
			return new Execution(source);
		}
		return null;
	}
}

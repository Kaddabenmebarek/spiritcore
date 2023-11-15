package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.model.PlannedSample;
import com.idorsia.research.spirit.core.service.PlannedSampleService;

@Service
public class PlannedSampleDozerConverter extends DozerConverter<PlannedSample, Integer>{
	
	@Autowired
	private PlannedSampleService plannedSampleService = (PlannedSampleService) ContextShare.getContext().getBean("plannedSampleService");
	
	public PlannedSampleDozerConverter() {
		super(PlannedSample.class, Integer.class);
	}

	@Override
	public Integer convertTo(PlannedSample source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public PlannedSample convertFrom(Integer source, PlannedSample destination) {
		if(source != null) {
			return plannedSampleService.get(source);
		}
		return null;
	}
}

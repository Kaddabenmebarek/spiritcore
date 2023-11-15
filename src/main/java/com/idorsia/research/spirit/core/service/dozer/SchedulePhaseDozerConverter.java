package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.SchedulePhaseDto;
import com.idorsia.research.spirit.core.service.SchedulePhaseService;

public class SchedulePhaseDozerConverter extends DozerConverter<SchedulePhaseDto, Integer>{
	private SchedulePhaseService schedulePhaseService = (SchedulePhaseService) ContextShare.getContext().getBean("schedulePhaseService");
	
	public SchedulePhaseDozerConverter() {
		super(SchedulePhaseDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(SchedulePhaseDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public SchedulePhaseDto convertFrom(Integer source, SchedulePhaseDto destination) {
		if(source != null) {
			return schedulePhaseService.getScheduleTypeDto(source);
		}
		return null;
	}
}

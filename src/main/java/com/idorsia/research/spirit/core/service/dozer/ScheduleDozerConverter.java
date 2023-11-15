package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.ScheduleDto;
import com.idorsia.research.spirit.core.service.ScheduleService;

@Service
public class ScheduleDozerConverter extends DozerConverter<ScheduleDto, Integer>{
	
	@Autowired
	private ScheduleService scheduleService = (ScheduleService) ContextShare.getContext().getBean("scheduleService");
	
	public ScheduleDozerConverter() {
		super(ScheduleDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(ScheduleDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public ScheduleDto convertFrom(Integer source, ScheduleDto destination) {
		if(source != null) {
			return scheduleService.getScheduleDto(source);
		}
		return null;
	}
}

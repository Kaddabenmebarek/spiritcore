package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.StageDto;
import com.idorsia.research.spirit.core.service.StageService;

@Service
public class StageDozerConverter extends DozerConverter<StageDto, Integer>{
	
	@Autowired
	private StageService stageService = (StageService) ContextShare.getContext().getBean("stageService");
	
	public StageDozerConverter() {
		super(StageDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(StageDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public StageDto convertFrom(Integer source, StageDto destination) {
		if(source != null) {
			return stageService.getStageDto(source);
		}
		return null;
	}

}

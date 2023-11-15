package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.ActionPatternsDto;
import com.idorsia.research.spirit.core.service.ActionPatternsService;

@Service
public class ActionPatternsDozerConverter extends DozerConverter<ActionPatternsDto, Integer>{
	
	@Autowired
	private ActionPatternsService actionPatternsService = (ActionPatternsService) ContextShare.getContext().getBean("actionPatternsService");
	
	public ActionPatternsDozerConverter() {
		super(ActionPatternsDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(ActionPatternsDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public ActionPatternsDto convertFrom(Integer source, ActionPatternsDto destination) {
		if(source != null) {
			return actionPatternsService.getActionPatternsDto(source);
		}
		return null;
	}
}

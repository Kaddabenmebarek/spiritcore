package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.PhaseDto;
import com.idorsia.research.spirit.core.service.PhaseService;

@Service
public class PhaseDozerConverter extends DozerConverter<PhaseDto, Integer>{
	
	@Autowired
	private PhaseService phaseService = (PhaseService) ContextShare.getContext().getBean("phaseService");;
	
	public PhaseDozerConverter() {
		super(PhaseDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(PhaseDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public PhaseDto convertFrom(Integer source, PhaseDto destination) {
		if(source != null && source!=Constants.NEWTRANSIENTID) {
			return phaseService.getPhaseDto(source);
		}
		return null;
	}
}

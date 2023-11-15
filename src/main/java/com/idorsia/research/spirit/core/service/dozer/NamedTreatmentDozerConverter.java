package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.NamedTreatmentDto;
import com.idorsia.research.spirit.core.service.NamedTreatmentService;

@Service
public class NamedTreatmentDozerConverter extends DozerConverter<NamedTreatmentDto, Integer>{
	
	@Autowired
	private NamedTreatmentService namedTreatmentService = (NamedTreatmentService) ContextShare.getContext().getBean("namedTreatmentService");
	
	public NamedTreatmentDozerConverter() {
		super(NamedTreatmentDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(NamedTreatmentDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public NamedTreatmentDto convertFrom(Integer source, NamedTreatmentDto destination) {
		if(source != null) {
			return namedTreatmentService.getNamedTreatmentDto(source);
		}
		return null;
	}
}

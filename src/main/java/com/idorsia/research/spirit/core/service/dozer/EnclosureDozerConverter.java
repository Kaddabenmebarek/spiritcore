package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.EnclosureDto;
import com.idorsia.research.spirit.core.service.EnclosureService;

@Service
public class EnclosureDozerConverter extends DozerConverter<EnclosureDto, Integer>{
	
	@Autowired
	private EnclosureService enclosureService = (EnclosureService) ContextShare.getContext().getBean("enclosureService");
	
	public EnclosureDozerConverter() {
		super(EnclosureDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(EnclosureDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public EnclosureDto convertFrom(Integer source, EnclosureDto destination) {
		if(source != null) {
			return enclosureService.getEnclosureDto(source);
		}
		return null;
	}
}

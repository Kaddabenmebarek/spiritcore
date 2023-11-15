package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.BiotypeDto;
import com.idorsia.research.spirit.core.service.BiotypeService;

@Service
public class BiotypeDozerConverter extends DozerConverter<BiotypeDto, Integer>{
	
	@Autowired
	private BiotypeService biotypeService = (BiotypeService) ContextShare.getContext().getBean("biotypeService");
	
	public BiotypeDozerConverter() {
		super(BiotypeDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(BiotypeDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public BiotypeDto convertFrom(Integer source, BiotypeDto destination) {
		if(source != null) {
			return biotypeService.getBiotypeDto(source);
		}
		return null;
	}
}

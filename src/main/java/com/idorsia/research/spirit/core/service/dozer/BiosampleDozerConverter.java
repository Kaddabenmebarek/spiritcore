package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.BiosampleDto;
import com.idorsia.research.spirit.core.service.BiosampleService;

@Service
public class BiosampleDozerConverter extends DozerConverter<BiosampleDto, Integer>{
	
	@Autowired
	private BiosampleService biosampleService = (BiosampleService) ContextShare.getContext().getBean("biosampleService");

	
	public BiosampleDozerConverter() {
		super(BiosampleDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(BiosampleDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public BiosampleDto convertFrom(Integer source, BiosampleDto destination) {
		if(source != null && source!=Constants.NEWTRANSIENTID) {
			return biosampleService.getBiosampleDto(source);
		}
		return null;
	}
}

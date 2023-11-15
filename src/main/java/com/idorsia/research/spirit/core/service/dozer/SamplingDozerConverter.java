package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.SamplingDto;
import com.idorsia.research.spirit.core.service.SamplingService;

@Service
public class SamplingDozerConverter extends DozerConverter<SamplingDto, Integer>{
	
	@Autowired
	private SamplingService samplingService = (SamplingService) ContextShare.getContext().getBean("samplingService");
	
	public SamplingDozerConverter() {
		super(SamplingDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(SamplingDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public SamplingDto convertFrom(Integer source, SamplingDto destination) {
		if(source != null && source!=Constants.NEWTRANSIENTID) {
			return samplingService.getSamplingDto(source);
		}
		return null;
	}
	
}

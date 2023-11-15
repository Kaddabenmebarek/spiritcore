package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.NamedSamplingDto;
import com.idorsia.research.spirit.core.service.NamedSamplingService;

@Service
public class NamedSamplingDozerConverter extends DozerConverter<NamedSamplingDto, Integer>{
	
	@Autowired
	private NamedSamplingService namedSamplingService=(NamedSamplingService) ContextShare.getContext().getBean("namedSamplingService");
	
	public NamedSamplingDozerConverter() {
		super(NamedSamplingDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(NamedSamplingDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public NamedSamplingDto convertFrom(Integer source, NamedSamplingDto destination) {
		if(source != null) {
			return namedSamplingService.getNamedSamplingDto(source);
		}
		return null;
	}
}

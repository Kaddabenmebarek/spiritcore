package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.AssayResultDto;
import com.idorsia.research.spirit.core.service.AssayResultService;

@Service
public class AssayResultDozerConverter extends DozerConverter<AssayResultDto, Integer>{
	
	private AssayResultService assayResultService=(AssayResultService) ContextShare.getContext().getBean("assayResultService");
	
	public AssayResultDozerConverter() {
		super(AssayResultDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(AssayResultDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public AssayResultDto convertFrom(Integer source, AssayResultDto destination) {
		if(source != null) {
			return assayResultService.getAssayResultDto(source);
		}
		return null;
	}

}

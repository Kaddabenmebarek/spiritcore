package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.AssayDto;
import com.idorsia.research.spirit.core.service.AssayService;

@Service
public class AssayDozerConverter extends DozerConverter<AssayDto, Integer>{
	
	private AssayService assayService=(AssayService) ContextShare.getContext().getBean("assayService");
	
	public AssayDozerConverter() {
		super(AssayDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(AssayDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public AssayDto convertFrom(Integer source, AssayDto destination) {
		if(source != null) {
			return assayService.getAssayDto(source);
		}
		return null;
	}
}

package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.AssayAttributeDto;
import com.idorsia.research.spirit.core.service.AssayAttributeService;

@Service
public class AssayAttributeDozerConverter extends DozerConverter<AssayAttributeDto, Integer>{
	
	@Autowired
	private AssayAttributeService assayAttributeService=(AssayAttributeService) ContextShare.getContext().getBean("assayAttributeService");
	
	public AssayAttributeDozerConverter() {
		super(AssayAttributeDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(AssayAttributeDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public AssayAttributeDto convertFrom(Integer source, AssayAttributeDto destination) {
		if(source != null) {
			return assayAttributeService.getAssayAttributeDto(source);
		}
		return null;
	}
}

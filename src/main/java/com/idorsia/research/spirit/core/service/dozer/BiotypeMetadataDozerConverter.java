package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataDto;
import com.idorsia.research.spirit.core.service.BiotypeMetadataService;

@Service
public class BiotypeMetadataDozerConverter extends DozerConverter<BiotypeMetadataDto, Integer>{
	
	@Autowired
	private BiotypeMetadataService biotypeMetadataService = (BiotypeMetadataService) ContextShare.getContext().getBean("biotypeMetadataService");
	
	public BiotypeMetadataDozerConverter() {
		super(BiotypeMetadataDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(BiotypeMetadataDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public BiotypeMetadataDto convertFrom(Integer source, BiotypeMetadataDto destination) {
		if(source != null) {
			return biotypeMetadataService.map(biotypeMetadataService.get(source));
		}
		return null;
	}
}

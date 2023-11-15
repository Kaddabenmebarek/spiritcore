package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.BiotypeMetadataValueDto;
import com.idorsia.research.spirit.core.service.BiotypeMetadataValueService;

@Service
public class BiotypeMetadataValueDozerConverter extends DozerConverter<BiotypeMetadataValueDto, Integer>{
	
	@Autowired
	private BiotypeMetadataValueService biotypeMetadataValueService = (BiotypeMetadataValueService) ContextShare.getContext().getBean("biotypeMetadataValueService");
	
	public BiotypeMetadataValueDozerConverter() {
		super(BiotypeMetadataValueDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(BiotypeMetadataValueDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public BiotypeMetadataValueDto convertFrom(Integer source, BiotypeMetadataValueDto destination) {
		if(source != null) {
			return biotypeMetadataValueService.getBiotypeMetadataValueDto(source);
		}
		return null;
	}

}

package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.ContainerType;

@Service
public class ContainerTypeDozerConverter extends DozerConverter<ContainerType, String>{
	
	public ContainerTypeDozerConverter() {
		super(ContainerType.class, String.class);
	}

	@Override
	public String convertTo(ContainerType source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public ContainerType convertFrom(String source, ContainerType destination) {
		if(source != null) {
			return ContainerType.valueOf(source);
		}
		return null;
	}
}

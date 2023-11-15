package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.BiotypeCategory;

@Service
public class BiotypeCategoryDozerConverter extends DozerConverter<BiotypeCategory, String>{
	
	public BiotypeCategoryDozerConverter() {
		super(BiotypeCategory.class, String.class);
	}

	@Override
	public String convertTo(BiotypeCategory source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public BiotypeCategory convertFrom(String source, BiotypeCategory destination) {
		if(source != null ) {
			return BiotypeCategory.valueOf(source);
		}
		return null;
	}
}

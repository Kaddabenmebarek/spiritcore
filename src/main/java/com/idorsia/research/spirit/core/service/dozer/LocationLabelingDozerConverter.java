package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.LocationLabeling;

@Service
public class LocationLabelingDozerConverter extends DozerConverter<LocationLabeling, String>{
	
	public LocationLabelingDozerConverter() {
		super(LocationLabeling.class, String.class);
	}

	@Override
	public String convertTo(LocationLabeling source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public LocationLabeling convertFrom(String source, LocationLabeling destination) {
		if(source != null ) {
			return LocationLabeling.valueOf(source);
		}
		return null;
	}
}

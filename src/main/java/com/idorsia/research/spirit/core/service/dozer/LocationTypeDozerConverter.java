package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.LocationType;

@Service
public class LocationTypeDozerConverter extends DozerConverter<LocationType, String>{
	
	public LocationTypeDozerConverter() {
		super(LocationType.class, String.class);
	}

	@Override
	public String convertTo(LocationType source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public LocationType convertFrom(String source, LocationType destination) {
		if(source != null ) {
			return LocationType.valueOf(source);
		}
		return null;
	}
}

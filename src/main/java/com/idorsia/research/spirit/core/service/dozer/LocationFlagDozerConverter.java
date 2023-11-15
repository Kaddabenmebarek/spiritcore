package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.LocationFlag;

@Service
public class LocationFlagDozerConverter extends DozerConverter<LocationFlag, String>{
	
	public LocationFlagDozerConverter() {
		super(LocationFlag.class, String.class);
	}

	@Override
	public String convertTo(LocationFlag source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public LocationFlag convertFrom(String source, LocationFlag destination) {
		if(source != null ) {
			return LocationFlag.valueOf(source);
		}
		return null;
	}
}

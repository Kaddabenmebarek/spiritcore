package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.LocationDto;
import com.idorsia.research.spirit.core.service.LocationService;

@Service
public class LocationDozerConverter extends DozerConverter<LocationDto, Integer>{
	
	@Autowired
	private LocationService locationService=(LocationService) ContextShare.getContext().getBean("locationService");
	
	public LocationDozerConverter() {
		super(LocationDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(LocationDto source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public LocationDto convertFrom(Integer source, LocationDto destination) {
		if(source != null) {
			return locationService.getLocationDto(source);
		}
		return null;
	}
}

package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Quality;

@Service
public class QualityDozerConverter extends DozerConverter<Quality, Integer>{
	
	public QualityDozerConverter() {
		super(Quality.class, Integer.class);
	}

	@Override
	public Integer convertTo(Quality source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public Quality convertFrom(Integer source, Quality destination) {
		if(source != null) {
			return Quality.getById(source);
		}
		return null;
	}
}

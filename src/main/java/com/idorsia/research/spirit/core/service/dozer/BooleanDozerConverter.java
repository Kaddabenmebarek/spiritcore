package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.util.DataUtils;

@Service
public class BooleanDozerConverter extends DozerConverter<Boolean, Integer>{
	
	public BooleanDozerConverter() {
		super(Boolean.class, Integer.class);
	}

	@Override
	public Integer convertTo(Boolean source, Integer destination) {
		return source == null ? null : DataUtils.booleanToInt(source);
	}

	@Override
	public Boolean convertFrom(Integer source, Boolean destination) {
		return source == null ? null : DataUtils.intToBolean(source);
	}

}

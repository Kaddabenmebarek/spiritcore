package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.AmountUnit;

@Service
public class AmountUnitDozerConverter extends DozerConverter<AmountUnit, String>{
	
	public AmountUnitDozerConverter() {
		super(AmountUnit.class, String.class);
	}

	@Override
	public String convertTo(AmountUnit source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public AmountUnit convertFrom(String source, AmountUnit destination) {
		if(source != null ) {
			return AmountUnit.valueOf(source);
		}
		return null;
	}
}

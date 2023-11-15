package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.model.Property;
import com.idorsia.research.spirit.core.service.PropertyService;

@Service
public class PropertyDozerConverter extends DozerConverter<Property, Integer>{
	
	@Autowired
	private PropertyService propertyService = (PropertyService) ContextShare.getContext().getBean("propertyService");
	
	public PropertyDozerConverter() {
		super(Property.class, Integer.class);
	}

	@Override
	public Integer convertTo(Property source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public Property convertFrom(Integer source, Property destination) {
		if(source != null && source!=Constants.NEWTRANSIENTID) {
			return propertyService.get(source);
		}
		return null;
	}

}

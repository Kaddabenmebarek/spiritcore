package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.model.PropertyLink;
import com.idorsia.research.spirit.core.service.PropertyLinkService;

@Service
public class PropertyLinkDozerConverter extends DozerConverter<PropertyLink, Integer>{
	
	@Autowired
	private PropertyLinkService propertyLinkService = (PropertyLinkService) ContextShare.getContext().getBean("propertyLinkService");
	
	public PropertyLinkDozerConverter() {
		super(PropertyLink.class, Integer.class);
	}

	@Override
	public Integer convertTo(PropertyLink source, Integer destination) {
		if(source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public PropertyLink convertFrom(Integer source, PropertyLink destination) {
		if(source != null) {
			return propertyLinkService.get(source);
		}
		return null;
	}

}

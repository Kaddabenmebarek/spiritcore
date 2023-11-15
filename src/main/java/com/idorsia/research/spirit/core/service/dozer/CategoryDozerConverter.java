package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.constants.Category;

@Service
public class CategoryDozerConverter extends DozerConverter<Category, String>{
	
	public CategoryDozerConverter() {
		super(Category.class, String.class);
	}

	@Override
	public String convertTo(Category source, String destination) {
		if(source != null) {
			return source.name();
		}
		return null;
	}

	@Override
	public Category convertFrom(String source, Category destination) {
		if(source != null ) {
			return Category.valueOf(source);
		}
		return null;
	}
}

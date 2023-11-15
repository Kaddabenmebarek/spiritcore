package com.idorsia.research.spirit.core.service.dozer;

import java.awt.Color;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

@Service
public class ColorDozerConverter extends DozerConverter<Color, Integer> {

	public ColorDozerConverter() {
		super(Color.class, Integer.class);
	}

	@Override
	public Integer convertTo(Color source, Integer destination) {
		if (source != null) {
			return source.getRGB();
		}
		return null;
	}

	@Override
	public Color convertFrom(Integer source, Color destination) {
		if(source == null) {
			return Color.BLACK;
		}
		return new Color(source);
	}
}

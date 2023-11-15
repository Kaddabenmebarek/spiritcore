package com.idorsia.research.spirit.core.service.dozer;

import java.io.Serializable;
import java.time.Duration;

import org.dozer.DozerBeanMapper;
import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DurationDozerConverter extends DozerConverter<Duration, Long> implements Serializable {

	private static final long serialVersionUID = 8208265912403399747L;

	public DurationDozerConverter() {
		super(Duration.class, Long.class);
	}

	@Qualifier("org.dozer.Mapper")
	protected DozerBeanMapper dozerBeanMapper;
	
	@Override
	public Long convertTo(Duration source, Long destination) {
		if(source != null) {
			return source.toNanos();
		}
		return null;
	}

	@Override
	public Duration convertFrom(Long source, Duration destination) {
		if(source != null) {
			return Duration.ofNanos(source);
		}
		return null;
	}
}

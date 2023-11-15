package com.idorsia.research.spirit.core.service.dozer;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.dozer.DozerConverter;
import org.springframework.stereotype.Service;

@Service
public class ZonedDateTimeDozerConverter extends DozerConverter<ZonedDateTime, Date> {

	public ZonedDateTimeDozerConverter() {
		super(ZonedDateTime.class, Date.class);
	}

	@Override
	public Date convertTo(ZonedDateTime source, Date destination) {
		if(source != null) {
			return Date.from(source.toInstant());
		}
		return null;
	}

	@Override
	public ZonedDateTime convertFrom(Date source, ZonedDateTime destination) {
		if(source != null) {
			return ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
		}
		return null;
	}

}

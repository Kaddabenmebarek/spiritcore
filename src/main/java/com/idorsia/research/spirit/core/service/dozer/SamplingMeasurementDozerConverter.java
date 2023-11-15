package com.idorsia.research.spirit.core.service.dozer;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idorsia.research.spirit.core.conf.ContextShare;
import com.idorsia.research.spirit.core.dto.SamplingMeasurementDto;
import com.idorsia.research.spirit.core.service.SamplingMeasurementService;

@Service
public class SamplingMeasurementDozerConverter extends DozerConverter<SamplingMeasurementDto, Integer> {

	@Autowired
	private SamplingMeasurementService samplingMeasurementService = (SamplingMeasurementService) ContextShare
			.getContext().getBean("samplingMeasurementService");

	public SamplingMeasurementDozerConverter() {
		super(SamplingMeasurementDto.class, Integer.class);
	}

	@Override
	public Integer convertTo(SamplingMeasurementDto source, Integer destination) {
		if (source != null) {
			return source.getId();
		}
		return null;
	}

	@Override
	public SamplingMeasurementDto convertFrom(Integer source, SamplingMeasurementDto destination) {
		if (source != null) {
			return samplingMeasurementService.getDto(source);
		}
		return null;
	}

}

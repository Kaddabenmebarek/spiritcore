package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.SamplingMeasurementAttribute;

public interface SamplingMeasurementAttributeDao {
	
	public SamplingMeasurementAttribute get(Integer id);
	
	public List<SamplingMeasurementAttribute> getByAssayAttribute(Integer assayAttributeId);

	public List<SamplingMeasurementAttribute> getBySamplingMeasurement(Integer samplingMeasurementId);

	public Integer saveOrUpdate(SamplingMeasurementAttribute samplingMeasurementAttribute);

	public int addSamplingMeasurementAttribute(SamplingMeasurementAttribute samplingMeasurementAttribute);

	public void delete(Integer id);

	public void delete(List<Integer> ids);
}

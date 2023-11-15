package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.SamplingMeasurement;

public interface SamplingMeasurementDao {

	public SamplingMeasurement get(Integer id);
	
	public List<SamplingMeasurement> getSamplingMeasurementByTest(int testId);
	
	public List<SamplingMeasurement> getSamplingMeasurementBySampling(int samplingId);
	public SamplingMeasurement getSamplingMeasurementById(int id);
	
	public List<SamplingMeasurement> getSamplingMeasurementsByAssay(Integer assayId);
	
	public List<SamplingMeasurement> getSamplingMeasurementsBySampling(Integer samplingId);
	
	public List<SamplingMeasurement> getSamplingMeasurementBySamplingAndAssay(Integer samplingId, Integer assayId);
	
	public List<SamplingMeasurement> list();

	public int getCount();
	
	public Integer saveOrUpdate(SamplingMeasurement samplingMeasurement);

	public int addSamplingMeasurement(SamplingMeasurement samplingMeasurement);
	
	public void delete(int samplingMeasurementId);

	public void delete(List<Integer> ids);
	
}

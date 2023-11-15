package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.SamplingParameter;

public interface SamplingParameterDao {
	
	public SamplingParameter get(Integer id);
	
	public List<SamplingParameter> getSamplingParameterByBiotypeMetadata(int biotypeMetadataId);
	
	public List<SamplingParameter> getSamplingParameterBySampling(int samplingId);
	
	public List<SamplingParameter> list();

	public int getCount();
	
	public Integer saveOrUpdate(SamplingParameter samplingParameter);

	public int addSamplingParameter(SamplingParameter samplingParameter);
	
	public void delete(int samplingParameterId);

	public void delete(List<Integer> ids);

}

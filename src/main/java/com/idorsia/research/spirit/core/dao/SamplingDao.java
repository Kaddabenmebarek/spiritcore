package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.Sampling;

public interface SamplingDao {

	public Sampling get(Integer id);
	
	public List<Sampling> getByNamedSampling(Integer namedSamplingId);
	
	public List<Sampling> getByParent(Integer parentId);

	public List<Sampling> list();

	public int getCount();
	
	public Integer saveOrUpdate(Sampling sampling);

	public int addSampling(Sampling sampling);
	
	public void delete(int samplingId);
}

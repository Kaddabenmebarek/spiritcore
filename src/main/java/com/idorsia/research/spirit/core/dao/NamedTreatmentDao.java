package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.NamedTreatment;


public interface NamedTreatmentDao {

	public NamedTreatment get(Integer id);
	
	public List<NamedTreatment> list();

	public int getCount();
	
	public Integer saveOrUpdate(NamedTreatment namedTreatment);

	public int addNamedTreatment(NamedTreatment namedTreatment);
	
	public void delete(int namedTreatmentId);

	public List<NamedTreatment> getNamedTreatmentsByStudy(Integer studyId);
	
}

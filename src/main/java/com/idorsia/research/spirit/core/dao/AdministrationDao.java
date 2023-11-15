package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.Administration;

public interface AdministrationDao {

	public Administration get(Integer id);
	
	public List<Administration> list();
	
	public List<Administration> getByNamedTreatment(int namedTreatmentId);
	
	public List<Administration> getByBiosample(int biosampleId);
	
	public List<Administration> getByPhase(int phaseId);
	
	public List<Administration> getByStudyId(Integer studyId);
	
	public List<Administration> getByBiosamplesAndPhase(List<Integer> biosampleId, Integer phaseId);

	public int getCount();
	
	public int addAdministration(Administration administration);

	public Integer saveOrUpdate(Administration administration);
	
	public void delete(int administrationId);

	public void deleteAllByStudy(int studyId);

}

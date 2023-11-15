package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.PlannedSample;

public interface PlannedSampleDao {
	
	public PlannedSample getPlannedSampleById(int id);
	
	public PlannedSample get(Integer id);
	
	public List<PlannedSample> getPlannedSamplesByStage(Integer stageId);
	
	public List<PlannedSample> getPlannedSamplesByBiosample(Integer biosampleId);
	
	public PlannedSample getPlannedSampleByStageAndBiosample(Integer stageId, Integer biosampleId);
	
	public Integer saveOrUpdate(PlannedSample plannedSample);

	public int addPlannedSample(PlannedSample plannedSample);
	
	public void delete(int plannedSampleId);

}

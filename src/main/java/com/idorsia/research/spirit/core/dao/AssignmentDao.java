package com.idorsia.research.spirit.core.dao;

import java.util.List;
import java.util.Map;

import com.idorsia.research.spirit.core.model.Assignment;

public interface AssignmentDao {

	public Assignment get(Integer id);
	
	public Assignment getAssignmentById(int id);
	
	public Assignment getAssignmentByBiosampleAndStage(Integer biosampleId, Integer stageId);
	
	public Assignment getAssignmentByBiosampleAndSubgroup(Integer BiosampleId, Integer subgroupId);
	
	public List<Assignment> getAssignmentsByStage(Integer stageId);
	
	public List<Assignment> getAssignmentsByStudy(Integer studyId);
	
	public List<Assignment> getAssignmentsByBiosample(Integer biosampleId);
	
	public List<Assignment> getAssignmentsBySubgroup(Integer subgroupId);
	
	public int getCount();
	
	public Integer saveOrUpdate(Assignment assay);

	public int addAssignment(Assignment assay);
	
	public void delete(int assayId);

	public List<Assignment> getAssignmentsByStages(List<Integer> stageIds);

	public Map<Integer, Integer> getBiosampleSubgroupMap(List<Integer> stageIds);
}

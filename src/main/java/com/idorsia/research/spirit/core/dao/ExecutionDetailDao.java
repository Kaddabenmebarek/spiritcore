package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.ExecutionDetail;

public interface ExecutionDetailDao {

	public ExecutionDetail get(Integer id);
	
	public ExecutionDetail getByPhaseAndAssignment(int phaseLinkId, int assignmentId);
	
	public List<ExecutionDetail> list();

	public int getCount();
	
	public int addExecutionDetail(ExecutionDetail executionDetail);

	public Integer saveOrUpdate(ExecutionDetail executionDetail);
	
	public void delete(int executionDetailId);

	public List<ExecutionDetail> getBySchedulePhase(Integer schedulePhaseId);

	public List<ExecutionDetail> getByAssignment(Integer assignmentId);

	public List<ExecutionDetail> getByStudyId(Integer studyId);
	
}

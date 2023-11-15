package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.ResultAssignment;

public interface ResultAssignmentDao {

	public ResultAssignment get(Integer id);
	
	public ResultAssignment getResultAssignmentById(int id);
	
	public List<ResultAssignment> getResultAssignmentByAssignment(Integer assignmentId);
	
	public List<ResultAssignment> getResultAssignmentByResult(Integer resultId);

	public ResultAssignment getResultAssignmentbyAssignmentAndAssay(Integer assignmentId, Integer assayId);

	public Integer saveOrUpdate(ResultAssignment resultAssignment);

	public int addResultAssignment(ResultAssignment resultAssignment);
	
	public void delete(int resultAssignmentId);
}

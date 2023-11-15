package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.AssignmentPattern;

public interface AssignmentPatternDao {

	public AssignmentPattern get(Integer id);
	
	public List<AssignmentPattern> getByAssignment(int assignmentId);
	
	public List<AssignmentPattern> list();

	public int getCount();
	
	public Integer saveOrUpdate(AssignmentPattern assignmentPattern);

	public int addAssignmentPattern(AssignmentPattern assignmentPattern);
	
	public void delete(int assignmentPatternId);
	
	public void deleteByAssignment(int assignmentId);

	public void deleteByActionPattern(int assignmentPatternId);

	public List<Integer> getByAssignments(List<Integer> assignmentIds);
}

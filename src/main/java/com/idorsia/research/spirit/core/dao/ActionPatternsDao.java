package com.idorsia.research.spirit.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.idorsia.research.spirit.core.model.ActionPatterns;

public interface ActionPatternsDao {

	public ActionPatterns get(Integer id);
	
	public List<ActionPatterns> getByIds(List<Integer> actionPatternsIds);
	
	public List<ActionPatterns> getByAssignment(Integer assignmentId);

	public List<ActionPatterns> getBySubGroup(Integer subGroupId);

	public List<ActionPatterns> getByGroup(Integer groupId);
	
	public List<ActionPatterns> getByAction(Integer actionId, String type);

	public List<ActionPatterns> list();

	public int getCount();
	
	public Integer saveOrUpdate(ActionPatterns actionPattern);

	public int addActionPattern(ActionPatterns actionPattern);
	
	public void delete(int actionPatternId);
	
	public List<ActionPatterns> getStageDefinition(Integer stageId);
	
	public List<ActionPatterns> getActionTypeByStage(Integer stageId);
	
	public List<ActionPatterns> getActionPatternsByParent(Integer parentId);
	
	public ActionPatterns getActionTypeBySchdule(Integer id);

	public Set<ActionPatterns> getBySubGoupsAndSchedules(Set<Integer> subGroupIds, Set<Integer> scheduleIds);

	public Map<Integer, List<Integer>> getActionsPhasesMap(List<Integer> stageIds);

	public Map<Integer, Integer> getActionsPhasesMap(Integer actionpatternId);

	public String getActionTypeByActionId(Integer id);
}

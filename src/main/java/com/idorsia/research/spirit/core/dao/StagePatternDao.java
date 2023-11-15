package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.StagePattern;

public interface StagePatternDao {

	public StagePattern get(Integer id);
	
	public StagePattern getByActionPattern(int actionPatternId);
	
	public List<StagePattern> getByStage(int stageId);
	
	public List<StagePattern> list();

	public int getCount();
	
	public Integer saveOrUpdate(StagePattern stagePattern);

	public int addStagePattern(StagePattern stagePattern);
	
	public void delete(int stagePatternId);
	
	public void deleteByStage(int stageId);

	public void deleteByActionPattern(int stagePatternId);

	public List<Integer> getByStages(List<Integer> stageIds);
}

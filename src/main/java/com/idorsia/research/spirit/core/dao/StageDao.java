package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.Stage;

public interface StageDao {

	public Stage get(Integer id);
	
	public Stage getStageById(int id);
	
	public List<Stage> getStagesByStudyId(Integer studyId);
	
	public void changeOwner(Stage stage, String updUser, String creUser);

	public List<Stage> list();

	public int getCount();
	
	public Integer saveOrUpdate(Stage stage);

	public int addStage(Stage stage);
	
	public void delete(int stageId);

	public List<Stage> getStagesByStudyIdentifier(String studyIdentifier);

	public Stage getBySchedule(int scheduleId);

}

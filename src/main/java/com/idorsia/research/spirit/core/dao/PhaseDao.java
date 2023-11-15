package com.idorsia.research.spirit.core.dao;

import java.util.List;
import java.util.Set;

import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.model.Stage;

public interface PhaseDao {

	public Phase get(Integer id);
	
	public List<Phase> list();

	public int getCount();
	
	public Integer saveOrUpdate(Phase phase);

	public int addPhase(Phase phase);
	
	public void delete(int phaseId);

	public List<Phase> getPhasesByStage(Integer stageId);

	public List<Phase> getPhasesByStages(List<Stage> stages);
	
	public List<Phase> getPhasesByStudyId(Integer studyId);
	
	public List<Phase> getPhasesByStudy(int studyId);

	public Set<Integer> getBySchedule(Set<Integer> matchingSchedules);

	public Phase getByStageAndDuration(double phaseDuration, Integer stageId);
}

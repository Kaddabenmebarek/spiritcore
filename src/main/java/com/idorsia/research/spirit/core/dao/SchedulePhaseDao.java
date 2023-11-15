package com.idorsia.research.spirit.core.dao;

import java.util.List;
import java.util.Set;

import com.idorsia.research.spirit.core.model.SchedulePhase;

public interface SchedulePhaseDao {

	public SchedulePhase get(Integer id);

	public List<SchedulePhase> getByPhase(int phaseId);
	
	public List<SchedulePhase> getBySchedule(int scheduleId);
	
	public List<SchedulePhase> list();

	public int getCount();

	public Integer saveOrUpdate(SchedulePhase schedulePhase);

	public int addSchedulePhase(SchedulePhase schedulePhase);

	public void delete(int schedulePhaseId);

	public List<SchedulePhase> getBySchedules(Set<Integer> scheduleIds);

}

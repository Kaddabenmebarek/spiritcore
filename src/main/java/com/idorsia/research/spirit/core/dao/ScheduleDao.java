package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.model.Phase;
import com.idorsia.research.spirit.core.model.Schedule;

public interface ScheduleDao {

	public Schedule get(Integer id);

	public List<Schedule> list();

	public int getCount();

	public Integer saveOrUpdate(Schedule schedule);

	public int addSchedule(Schedule schedule);

	public void delete(int scheduleId);

	public List<Schedule> getSchedulesByPhases(List<Phase> phases);

	public List<Schedule> getByStudy(Integer studyId);

	public List<Schedule> getSchedulesByStages(List<Integer> stageIds);

}
